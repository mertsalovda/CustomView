package ru.mertsalovda.customview.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mertsalovda.customview.R


class PaintPickerDialog(private val listener: PaintPickerDialogListener) : DialogFragment(), ColorRecyclerAdapter.OnClockItem {

    private lateinit var rvColors: RecyclerView
    private lateinit var tvStrokeWidth: TextView
    private lateinit var sbStrokeWidth: SeekBar

    private var mColorResult = Color.BLACK
    private var mStrokeWidthResult = 20

    companion object{
        fun newInstance(listener: PaintPickerDialogListener) = PaintPickerDialog(listener)
    }

    interface PaintPickerDialogListener{
        fun onResultOk(color: Int, strokeWidth: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val view = LayoutInflater.from(activity).inflate(R.layout.paint_picker, null, false)

        rvColors = view.findViewById(R.id.colorRecycler)
        tvStrokeWidth = view.findViewById(R.id.tvStrokeWidth)
        sbStrokeWidth = view.findViewById(R.id.sbStrokeWidth)

        tvStrokeWidth.text = sbStrokeWidth.progress.toString()

        builder.setView(view)
        builder.setMessage("Выберете цвет и ширину линии")
            .setPositiveButton("OK") { _, _ ->
                listener.onResultOk(mColorResult, mStrokeWidthResult)
                onCancel()
            }
            .setNegativeButton("Отмена") { _, _ ->
                onCancel()
            }
        val list = resources.getIntArray(R.array.colors).toMutableList()
        rvColors.adapter = ColorRecyclerAdapter(list, this)
        rvColors.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        sbStrokeWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvStrokeWidth.text = progress.toString()
                mStrokeWidthResult = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        return builder.create()
    }

    override fun onClickItem(color: Int) {
        mColorResult = color
    }

    fun onCancel() {
        this.dismiss()
    }
}
package ru.mertsalovda.customview

import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.ac_drawing.*
import ru.mertsalovda.customview.dialog.PaintPickerDialog

class DrawingActivity : AppCompatActivity(), PaintPickerDialog.PaintPickerDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_drawing)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuClear -> {
                drawingView.clear()
                true
            }
            R.id.menuRevert -> {
                drawingView.removeLast()
                true
            }
            R.id.menuBrush -> {
                val dialog = PaintPickerDialog.newInstance(this)
                dialog.show(supportFragmentManager, "picker")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResultOk(color: Int, strokeWidth: Int) {
        drawingView.setPaint(Paint().apply {
            this.color = color
            this.strokeWidth = strokeWidth.toFloat()
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        })
    }
}
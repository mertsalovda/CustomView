package ru.mertsalovda.customview.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ru.mertsalovda.customview.R

class ColorRecyclerAdapter(
    private val listColors: MutableList<Int>,
    private var listener: OnClockItem?
) : RecyclerView.Adapter<ColorRecyclerAdapter.ColorHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorHolder(view)
    }

    override fun getItemCount() = listColors.size

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.bind(listColors[position], listener)
    }

    fun setListener(listener: OnClockItem) {
        this.listener = listener
    }

    interface OnClockItem {
        fun onClickItem(color: Int)
    }

    class ColorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var ivColor: ImageView

        fun bind(color: Int, listener: OnClockItem?) {
            ivColor = itemView.findViewById(R.id.ivColor)
            ivColor.setBackgroundColor(color)
            ivColor.setOnClickListener { listener?.let { it.onClickItem(color) } }
        }

    }
}

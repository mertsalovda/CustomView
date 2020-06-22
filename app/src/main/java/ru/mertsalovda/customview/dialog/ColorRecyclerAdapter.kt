package ru.mertsalovda.customview.dialog

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ru.mertsalovda.customview.R

class ColorRecyclerAdapter(
    private val listColors: MutableList<Int>,
    private var listener: OnClockItem?
) : RecyclerView.Adapter<ColorRecyclerAdapter.ColorHolder>() {

    private var mPositionSelected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorHolder(view)
    }

    override fun getItemCount() = listColors.size

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.bind(listColors[position], listener)

        holder.itemView.findViewById<CardView>(R.id.layoutItem)
            .setCardBackgroundColor(if (mPositionSelected == position) Color.LTGRAY else Color.WHITE)
    }

    interface OnClockItem {
        fun onClickItem(color: Int)
    }

    inner class ColorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var ivColor: ImageView

        fun bind(color: Int, listener: OnClockItem?) {
            ivColor = itemView.findViewById(R.id.ivColor)
            ivColor.setBackgroundColor(color)
            itemView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                notifyItemChanged(mPositionSelected)
                mPositionSelected = adapterPosition
                notifyItemChanged(mPositionSelected)
                listener?.let { it.onClickItem(color) }
            }
        }

    }
}

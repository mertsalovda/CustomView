package ru.mertsalovda.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counter1.setOnClickListener {
            increment(it)
        }
        counter2.setOnClickListener {
            increment(it)
        }
        counter3.setOnClickListener {
            increment(it)
        }
    }

    private fun increment(view: View){
        val indicatorView = view as IndicatorView
        val max = indicatorView.getMaxValue()
        val value = indicatorView.getValue()

        if (value + 1 > max) {
            indicatorView.setValue(0)
        } else {
            indicatorView.setValue(value + 1)
        }
    }
}
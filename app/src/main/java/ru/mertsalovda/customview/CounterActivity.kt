package ru.mertsalovda.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.ac_counter.*

class CounterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_counter)

        btnPlus.setOnClickListener {
            counter.setValue(counter.getValue() + 1)
        }
        btnMinus.setOnClickListener {
            counter.setValue(counter.getValue() - 1)
        }
    }
}
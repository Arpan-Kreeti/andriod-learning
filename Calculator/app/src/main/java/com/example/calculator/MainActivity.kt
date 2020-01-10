package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
    }

    private fun setListeners() {

        val clickableViews: List<View> =
            listOf(button_0,button_1,button_2,button_3,button_4,button_5,button_6,button_7,
                button_8,button_9,button_c,button_divide,button_enter,button_minus,
                button_multiply,button_plus)

        for (item in clickableViews) {
            item.setOnClickListener { setText(it)}
        }
    }

    private fun setText(view: View) {

        when (view.id) {

            R.id.button_0 -> displayPanel.text = appendChar(displayPanel.text, "0")
            R.id.button_1 -> displayPanel.text = appendChar(displayPanel.text, "1")
            R.id.button_2 -> displayPanel.text = appendChar(displayPanel.text, "2")
            R.id.button_3 -> displayPanel.text = appendChar(displayPanel.text, "3")
            R.id.button_4 -> displayPanel.text = appendChar(displayPanel.text, "4")
            R.id.button_5 -> displayPanel.text = appendChar(displayPanel.text, "5")
            R.id.button_6 -> displayPanel.text = appendChar(displayPanel.text, "6")
            R.id.button_7 -> displayPanel.text = appendChar(displayPanel.text, "7")
            R.id.button_8 -> displayPanel.text = appendChar(displayPanel.text, "8")
            R.id.button_9 -> displayPanel.text = appendChar(displayPanel.text, "9")
            R.id.button_plus -> displayPanel.text = appendChar(displayPanel.text, "+")
            R.id.button_minus -> displayPanel.text = appendChar(displayPanel.text, "-")
            R.id.button_multiply -> displayPanel.text = appendChar(displayPanel.text, "X")
            R.id.button_divide -> displayPanel.text = appendChar(displayPanel.text, "/")
            R.id.button_c-> displayPanel.text = displayPanel.text.dropLast(1)
            R.id.button_enter -> displayPanel.text = "Enter"

        }

    }

    private fun appendChar(ch: CharSequence, symbol: String): String {
        var str = ch.toString()

        return "$str$symbol"

    }

}





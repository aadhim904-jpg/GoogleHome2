
package com.aadhim904.googlehome2

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class HomeActivity : Activity() {

    private lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.rgb(12, 16, 28))
            setPadding(28, 28, 28, 28)
        }

        val title = TextView(this).apply {
            text = "Google TV"
            textSize = 34f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Welcome to your home"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        setContentView(root)
    }
}

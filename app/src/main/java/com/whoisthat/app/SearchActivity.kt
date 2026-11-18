package com.whoisthat.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private var selectedNetwork: String = "جميع الشبكات"
    private var selectedChip: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val input = findViewById<EditText>(R.id.phoneNumberInput)
        val button = findViewById<android.widget.Button>(R.id.searchButton)

        // network chips
        val container = findViewById<LinearLayout>(R.id.chipContainer)
        setupChips(container)

        button.setOnClickListener {
            val q = input.text.toString().trim()
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("search_type", "number")
            intent.putExtra("query", q)
            intent.putExtra("network", selectedNetwork)
            startActivity(intent)
        }

        // contact shortcut via logo long click
        findViewById<android.widget.ImageView>(R.id.appLogo).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }
    }

    private fun setupChips(container: LinearLayout?) {
        if (container == null) return
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is TextView) {
                child.setOnClickListener {
                    selectedNetwork = child.text.toString()
                    selectedChip?.setBackgroundColor(Color.TRANSPARENT)
                    selectedChip = child
                    child.setBackgroundResource(R.drawable.bg_network_chip)
                }
                if (i == 0) {
                    selectedChip = child
                }
            }
        }
    }
}
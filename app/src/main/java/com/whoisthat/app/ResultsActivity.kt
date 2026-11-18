package com.whoisthat.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResultsActivity : Activity() {

    private var selectedNetwork: String = "جميع الشبكات"
    private var selectedChip: TextView? = null
    private lateinit var adapter: ResultAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var nameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        recycler = findViewById(R.id.resultsRecycler)
        nameInput = findViewById(R.id.nameSearchInput)
        adapter = ResultAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val container = findViewById<LinearLayout>(R.id.chipContainer)
        setupChips(container)

        // handle incoming number search
        val type = intent.getStringExtra("search_type")
        val query = intent.getStringExtra("query").orEmpty()
        val net = intent.getStringExtra("network")
        if (net != null) selectedNetwork = net
        if (type == "number") {
            nameInput.setText(query)
            performNumberSearch(query, selectedNetwork)
        } else {
            performNameSearch("", selectedNetwork)
        }

        nameInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performNameSearch(s?.toString().orEmpty(), selectedNetwork)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // navigation to contact screen available via menu if needed
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
                    performNameSearch(nameInput.text.toString(), selectedNetwork)
                }
                if (i == 0) selectedChip = child
            }
        }
    }

    private fun performNumberSearch(q: String, net: String) {
        adapter.submit(ContactsRepository.searchByNumber(q, net))
    }

    private fun performNameSearch(q: String, net: String) {
        adapter.submit(ContactsRepository.searchByName(q, net))
    }
}
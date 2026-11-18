package com.whoisthat.app

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : Activity() {

    private var selectedNumberNetwork: String = "جميع الشبكات"
    private var selectedNameNetwork: String = "جميع الشبكات"

    private lateinit var tabNumber: View
    private lateinit var tabName: View
    private lateinit var tabContact: View

    private lateinit var numberAdapter: ResultAdapter
    private lateinit var nameAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabNumber = findViewById(R.id.tabNumber)
        tabName = findViewById(R.id.tabName)
        tabContact = findViewById(R.id.tabContact)

        setupBottomNav()
        setupNumberTab()
        setupNameTab()
        showTab(0)
    }

    private fun setupBottomNav() {
        findViewById<View>(R.id.navNumber).setOnClickListener { showTab(0) }
        findViewById<View>(R.id.navName).setOnClickListener { showTab(1) }
        findViewById<View>(R.id.navContact).setOnClickListener { showTab(2) }
    }

    private fun showTab(index: Int) {
        tabNumber.visibility = if (index == 0) View.VISIBLE else View.GONE
        tabName.visibility = if (index == 1) View.VISIBLE else View.GONE
        tabContact.visibility = if (index == 2) View.VISIBLE else View.GONE

        val activeColor = resources.getColor(R.color.brand_primary)
        val inactiveColor = resources.getColor(R.color.text_secondary)

        fun setTabColors(active: Boolean, icon: ImageView, label: TextView) {
            val c = if (active) activeColor else inactiveColor
            icon.setColorFilter(c)
            label.setTextColor(c)
        }

        setTabColors(index == 0, findViewById(R.id.navNumberIcon), findViewById(R.id.navNumberLabel))
        setTabColors(index == 1, findViewById(R.id.navNameIcon), findViewById(R.id.navNameLabel))
        setTabColors(index == 2, findViewById(R.id.navContactIcon), findViewById(R.id.navContactLabel))
    }

    private fun setupNumberTab() {
        val input = findViewById<EditText>(R.id.numberPhoneInput)
        val button = findViewById<Button>(R.id.numberSearchButton)
        val emptyText = findViewById<TextView>(R.id.numberEmptyText)
        val recycler = findViewById<RecyclerView>(R.id.numberResultsRecycler)
        val chipContainer = findViewById<LinearLayout>(R.id.numberChipContainer)

        numberAdapter = ResultAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = numberAdapter
        recycler.isNestedScrollingEnabled = false

        setupChipContainer(chipContainer) { network ->
            selectedNumberNetwork = network
        }

        button.setOnClickListener {
            val q = input.text.toString().trim()
            if (q.isEmpty()) {
                numberAdapter.submit(emptyList())
                emptyText.text = "الرجاء إدخال رقم للبحث"
                return@setOnClickListener
            }
            val results = ContactsRepository.searchByNumber(q, selectedNumberNetwork)
            numberAdapter.submit(results)
            emptyText.text = if (results.isEmpty()) "لا توجد نتائج" else "عدد النتائج: ${results.size}"
        }
    }

    private fun setupNameTab() {
        val input = findViewById<EditText>(R.id.nameSearchInput)
        val button = findViewById<Button>(R.id.nameSearchButton)
        val emptyText = findViewById<TextView>(R.id.nameEmptyText)
        val recycler = findViewById<RecyclerView>(R.id.nameResultsRecycler)
        val chipContainer = findViewById<LinearLayout>(R.id.nameChipContainer)

        nameAdapter = ResultAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = nameAdapter
        recycler.isNestedScrollingEnabled = false

        nameAdapter.submit(emptyList())
        emptyText.text = "أدخل اسمًا للبحث ثم اضغط بحث"

        fun doSearch() {
            val q = input.text.toString().trim()
            if (q.isEmpty()) {
                nameAdapter.submit(emptyList())
                emptyText.text = "الرجاء إدخال اسم للبحث"
                return
            }
            val results = ContactsRepository.searchByName(q, selectedNameNetwork)
            nameAdapter.submit(results)
            emptyText.text = if (results.isEmpty()) "لا توجد نتائج" else "عدد النتائج: ${results.size}"
        }

        setupChipContainer(chipContainer) { network ->
            selectedNameNetwork = network
            val q = input.text.toString().trim()
            if (q.isNotEmpty()) {
                val results = ContactsRepository.searchByName(q, selectedNameNetwork)
                nameAdapter.submit(results)
                emptyText.text = if (results.isEmpty()) "لا توجد نتائج" else "عدد النتائج: ${results.size}"
            }
        }

        button.setOnClickListener { doSearch() }
    }

    private fun setupChipContainer(container: LinearLayout, onSelect: (String) -> Unit) {
        var selected: TextView? = null
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is TextView) {
                if (i == 0) {
                    selected = child
                    highlightChip(child, true)
                } else {
                    highlightChip(child, false)
                }
                child.setOnClickListener {
                    selected?.let { highlightChip(it, false) }
                    selected = child
                    highlightChip(child, true)
                    onSelect(child.text.toString())
                }
            }
        }
    }

    private fun highlightChip(chip: TextView, selected: Boolean) {
        if (selected) {
            chip.setBackgroundColor(resources.getColor(R.color.brand_primary))
            chip.setTextColor(resources.getColor(R.color.text_on_primary))
        } else {
            chip.setBackgroundResource(R.drawable.bg_network_chip)
            // restore original text color? chips in layout have different colors per network; easier to set a neutral
            chip.setTextColor(resources.getColor(R.color.brand_primary))
        }
    }
}
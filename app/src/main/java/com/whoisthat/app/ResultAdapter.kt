package com.whoisthat.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultAdapter : RecyclerView.Adapter<ResultAdapter.VH>() {

    private val items = mutableListOf<ContactEntry>()

    fun submit(list: List<ContactEntry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.name.text = e.name
        holder.number.text = e.number
        holder.sim.text = e.network.take(3)
        // color sim chip by network
        val color = when (e.network) {
            "يمن موبايل" -> R.color.carrier_yemen_mobile
            "YOU" -> R.color.carrier_you
            "سبأفون" -> R.color.carrier_sabafon
            "واي" -> R.color.carrier_way
            "يمن نت" -> R.color.carrier_yemennet
            else -> R.color.brand_primary
        }
        holder.sim.setBackgroundResource(color)
    }

    override fun getItemCount(): Int = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.resultName) ?: run {
            // fallback: find by index
            v.findViewById(android.R.id.text1)
        }
        val number: TextView = v.findViewById(R.id.resultNumber) ?: v.findViewById(android.R.id.text2)
        val sim: TextView = v.findViewById(R.id.resultSim) ?: v.findViewById(android.R.id.icon)
    }
}
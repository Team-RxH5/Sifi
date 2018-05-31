package com.anagramsoftware.sifi.ui.use

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.`interface`.ItemClickListener
import com.anagramsoftware.sifi.data.model.Hotspot

class ResultAdapter: RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private var dataSet = ArrayList<Hotspot>()
    var listener: ItemClickListener? = null

    fun accept(items: List<Hotspot>) {
        dataSet = items as ArrayList<Hotspot>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_result, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size
    fun getItem(position: Int): Hotspot = dataSet[position]

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val ssid = itemView.findViewById<TextView>(R.id.ssid_tv)

        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener?.onItemClick(position)
            }
        }

        fun bind(item: Hotspot) {
            ssid.text = item.SSID
        }
    }

}
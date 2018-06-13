package com.anagramsoftware.sifi.ui.selectnetwork

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.`interface`.ItemClickListener
import com.anagramsoftware.sifi.data.model.Hotspot

class ResultAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

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

    inner class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.name_tv)
        private val level = itemView.findViewById<ImageView>(R.id.level_iv)

        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION)
                    listener?.onItemClick(position)
            }
        }

        fun bind(item: Hotspot) {
            name.text = item.name
            when (item.level) {
                0 -> level.setImageResource(R.drawable.ic_signal_wifi_0_bar_black_24dp)
                1 -> level.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_24dp)
                2 -> level.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_24dp)
                3 -> level.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_24dp)
                4 -> level.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp)
            }
        }
    }

}
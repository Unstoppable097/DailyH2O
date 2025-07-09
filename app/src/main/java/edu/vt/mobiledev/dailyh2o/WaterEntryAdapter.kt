package edu.vt.mobiledev.dailyh2o

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterEntryAdapter : ListAdapter<WaterEntry, WaterEntryAdapter.WaterEntryViewHolder>(WaterEntryDiffCallback()) {

    interface OnItemLongClickListener {
        fun onItemLongClick(waterEntry: WaterEntry)
    }

    private var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.onItemLongClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_entry, parent, false)
        return WaterEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: WaterEntryViewHolder, position: Int) {
        val waterEntry = getItem(position)
        holder.bind(waterEntry, onItemLongClickListener)
    }

    class WaterEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_entry_amount)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_entry_time)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_entry_date)

        fun bind(waterEntry: WaterEntry, listener: OnItemLongClickListener?) {
            tvAmount.text = "${waterEntry.amount.toInt()} oz"
            val date = Date(waterEntry.timestamp)
            tvTime.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            tvDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(waterEntry)
                true
            }
        }
    }

    private class WaterEntryDiffCallback : DiffUtil.ItemCallback<WaterEntry>() {
        override fun areItemsTheSame(oldItem: WaterEntry, newItem: WaterEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WaterEntry, newItem: WaterEntry): Boolean {
            return oldItem == newItem
        }
    }
}
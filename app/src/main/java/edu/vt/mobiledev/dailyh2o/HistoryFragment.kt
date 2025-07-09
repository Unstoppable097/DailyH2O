package edu.vt.mobiledev.dailyh2o

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment(), WaterEntryAdapter.OnItemLongClickListener {

    private val waterViewModel: WaterViewModel by viewModels()
    private lateinit var waterEntryAdapter: WaterEntryAdapter
    private lateinit var tvNoEntries: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_water_entries)
        tvNoEntries = view.findViewById(R.id.tv_no_entries)

        waterEntryAdapter = WaterEntryAdapter()
        recyclerView.adapter = waterEntryAdapter

        waterEntryAdapter.setOnItemLongClickListener(this)

        viewLifecycleOwner.lifecycleScope.launch {
            waterViewModel.allWaterEntries.collectLatest { entries ->
                waterEntryAdapter.submitList(entries)
                if (entries.isEmpty()) {
                    tvNoEntries.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvNoEntries.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onItemLongClick(waterEntry: WaterEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this ${waterEntry.amount.toInt()} oz entry from ${SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(Date(waterEntry.timestamp))}?")
            .setPositiveButton("Delete") { dialog: DialogInterface, _: Int ->
                waterViewModel.deleteWaterEntry(waterEntry)
                Toast.makeText(context, "Entry deleted.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            .show()
    }
}
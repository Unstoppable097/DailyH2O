package edu.vt.mobiledev.dailyh2o

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class LoggingFragment : Fragment() {

    private val waterViewModel: WaterViewModel by viewModels()

    private lateinit var progressBar: ProgressBar
    private lateinit var tvCurrentGoal: TextView
    private lateinit var etCustomAmount: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logging, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progress_bar_hydration)
        tvCurrentGoal = view.findViewById(R.id.tv_current_goal)
        etCustomAmount = view.findViewById(R.id.et_custom_amount)

        viewLifecycleOwner.lifecycleScope.launch {
            waterViewModel.dailyGoal.collectLatest { goal ->
                progressBar.max = if (goal > 0) goal.toInt() else 100
                val currentTotalWater = waterViewModel.currentDayTotalWater.value
                tvCurrentGoal.text = "${currentTotalWater.toInt()} oz / ${goal.toInt()} oz"

                if (currentTotalWater >= goal && currentTotalWater > 0 && goal > 0) {
                    Toast.makeText(context, "Goal Reached! Keep going!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            waterViewModel.currentDayTotalWater.collectLatest { totalWater ->
                progressBar.progress = totalWater.toInt()
                val currentGoal = waterViewModel.dailyGoal.value
                tvCurrentGoal.text = "${totalWater.toInt()} oz / ${currentGoal.toInt()} oz"

                if (totalWater >= currentGoal && totalWater > 0 && currentGoal > 0) {
                }
            }
        }

        view.findViewById<Button>(R.id.btn_add_8oz).setOnClickListener {
            waterViewModel.addWater(8f)
        }
        view.findViewById<Button>(R.id.btn_add_16oz).setOnClickListener {
            waterViewModel.addWater(16f)
        }
        view.findViewById<Button>(R.id.btn_add_24oz).setOnClickListener {
            waterViewModel.addWater(24f)
        }

        view.findViewById<Button>(R.id.btn_add_custom).setOnClickListener {
            val amountStr = etCustomAmount.text.toString()
            if (amountStr.isNotBlank()) {
                val amount = amountStr.toFloatOrNull()
                if (amount != null && amount > 0) {
                    waterViewModel.addWater(amount)
                    etCustomAmount.text.clear()
                } else {
                    Toast.makeText(context, "Please enter a valid amount (e.g., 10.5).", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Custom amount cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        val btnOpenSettings = view.findViewById<Button>(R.id.btn_open_settings)
        btnOpenSettings.setOnClickListener {
            findNavController().navigate(R.id.action_loggingFragment_to_settingsFragment)
        }

        val btnOpenHistory = view.findViewById<Button>(R.id.btn_open_history)
        btnOpenHistory.setOnClickListener {
            findNavController().navigate(R.id.action_loggingFragment_to_historyFragment)
        }
    }
}
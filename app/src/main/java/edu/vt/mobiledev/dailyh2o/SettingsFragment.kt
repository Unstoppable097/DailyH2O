package edu.vt.mobiledev.dailyh2o

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private val waterViewModel: WaterViewModel by viewModels()

    private lateinit var etWeight: EditText
    private lateinit var spinnerActivityLevel: Spinner
    private lateinit var btnSaveProfile: Button
    private lateinit var tvCalculatedGoal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etWeight = view.findViewById(R.id.et_weight)
        spinnerActivityLevel = view.findViewById(R.id.spinner_activity_level)
        btnSaveProfile = view.findViewById(R.id.btn_save_profile)
        tvCalculatedGoal = view.findViewById(R.id.tv_calculated_goal)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activity_levels_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerActivityLevel.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            waterViewModel.userProfile.collectLatest { profile ->
                profile?.let {
                    etWeight.setText(it.weight.toString())
                    val activityLevels = resources.getStringArray(R.array.activity_levels_array)
                    val index = activityLevels.indexOfFirst { level -> level.equals(it.activityLevel, ignoreCase = true) }
                    if (index != -1) {
                        spinnerActivityLevel.setSelection(index)
                    }
                    tvCalculatedGoal.text = "Calculated Daily Goal: ${it.dailyGoal.toInt()} oz"
                } ?: run {
                    tvCalculatedGoal.text = "Calculated Daily Goal: -- oz"
                }
            }
        }

        btnSaveProfile.setOnClickListener {
            val weightStr = etWeight.text.toString()
            val selectedActivityLevel = spinnerActivityLevel.selectedItem.toString()

            val weight = weightStr.toFloatOrNull()
            if (weight != null && weight > 0) {
                waterViewModel.saveUserProfile(weight, selectedActivityLevel)
                Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter a valid weight.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
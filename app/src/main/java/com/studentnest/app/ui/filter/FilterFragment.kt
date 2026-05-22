package com.studentnest.app.ui.filter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.studentnest.app.R
import com.studentnest.app.databinding.FragmentFilterBinding
import com.studentnest.app.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FilterFragment : Fragment() {
    private lateinit var binding: FragmentFilterBinding
    private var selectedDate: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLocationSpinner()
        setupDatePicker()
        setupClickListeners()
    }

    private fun setupLocationSpinner() {
        val locations = listOf(
            "All Areas",
            "Block 6",
            "Phase 2",
            "Tlokweng",
            "Broadhurst",
            "Main Mall",
            "Village",
            "Mogoditshane",
            "Kgale"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLocation.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)
                    selectedDate = selectedCalendar.timeInMillis
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    binding.tvSelectedDate.text = dateFormat.format(Date(selectedDate))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnApplyFilters.setOnClickListener {
            applyFilters()
        }

        binding.btnSavePreferences.setOnClickListener {
            savePreferences()
        }
    }

    private fun applyFilters() {
        val location = binding.spinnerLocation.selectedItem as String
        val maxPriceText = binding.etMaxPrice.text.toString().trim()
        val maxPrice = if (maxPriceText.isEmpty()) Double.MAX_VALUE else maxPriceText.toDoubleOrNull() ?: Double.MAX_VALUE

        val bundle = Bundle().apply {
            putString("location", location)
            putDouble("max_price", maxPrice)
            putLong("availability_date", selectedDate)
        }

        findNavController().previousBackStackEntry?.savedStateHandle?.set("filter_results", bundle)
        findNavController().popBackStack()
    }

    private fun savePreferences() {
        val prefs = requireActivity().getSharedPreferences("studentnest_prefs", 0)
        prefs.edit().apply {
            putString("filter_location", binding.spinnerLocation.selectedItem as String)
            putFloat("filter_max_price", binding.etMaxPrice.text.toString().toFloatOrNull() ?: Float.MAX_VALUE)
            putLong("filter_date", selectedDate)
        }.apply()

        Toast.makeText(requireContext(), "Preferences saved!", Toast.LENGTH_SHORT).show()
        NotificationHelper.showListingMatchNotification(
            requireContext(),
            "Your saved preferences are active!"
        )
    }
}

package com.st_louis.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.st_louis.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnAddStudent.setOnClickListener {
            Toast.makeText(context, "Navigating to Student Management", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnAddTeacher.setOnClickListener {
            Toast.makeText(context, "Navigating to Teacher Management", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnAddBursar.setOnClickListener {
            Toast.makeText(context, "Navigating to Bursar Management", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
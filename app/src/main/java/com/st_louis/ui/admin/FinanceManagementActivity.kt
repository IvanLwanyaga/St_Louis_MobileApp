package com.st_louis.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.st_louis.databinding.ActivityFinanceManagementBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinanceManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinanceManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinanceManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}

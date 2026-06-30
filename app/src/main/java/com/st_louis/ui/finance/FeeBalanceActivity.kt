package com.st_louis.ui.finance

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.st_louis.R
import com.st_louis.data.ApiClient
import com.st_louis.databinding.ActivityFeeBalanceBinding
import com.st_louis.models.FeeSummary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeeBalanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeeBalanceBinding
    private val viewModel: FeeBalanceViewModel by viewModels()
    private lateinit var adapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeeBalanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        // Load data for current student
        viewModel.loadFeeDetails("S001")
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { 
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = PaymentAdapter()
        binding.paymentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.paymentRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.feeSummary.observe(this) { summary ->
            updateSummaryUI(summary)
        }

        viewModel.paymentHistory.observe(this) { history ->
            adapter.submitList(history)
            binding.emptyStateView.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.loadingIndicator.visibility = if (isLoading && !binding.swipeRefresh.isRefreshing) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFeeDetails("S001")
        }
        
        // Customize color
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.primary_blue)
        )
    }

    private fun updateSummaryUI(summary: FeeSummary) {
        binding.totalDueValue.text = "UGX ${String.format("%,.0f", summary.totalDue)}"
        binding.totalPaidValue.text = "UGX ${String.format("%,.0f", summary.totalPaid)}"
        binding.balanceValue.text = "UGX ${String.format("%,.0f", summary.balance)}"

        when (summary.status.lowercase()) {
            "fully_paid" -> {
                binding.statusBadge.text = "Fully paid"
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_fully_paid)
                binding.statusBadge.setTextColor(ContextCompat.getColor(this, R.color.success))
            }
            "partial" -> {
                binding.statusBadge.text = "Partial"
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_partial)
                binding.statusBadge.setTextColor(ContextCompat.getColor(this, R.color.warning))
            }
            else -> {
                binding.statusBadge.text = "Unpaid"
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_unpaid)
                binding.statusBadge.setTextColor(ContextCompat.getColor(this, R.color.error))
            }
        }
    }
}

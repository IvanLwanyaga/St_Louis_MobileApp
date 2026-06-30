package com.st_louis.ui.finance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.databinding.ItemPaymentHistoryBinding
import com.st_louis.models.Payment
import java.text.NumberFormat
import java.util.Locale

class PaymentAdapter : ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PaymentViewHolder(private val binding: ItemPaymentHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(payment: Payment) {
            binding.paymentDate.text = payment.date
            binding.paymentMethod.text = payment.paymentMethod
            binding.receiptNumber.text = "Receipt #${payment.receiptNumber}"
            
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "UG"))
            // Locale UG doesn't always format perfectly as "UGX 100,000", might need custom string
            binding.paymentAmount.text = "UGX ${String.format("%,.0f", payment.amount)}"
        }
    }

    class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }
}

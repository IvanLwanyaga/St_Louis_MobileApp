package com.st_louis

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.st_louis.databinding.DialogResetSuccessBinding

class DialogResetSuccess(context: Context) {
    private val dialog = Dialog(context)
    private val binding: DialogResetSuccessBinding = DialogResetSuccessBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setCancelable(false)
    }

    fun show(
        title: String,
        message: String,
        onBackToLogin: () -> Unit,
        onResend: () -> Unit
    ) {
        binding.dialogTitle.text = title
        binding.dialogMessage.text = message
        
        binding.backToLoginButton.setOnClickListener {
            dialog.dismiss()
            onBackToLogin()
        }
        
        binding.resendText.setOnClickListener {
            onResend()
        }
        
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}

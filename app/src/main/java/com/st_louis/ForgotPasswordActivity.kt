package com.st_louis

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.st_louis.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var dialogResetSuccess: DialogResetSuccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogResetSuccess = DialogResetSuccess(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String) {
        // API call to reset password would happen here
        // On success, show dialog
        dialogResetSuccess.show(
            title = "Check your email",
            message = "We've sent a password reset link to your email address. Please check your inbox.",
            onBackToLogin = {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            },
            onResend = {
                resendResetEmail(email)
            }
        )
    }

    private fun resendResetEmail(email: String) {
        // API call to resend reset email
        Toast.makeText(
            this,
            "Reset link resent to $email",
            Toast.LENGTH_SHORT
        ).show()
    }
}

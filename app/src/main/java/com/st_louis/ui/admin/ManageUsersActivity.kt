package com.st_louis.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.st_louis.databinding.ActivityManageUsersBinding
import com.st_louis.models.UserAccount
import java.util.*

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private val db = FirebaseFirestore.getInstance()
    private var userRole: String = "STUDENT"
    private var isEditMode: Boolean = false
    private var existingUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRole = intent.getStringExtra("USER_TYPE")?.uppercase() ?: "STUDENT"
        existingUserId = intent.getStringExtra("EDIT_USER_ID")
        isEditMode = existingUserId != null

        setupUI()
        if (isEditMode) {
            loadUserData(existingUserId!!)
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.toolbar.title = if (isEditMode) "Edit $userRole" else "Register $userRole"
        binding.tvFormTitle.text = if (isEditMode) "Update Information" else "Create New $userRole Account"
        binding.btnSubmit.text = if (isEditMode) "Save Changes" else "Register and Generate Credentials"

        // Setup Spinners
        val classes = arrayOf("Pre-Unit", "Baby Class", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6")
        val streams = arrayOf("Blue", "Yellow", "Green", "N/A")
        
        binding.actvClass.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classes))
        binding.actvStream.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, streams))

        // Adjust visibility based on role
        if (userRole == "BURSAR" || userRole == "ADMIN" || userRole == "TEACHER") {
            binding.tilClass.visibility = View.GONE
            binding.tilStream.visibility = View.GONE
            binding.tilParentEmail.visibility = View.GONE
        } else if (userRole == "STUDENT") {
            binding.tilParentEmail.visibility = View.VISIBLE
        }

        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
        
        // Generate preview on the fly for new users
        if (!isEditMode) {
            binding.etName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) generateCredentialsPreview()
            }
            binding.etIdNumber.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) generateCredentialsPreview()
            }
        }
    }

    private fun generateCredentialsPreview() {
        val name = binding.etName.text.toString().trim()
        val idNum = binding.etIdNumber.text.toString().trim()
        
        if (name.isNotEmpty() && idNum.isNotEmpty()) {
            val username = name.split(" ").first().lowercase() + "_" + idNum.takeLast(4)
            val password = "ST_" + UUID.randomUUID().toString().take(6).uppercase()
            
            binding.tvGeneratedUsername.text = username
            binding.tvGeneratedPassword.text = password
        }
    }

    private fun loadUserData(uid: String) {
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val user = doc.toObject(UserAccount::class.java)
            user?.let {
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
                binding.etPhone.setText(it.phone)
                binding.etIdNumber.setText(it.userId)
                binding.actvClass.setText(it.className, false)
                binding.actvStream.setText(it.stream, false)
                binding.etParentEmail.setText(it.parentEmail)
                binding.tvGeneratedUsername.text = it.generatedUsername
                binding.tvGeneratedPassword.text = "*******" // Security
            }
        }
    }

    private fun validateAndSubmit() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val idNumber = binding.etIdNumber.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || idNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val username = binding.tvGeneratedUsername.text.toString()
        val password = binding.tvGeneratedPassword.text.toString()

        val userData = UserAccount(
            id = existingUserId ?: db.collection("users").document().id,
            name = name,
            email = email,
            phone = phone,
            userId = idNumber,
            role = userRole,
            className = binding.actvClass.text.toString(),
            stream = binding.actvStream.text.toString(),
            parentEmail = binding.etParentEmail.text.toString(),
            generatedUsername = username,
            tempPassword = if (isEditMode) "" else password
        )

        db.collection("users").document(userData.id).set(userData)
            .addOnSuccessListener {
                showSuccessDialog(userData)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showSuccessDialog(user: UserAccount) {
        AlertDialog.Builder(this)
            .setTitle("Account Ready")
            .setMessage("""
                Account for ${user.name} has been ${if (isEditMode) "updated" else "created"}.
                
                Login Credentials:
                Username: ${user.generatedUsername}
                Password: ${if (isEditMode) "[Unchanged]" else user.tempPassword}
                
                Please share these details with the user.
            """.trimIndent())
            .setPositiveButton("Done") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}

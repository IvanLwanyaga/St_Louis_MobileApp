package com.st_louis.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.st_louis.data.repository.UserRepository
import com.st_louis.databinding.ActivityTeacherManagementBinding
import com.st_louis.models.UserAccount
import com.st_louis.navigation.NavigationRoutes
import com.st_louis.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TeacherManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherManagementBinding
    private lateinit var adapter: UserAdapter

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadTeachers()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = UserAdapter(emptyList()) { user ->
            editTeacher(user)
        }
        binding.rvTeachers.layoutManager = LinearLayoutManager(this)
        binding.rvTeachers.adapter = adapter

        binding.btnAddTeacher.setOnClickListener {
            NavigationRoutes.navigateToRegistration(this, "TEACHER")
        }
    }

    private fun loadTeachers() {
        // Show loading
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            val result = userRepository.getAllUsers()

            // Hide loading
            binding.progressBar.visibility = android.view.View.GONE

            when (result) {
                is Result.Success -> {
                    // Filter only teachers
                    val teachers = result.data.filter {
                        it.role.equals("TEACHER", ignoreCase = true)
                    }
                    adapter.updateData(teachers)

                    if (teachers.isEmpty()) {
                        Toast.makeText(
                            this@TeacherManagementActivity,
                            "No teachers found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@TeacherManagementActivity,
                        "Failed to load teachers: ${result.exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Result.Loading -> {
                    // This won't happen since we're not returning Loading from repository
                    // But you can handle it just in case
                }
            }
        }
    }

    private fun editTeacher(user: UserAccount) {
        val intent = Intent(this, ManageUsersActivity::class.java)
        intent.putExtra("USER_TYPE", "TEACHER")
        intent.putExtra("EDIT_USER_ID", user.id)
        startActivity(intent)
    }
}
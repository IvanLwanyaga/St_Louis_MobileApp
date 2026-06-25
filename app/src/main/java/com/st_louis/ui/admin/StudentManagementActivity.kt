package com.st_louis.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.st_louis.databinding.ActivityStudentManagementBinding
import com.st_louis.models.UserAccount
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentManagementBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadStudents()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = UserAdapter(emptyList()) { user ->
            editStudent(user)
        }
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter

        binding.btnAddStudent.setOnClickListener {
            NavigationRoutes.navigateToRegistration(this, "Student")
        }
        binding.fabAddStudent.setOnClickListener {
            NavigationRoutes.navigateToRegistration(this, "Student")
        }
    }

    private fun loadStudents() {
        db.collection("users")
            .whereEqualTo("role", "STUDENT")
            .addSnapshotListener { snapshot, _ ->
                val students = snapshot?.toObjects(UserAccount::class.java) ?: emptyList()
                adapter.updateData(students)
            }
    }

    private fun editStudent(user: UserAccount) {
        val intent = Intent(this, ManageUsersActivity::class.java)
        intent.putExtra("USER_TYPE", "STUDENT")
        intent.putExtra("EDIT_USER_ID", user.id)
        startActivity(intent)
    }
}

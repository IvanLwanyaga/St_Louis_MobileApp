package com.st_louis.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.st_louis.databinding.ActivityBursarManagementBinding
import com.st_louis.models.UserAccount
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BursarManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBursarManagementBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBursarManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadBursars()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = UserAdapter(emptyList()) { user ->
            editBursar(user)
        }
        binding.rvBursars.layoutManager = LinearLayoutManager(this)
        binding.rvBursars.adapter = adapter

        binding.btnAddBursar.setOnClickListener {
            NavigationRoutes.navigateToRegistration(this, "Bursar")
        }
    }

    private fun loadBursars() {
        db.collection("users")
            .whereEqualTo("role", "BURSAR")
            .addSnapshotListener { snapshot, _ ->
                val bursars = snapshot?.toObjects(UserAccount::class.java) ?: emptyList()
                adapter.updateData(bursars)
            }
    }

    private fun editBursar(user: UserAccount) {
        val intent = Intent(this, ManageUsersActivity::class.java)
        intent.putExtra("USER_TYPE", "BURSAR")
        intent.putExtra("EDIT_USER_ID", user.id)
        startActivity(intent)
    }
}

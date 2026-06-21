package com.st_louis.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.databinding.ItemUserCardBinding
import com.st_louis.models.UserAccount

class UserAdapter(
    private var users: List<UserAccount>,
    private val onEditClick: (UserAccount) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUserCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvUserName.text = user.name
        holder.binding.tvUserSub.text = if (user.role == "STUDENT") "${user.className} · ${user.userId}" else "${user.role} · ${user.userId}"
        holder.binding.tvInitials.text = user.name.split(" ").filter { it.isNotEmpty() }.take(2).map { it[0] }.joinToString("").uppercase()
        
        holder.binding.btnEdit.setOnClickListener { onEditClick(user) }
    }

    override fun getItemCount() = users.size

    fun updateData(newUsers: List<UserAccount>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}

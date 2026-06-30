package com.st_louis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.models.Student

class StudentListAdapter(
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {

    private var students: List<Student> = emptyList()

    fun submitList(newList: List<Student>) {
        students = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvAdmissionNo: TextView = itemView.findViewById(R.id.tvAdmissionNo)
        private val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
        private val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)

        fun bind(student: Student) {
            tvStudentName.text = student.name
            tvAdmissionNo.text = "Adm: ${student.admissionNumber}"
            tvClassName.text = "Class: ${student.className}"

            val initials = student.name.split(" ")
                .take(2)
                .map { it.first().uppercase() }
                .joinToString("")
            tvInitials.text = initials

            itemView.setOnClickListener {
                onItemClick(student)
            }
        }
    }
}
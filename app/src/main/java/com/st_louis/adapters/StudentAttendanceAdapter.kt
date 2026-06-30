package com.st_louis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.models.Student

class StudentAttendanceAdapter(
    private val onAttendanceChanged: (Student, Boolean) -> Unit
) : RecyclerView.Adapter<StudentAttendanceAdapter.AttendanceViewHolder>() {

    private var students: List<Student> = emptyList()
    private val attendanceMap = mutableMapOf<String, Boolean>()

    fun submitList(newList: List<Student>) {
        students = newList
        // Initialize all as not marked
        students.forEach { attendanceMap[it.id] = false }
        notifyDataSetChanged()
    }

    fun markAllPresent() {
        students.forEach { student ->
            attendanceMap[student.id] = true
            onAttendanceChanged(student, true)
        }
        notifyDataSetChanged()
    }

    fun markAllAbsent() {
        students.forEach { student ->
            attendanceMap[student.id] = false
            onAttendanceChanged(student, false)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvAdmissionNo: TextView = itemView.findViewById(R.id.tvAdmissionNo)
        private val btnPresent: Button = itemView.findViewById(R.id.btnPresent)
        private val btnAbsent: Button = itemView.findViewById(R.id.btnAbsent)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(student: Student) {
            tvStudentName.text = student.name
            tvAdmissionNo.text = "Adm: ${student.admissionNumber}"

            val isPresent = attendanceMap[student.id] ?: false
            updateUI(isPresent)

            btnPresent.setOnClickListener {
                attendanceMap[student.id] = true
                onAttendanceChanged(student, true)
                updateUI(true)
            }

            btnAbsent.setOnClickListener {
                attendanceMap[student.id] = false
                onAttendanceChanged(student, false)
                updateUI(false)
            }
        }

        private fun updateUI(isPresent: Boolean) {
            if (isPresent) {
                btnPresent.setBackgroundColor(itemView.context.getColor(R.color.status_success))
                btnPresent.setTextColor(itemView.context.getColor(android.R.color.white))
                btnAbsent.setBackgroundColor(itemView.context.getColor(android.R.color.transparent))
                btnAbsent.setTextColor(itemView.context.getColor(R.color.text_secondary))
                tvStatus.text = "✅ Present"
                tvStatus.setTextColor(itemView.context.getColor(R.color.status_success))
            } else {
                btnAbsent.setBackgroundColor(itemView.context.getColor(R.color.status_error))
                btnAbsent.setTextColor(itemView.context.getColor(android.R.color.white))
                btnPresent.setBackgroundColor(itemView.context.getColor(android.R.color.transparent))
                btnPresent.setTextColor(itemView.context.getColor(R.color.text_secondary))
                tvStatus.text = "❌ Absent"
                tvStatus.setTextColor(itemView.context.getColor(R.color.status_error))
            }
        }
    }
}
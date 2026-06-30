package com.st_louis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.models.Student

class StudentResultAdapter(
    private val isPrePrimary: () -> Boolean,
    private val onResultChanged: (Student, Int, String) -> Unit
) : RecyclerView.Adapter<StudentResultAdapter.ResultViewHolder>() {

    private var students: List<Student> = emptyList()
    private val scores = mutableMapOf<String, Int>()
    private val grades = mutableMapOf<String, String>()

    fun submitList(newList: List<Student>) {
        students = newList
        scores.clear()
        grades.clear()
        notifyDataSetChanged()
    }

    fun clearList() {
        students = emptyList()
        scores.clear()
        grades.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_results, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvAdmissionNo: TextView = itemView.findViewById(R.id.tvAdmissionNo)
        private val etScore: EditText = itemView.findViewById(R.id.etScore)
        private val tvGrade: TextView = itemView.findViewById(R.id.tvGrade)
        private val tvPrePrimaryGrade: TextView = itemView.findViewById(R.id.tvPrePrimaryGrade)

        fun bind(student: Student) {
            tvStudentName.text = student.name
            tvAdmissionNo.text = "Adm: ${student.admissionNo}"

            // Show appropriate grading UI based on section
            if (isPrePrimary()) {
                tvPrePrimaryGrade.visibility = View.VISIBLE
                tvGrade.visibility = View.GONE
                setupPrePrimaryGrading(student)
            } else {
                tvPrePrimaryGrade.visibility = View.GONE
                tvGrade.visibility = View.VISIBLE
                setupPrimaryGrading(student)
            }
        }

        private fun setupPrePrimaryGrading(student: Student) {
            tvPrePrimaryGrade.text = "Grade: Click to set"
            tvPrePrimaryGrade.setOnClickListener {
                showPrePrimaryGradeOptions(student)
            }

            // Show saved grade if exists
            grades[student.id]?.let { savedGrade ->
                tvPrePrimaryGrade.text = "Grade: $savedGrade"
            }
        }

        private fun setupPrimaryGrading(student: Student) {
            etScore.hint = "Score (0-100)"
            etScore.setText(scores[student.id]?.toString() ?: "")

            etScore.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    val score = s.toString().toIntOrNull()
                    if (score != null && score in 0..100) {
                        val grade = calculatePrimaryGrade(score)
                        tvGrade.text = "Grade: $grade"
                        scores[student.id] = score
                        grades[student.id] = grade
                        onResultChanged(student, score, grade)
                    } else if (s.isNullOrEmpty()) {
                        tvGrade.text = "Grade: -"
                        scores.remove(student.id)
                        grades.remove(student.id)
                        // Don't call onResultChanged for empty
                    } else {
                        tvGrade.text = "Invalid score"
                    }
                }
            })
        }

        private fun showPrePrimaryGradeOptions(student: Student) {
            val options = arrayOf(
                "Exceeding (80-100)",
                "Meeting (60-79)",
                "Approaching (40-59)",
                "Beginning (0-39)"
            )

            val gradeMap = mapOf(
                "Exceeding (80-100)" to "Exceeding",
                "Meeting (60-79)" to "Meeting",
                "Approaching (40-59)" to "Approaching",
                "Beginning (0-39)" to "Beginning"
            )

            val scoreMap = mapOf(
                "Exceeding (80-100)" to 90,
                "Meeting (60-79)" to 70,
                "Approaching (40-59)" to 50,
                "Beginning (0-39)" to 30
            )

            android.app.AlertDialog.Builder(itemView.context)
                .setTitle("Select Grade for ${student.name}")
                .setItems(options) { _, which ->
                    val selected = options[which]
                    val grade = gradeMap[selected] ?: ""
                    val score = scoreMap[selected] ?: 0

                    tvPrePrimaryGrade.text = "Grade: $grade"
                    grades[student.id] = grade
                    scores[student.id] = score
                    onResultChanged(student, score, grade)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun calculatePrimaryGrade(score: Int): String {
            return when {
                score >= 80 -> "A"
                score >= 70 -> "B"
                score >= 60 -> "C"
                score >= 50 -> "D"
                else -> "E"
            }
        }
    }
}
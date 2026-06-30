package com.st_louis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.models.TeacherClass

class ClassScheduleAdapter(
    private val onItemClick: (TeacherClass) -> Unit
) : RecyclerView.Adapter<ClassScheduleAdapter.ClassViewHolder>() {

    private var classes: List<TeacherClass> = emptyList()

    fun submitList(newList: List<TeacherClass>) {
        classes = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class_schedule, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun getItemCount(): Int = classes.size

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvRoom: TextView = itemView.findViewById(R.id.tvRoom)
        private val tvStudentCount: TextView = itemView.findViewById(R.id.tvStudentCount)

        fun bind(classItem: TeacherClass) {
            tvClassName.text = classItem.name
            tvSubject.text = "📚 ${classItem.subject}"
            tvTime.text = "⏰ ${classItem.time}"
            tvRoom.text = "🏫 ${classItem.room}"
            tvStudentCount.text = "👥 ${classItem.studentsCount} students"

            itemView.setOnClickListener {
                onItemClick(classItem)
            }
        }
    }
}
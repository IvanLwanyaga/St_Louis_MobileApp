package com.st_louis.ui.student

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.databinding.ItemSubjectResultBinding
import com.st_louis.models.SubjectResult

class SubjectResultsAdapter : ListAdapter<SubjectResult, SubjectResultsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSubjectResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSubjectResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubjectResult) {
            binding.tvSubjectName.text = item.subjectName
            binding.tvSubjectMarks.text = "${item.marks} / ${item.totalMarks} marks"
            binding.pbSubjectScore.max = item.totalMarks
            binding.pbSubjectScore.progress = item.marks
            binding.tvSubjectGrade.text = item.grade

            val context = binding.root.context
            val (gradeColor, bgColor) = when (item.grade.firstOrNull()) {
                'A', 'B' -> Color.parseColor("#059669") to Color.parseColor("#E1F5E5")
                'C' -> Color.parseColor("#D97706") to Color.parseColor("#FFFBEB")
                else -> Color.parseColor("#DC2626") to Color.parseColor("#FEF2F2")
            }

            binding.tvSubjectGrade.setTextColor(gradeColor)
            binding.tvSubjectGrade.backgroundTintList = ColorStateList.valueOf(bgColor)
            binding.pbSubjectScore.progressTintList = ColorStateList.valueOf(gradeColor)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SubjectResult>() {
        override fun areItemsTheSame(oldItem: SubjectResult, newItem: SubjectResult): Boolean =
            oldItem.subjectName == newItem.subjectName

        override fun areContentsTheSame(oldItem: SubjectResult, newItem: SubjectResult): Boolean =
            oldItem == newItem
    }
}

package com.st_louis.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.st_louis.R
import com.st_louis.models.ClassInfo

class ClassSelectorAdapter(
    context: Context,
    private val classes: List<ClassInfo>
) : ArrayAdapter<ClassInfo>(context, R.layout.item_class_selector, classes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_class_selector,
            parent,
            false
        )

        val tvClassName = view.findViewById<TextView>(R.id.tvClassName)
        val tvClassInfo = view.findViewById<TextView>(R.id.tvClassInfo)

        val classInfo = getItem(position)
        classInfo?.let {
            tvClassName.text = it.name
            tvClassInfo.text = "${it.section.capitalize()} - ${it.level ?: "General"}"
        }

        return view
    }

    override fun getCount(): Int = classes.size

    override fun getItem(position: Int): ClassInfo = classes[position]

    override fun getItemId(position: Int): Long = position.toLong()
}
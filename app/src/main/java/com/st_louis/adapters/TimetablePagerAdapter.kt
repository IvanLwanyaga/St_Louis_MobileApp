package com.st_louis.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.st_louis.ui.admin.PrePrimaryTimetableFragment
import com.st_louis.ui.admin.PrimaryTimetableFragment

class TimetablePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PrimaryTimetableFragment()
            else -> PrePrimaryTimetableFragment()
        }
    }
}
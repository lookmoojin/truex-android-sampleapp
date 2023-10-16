package com.truedigital.component.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by aRM on 05/21/18.
 */
class ViewPagerAdapter(
    private val listFragment: List<Fragment>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = listFragment[position]

    override fun getCount(): Int = listFragment.size
}

class LazyLoadViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {

    private var list: MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment = this.list[position]

    override fun getCount(): Int = this.list.size

    fun setListFragment(list: MutableList<Fragment>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}

class StateViewPagerAdapter(
    private val listFragment: List<Fragment>,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = listFragment[position]

    override fun getCount(): Int = listFragment.size
}

class SlidingTabViewPagerAdapter(
    private val listTitleName: List<String>,
    private val listFragment: List<Fragment>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = listFragment[position]

    override fun getCount(): Int = listFragment.size

    override fun getPageTitle(position: Int): CharSequence? = listTitleName[position]
}

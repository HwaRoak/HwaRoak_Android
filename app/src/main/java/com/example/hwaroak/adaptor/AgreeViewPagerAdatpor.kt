package com.example.hwaroak.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hwaroak.ui.login.AgreeInitialFragment
import com.example.hwaroak.ui.login.AgreeSecondFragment
import com.example.hwaroak.ui.login.AgreeThirdFragment

class AgreeViewPagerAdatpor(fragment : FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            AgreeInitialFragment()
        }
        else if(position == 1){
            AgreeSecondFragment()
        }
        else{
            AgreeThirdFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}
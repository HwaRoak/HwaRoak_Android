package com.example.hwaroak.adaptor
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hwaroak.data.DiaryContent
import com.example.hwaroak.ui.diary.DiaryFinishFragment
import com.example.hwaroak.ui.diary.DiaryWriteFragment


class DiaryViewPagerAdaptor(fragment: Fragment,
    private var diaryContent: DiaryContent?): FragmentStateAdapter(fragment) {


    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            if(diaryContent != null){
                Log.d("log_diary", "viewpager가 보낸다")
                DiaryWriteFragment.newInstance(diaryContent!!)
            }
            else {
                DiaryWriteFragment() //일기 작성 fragment
            }
        } else if(position == 1) {
            DiaryFinishFragment() //일기 작성 완료 fragment
        } else{
            DiaryWriteFragment()
        }
    }


    //일단은 2개 리턴
    override fun getItemCount(): Int {
        return 2
    }

}
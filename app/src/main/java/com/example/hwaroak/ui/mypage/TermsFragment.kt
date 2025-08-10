package com.example.hwaroak.ui.mypage

import android.R.attr.visibility
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentTermsBinding
import com.example.hwaroak.ui.main.MainActivity

class TermsFragment : Fragment() {

    private var _binding: FragmentTermsBinding? = null
    private val binding get() = _binding!!

    private var isExpanded = false // 펼침 여부 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTermsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setTopBar("이용 약관", isBackVisible = true)

        binding.termsCv.setOnClickListener {
            toggleCardView(
                binding.termsContentTv,
                binding.termsToggleBtn
            )
        }

        binding.privacyCv.setOnClickListener {
            toggleCardView(
                binding.privacyContentTv,
                binding.privacyToggleBtn
            )
        }

    }

//    private fun toggleCardView(cardView: CardView, expandableLayout: View, arrowImageView: ImageView) {
//
//        TransitionManager.beginDelayedTransition(cardView, AutoTransition())
//
//        // 현재 펼쳐져 있는지 확인
//        val isVisible = expandableLayout.visibility == View.VISIBLE
//
//        // 상태를 반전시켜서 보여주거나 숨김
//        expandableLayout.visibility = if (isVisible) View.GONE else View.VISIBLE
//
//        // 화살표 아이콘을 상태에 맞게 회전
//        arrowImageView.animate().rotation(if (isVisible) 0f else 90f)
//    }

    private fun toggleCardView(expandableLayout: View, arrowImageView: ImageView) {
        if (isExpanded) {
            expandableLayout.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    expandableLayout.visibility = View.GONE
                }
                .start()
            arrowImageView.animate()
                .rotation(0f)
                .setDuration(200)
                .start()
        } else {
            expandableLayout.alpha = 0f
            expandableLayout.visibility = View.VISIBLE
            expandableLayout.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
            arrowImageView.animate()
                .rotation(90f)
                .setDuration(200)
                .start()
        }
        isExpanded = !isExpanded
    }
}
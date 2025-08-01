package com.example.hwaroak.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.hwaroak.R
import com.example.hwaroak.data.AgreeViewModel
import com.example.hwaroak.databinding.FragmentAgreeSecondBinding
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgreeSecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgreeSecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //viewModel
    private val vm: AgreeViewModel by activityViewModels()
    private lateinit var binding: FragmentAgreeSecondBinding

    private var scrollCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAgreeSecondBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.agree2FinishBtn.isEnabled = false
        binding.agree2FinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)

        //스크롤이 가능한 경우
        binding.agree2Scrollview.setOnScrollChangeListener { v, _, _, _, _ ->
            val atBottom = !v.canScrollVertically(1)
            if(!atBottom){scrollCheck = true}

            if(scrollCheck){
                binding.agree2FinishBtn.isEnabled = true
                binding.agree2FinishBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
            }
        }

        //스크롤이 안될 경우(변화 감지)
        binding.agree2Scrollview.post {
            val canScrollDown = binding.agree2Scrollview.canScrollVertically(1)
            val canScrollUp = binding.agree2Scrollview.canScrollVertically(-1)
            val checkScroll = canScrollDown || canScrollUp
            if (!checkScroll) {
                binding.agree2FinishBtn.isEnabled = true
                binding.agree2FinishBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
            }
        }

        //버튼 리스너
        binding.agree2FinishBtn.setOnClickListener {
            if(vm.allGo.value == true) {
                vm.serviceAgree.value = true
                vm.privacyAgree.value = true
            }
            else{
                vm.serviceAgree.value = true
            }
            (activity as AgreeActivity).goToMainPage()
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AgreeSecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgreeSecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
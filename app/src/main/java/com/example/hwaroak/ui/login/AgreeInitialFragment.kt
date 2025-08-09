package com.example.hwaroak.ui.login

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import com.example.hwaroak.R
import com.example.hwaroak.data.AgreeViewModel
import com.example.hwaroak.databinding.FragmentAgreeInitialBinding
import com.example.hwaroak.ui.main.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgreeInitialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgreeInitialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAgreeInitialBinding

    //viewModel
    private val vm: AgreeViewModel by activityViewModels()
    private lateinit var agreePref: SharedPreferences

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
        binding = FragmentAgreeInitialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sharedPrefernce 확인용
        agreePref = requireContext().getSharedPreferences("agree", MODE_PRIVATE)

        initSetting()
        buttonSetting()

        //livedata를 observe해서 바뀔떄마다 UI 변경
        vm.serviceAgree.observe(viewLifecycleOwner) { buttonUIsetting() }
        vm.privacyAgree.observe(viewLifecycleOwner) { buttonUIsetting() }

        binding.agreeFinishBtn.setOnClickListener {
            agreePref.edit { putBoolean("agree", true).apply() }
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            requireActivity().finish()
        }


    }

    
    //초기 세팅
    private fun initSetting(){
        //각 버튼들의 눌림 여부 및 버튼 접근X
        binding.agreeFinishBtn.isEnabled = false
        binding.agreeFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)

        binding.agreeCheck1Btn.setImageResource(R.drawable.ic_agree_no)
        binding.agreeCheck2Btn.setImageResource(R.drawable.ic_agree_no)
        binding.agreeCheck3Btn.setImageResource(R.drawable.ic_agree_no)
        binding.agreeFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
        binding.agreeFinishBtn.isEnabled = false
    }

    //각 버튼을 눌렀을 때의 T/F 설정
    private fun buttonSetting(){
        var current = false
        //1. 모두 동의
        binding.agreeCheck1Btn.setOnClickListener {
            
            current = vm.serviceAgree.value ?: false
            
            //일단 false인 상태면 이동
            if(!current){
                vm.allGo.value = true
                (activity as AgreeActivity).goToDetailPage()
            }
            else{
                vm.privacyAgree.value = false
                vm.serviceAgree.value = false
            }
        }

        //2. 서비스 동의
        binding.agreeCheck2Btn.setOnClickListener {

            current = vm.serviceAgree.value ?: false
            if(!current) {
                vm.allGo.value = false
                (activity as AgreeActivity).goToDetailPage()
            }
            else{
                vm.serviceAgree.value = false
            }


        }

        //3. 개인정보 처리 방침 동의
        binding.agreeCheck3Btn.setOnClickListener {
            current = vm.privacyAgree.value ?: false
            if(!current){
                vm.privacyAgree.value = false
                (activity as AgreeActivity).goToDetailPage2()
            }
            else{vm.privacyAgree.value = false}
        }

        //4. 보기 버튼
        binding.agreeSee2Tv.setOnClickListener {
            vm.allGo.value = false
            (activity as AgreeActivity).goToDetailPage() }
        binding.agreeSee3Tv.setOnClickListener {
            vm.allGo.value = false
            (activity as AgreeActivity).goToDetailPage2() }
    }

    //T/F 유무에 따라 버튼 UI 변경
    private fun buttonUIsetting(){
        var c1 = vm.serviceAgree.value ?: false
        var c2 = vm.privacyAgree.value ?: false

        if(c1){binding.agreeCheck2Btn.setImageResource(R.drawable.ic_agree_ok)}
        else{binding.agreeCheck2Btn.setImageResource(R.drawable.ic_agree_no)}

        if(c2){binding.agreeCheck3Btn.setImageResource(R.drawable.ic_agree_ok)}
        else{binding.agreeCheck3Btn.setImageResource(R.drawable.ic_agree_no)}

        if(c1 && c2){
            binding.agreeCheck1Btn.setImageResource(R.drawable.ic_agree_ok)
            binding.agreeFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
            binding.agreeFinishBtn.isEnabled = true
        }
        else{
            binding.agreeCheck1Btn.setImageResource(R.drawable.ic_agree_no)
            binding.agreeFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
            binding.agreeFinishBtn.isEnabled = false
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AgreeInitialFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgreeInitialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
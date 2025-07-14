package com.example.hwaroak.api.diary.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.diary.repository.DiaryRepository

/**
 * access
 *
 * viewModelFactory
 * viewModel의 경우, 생성자를 통해 의존성을 주입받을 수 없기 때문에 factory에서 따로
 * 의존성을 주입시키는 절차가 필요하다.
 *
 * 쉽게 말하면, viewModel을 생성할 땐, 인자 없이 ViewModel() 이렇게 받는다.
 * 그러나 우리는 viewModel에서 repository를 받아서 repository에서 api를 호출하도록
 * 하고 싶다.(로직 및 기능 분리를 위해서)
 * 그래서 factory를 만들어서 viewmodel 안에서 repository를 쓸 수 있게 한다.
 * **/

class DiaryViewModelFactory(private val repository: DiaryRepository) :
    ViewModelProvider.Factory {
    //요청한 ViewModel 클래스가 DiaryViewModel과 같거나 상속 관계인지 확인한다.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {

            // 의존성을 주입해 DiaryViewModel 인스턴스를 직접 생성해서 반환
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
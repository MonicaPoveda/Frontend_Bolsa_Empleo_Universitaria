package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi

class PostulacionViewModelFactory(private val api: PostulacionApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostulacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostulacionViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository

class OfertasViewModelFactory(
    private val repository: OfertasRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OfertasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OfertasViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
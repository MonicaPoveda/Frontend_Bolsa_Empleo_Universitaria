package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository

class PostulacionViewModelFactory(
    private val postulacionRepository: PostulacionRepository,
    private val seguimientoRepository: SeguimientoPostulacionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostulacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostulacionViewModel(postulacionRepository, seguimientoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
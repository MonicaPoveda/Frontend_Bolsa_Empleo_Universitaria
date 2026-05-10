package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionesRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository

class PostulacionViewModelFactory(
    private val api: PostulacionApi? = null,
    private val postulacionesRepository: PostulacionesRepository? = null,
    private val seguimientoRepository: SeguimientoPostulacionRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostulacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostulacionViewModel(api, postulacionesRepository!!, seguimientoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
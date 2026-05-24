package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository

class PerfilViewModelFactory(
    private val usuarioApi: UsuarioApi,
    private val perfilRepository: PerfilRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(usuarioApi, perfilRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

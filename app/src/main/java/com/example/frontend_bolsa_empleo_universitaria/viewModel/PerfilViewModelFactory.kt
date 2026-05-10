package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PerfilApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi

class PerfilViewModelFactory(
    private val usuarioApi: UsuarioApi,
    private val perfilApi: PerfilApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(usuarioApi, perfilApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
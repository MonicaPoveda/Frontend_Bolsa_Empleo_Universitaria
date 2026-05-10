package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val token: Token,
    private val empresaRepository: EmpresaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository, token, empresaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
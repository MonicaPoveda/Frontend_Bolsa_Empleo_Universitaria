package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.ArchivoRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.ProfilePhotoSection
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.ArchivoUrls
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMiPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val archivoRepo = remember { ArchivoRepository(RetrofitClient.archivoApi, context) }

    val userId = token.getUserId()
    val email = token.getUserEmail() ?: ""
    
    // Estados para edición
    var nombre by remember { mutableStateOf(token.getUserNombre()) }
    var apellido by remember { mutableStateOf(token.getUserApellido()) }
    var telefono by remember { mutableStateOf(token.getUserTelefono()) }
    
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var hasPhoto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BolsaTokens.Palette.Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BolsaTokens.Palette.Background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (userId == null || userId <= 0L) {
                Text("No se encontró la sesión del administrador.", color = BolsaTokens.Palette.Error)
                return@Column
            }

            // Tarjeta de Información Principal
            Card(
                colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BolsaTokens.Palette.Primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rol: Administrador", fontWeight = FontWeight.Bold, color = BolsaTokens.Palette.Primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(email, color = BolsaTokens.Palette.TextSecondary, fontSize = 14.sp)
                }
            }

            ProfilePhotoSection(
                photoUrl = ArchivoUrls.fotoUsuario(userId),
                title = "Foto de perfil",
                placeholderIcon = Icons.Default.AdminPanelSettings,
                hasUploadedPhoto = hasPhoto,
                onUpload = { uri, replace ->
                    archivoRepo.subirFotoUsuario(userId, uri, replace).also { result ->
                        if (result.isSuccess) hasPhoto = true
                    }
                }
            )

            // Formulario de Edición
            Card(
                colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Datos personales", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { 
                            if (it.length <= 10) telefono = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ej: 3001234567") }
                    )
                }
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = BolsaTokens.Palette.Error, fontSize = 14.sp)
            }
            if (successMessage != null) {
                Text(successMessage!!, color = BolsaTokens.Palette.Success, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorMessage = "El nombre es obligatorio"
                        return@Button
                    }
                    isSaving = true
                    errorMessage = null
                    successMessage = null
                    
                    scope.launch {
                        try {
                            val request = com.example.frontend_bolsa_empleo_universitaria.model.ActualizarUsuario(
                                nombre = nombre,
                                apellido = apellido,
                                email = email,
                                telefono = telefono.ifBlank { null },
                                password = null
                            )
                            val response = RetrofitClient.usuarioApi.actualizarUsuario(userId, request)
                            if (response.isSuccessful) {
                                successMessage = "Perfil actualizado correctamente"
                                // Sincronizar Token
                                token.saveUserName(nombre, apellido)
                                token.saveUserTelefono(telefono)
                            } else {
                                errorMessage = "Error al actualizar: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error de red: ${e.message}"
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Primary),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar cambios", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

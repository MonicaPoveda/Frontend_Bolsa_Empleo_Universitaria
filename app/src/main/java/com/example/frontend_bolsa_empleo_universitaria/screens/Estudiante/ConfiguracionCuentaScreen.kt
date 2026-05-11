package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarPerfilUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarUsuario
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionCuentaScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }
    val scope = rememberCoroutineScope()

    val viewModel: PerfilViewModel = viewModel(
        factory = PerfilViewModelFactory(RetrofitClient.usuarioApi, RetrofitClient.perfilApi)
    )

    val perfilState by viewModel.perfil.collectAsState()
    val isLoadingViewModel by viewModel.loading.collectAsState()
    val errorViewModel by viewModel.error.collectAsState()

    // Campos editables
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    // Campos del perfil profesional
    var perfilId by remember { mutableStateOf<Long?>(null) }
    var carrera by remember { mutableStateOf("") }
    var universidad by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var habilidades by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
    var disponibilidad by remember { mutableStateOf("INMEDIATA") }
    var expanded by remember { mutableStateOf(false) }
    val disponibilidadOptions = listOf("INMEDIATA", "1 MES", "3 MESES", "6 MESES")

    var isLoadingSave by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ Inicializar campos personales usando getters específicos del Token
    LaunchedEffect(Unit) {
        val userId = tokenManager.getUserId()
        if (userId != null) {
            viewModel.cargarSoloPerfil(userId)
        }
        // Cargar datos desde el token
        nombre = tokenManager.getUserNombre()
        apellido = tokenManager.getUserApellido()
        telefono = tokenManager.getUserTelefono()
    }

    // Sincronizar perfil profesional
    LaunchedEffect(perfilState) {
        perfilState?.let {
            perfilId = it.idPerfil
            carrera = it.carrera ?: ""
            universidad = it.universidad ?: ""
            semestre = it.semestre ?: ""
            habilidades = it.habilidades ?: ""
            experiencia = it.experiencia ?: ""
            disponibilidad = it.disponibilidad ?: "INMEDIATA"
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E5A7A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoadingViewModel) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Datos Personales ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Datos personales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E5A7A))
                        Spacer(modifier = Modifier.height(12.dp))

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
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Nueva Contraseña (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                                    Icon(
                                        if (mostrarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                        if (password.isNotBlank()) {
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirmar Contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation()
                            )
                            if (password != confirmPassword && confirmPassword.isNotBlank()) {
                                Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // --- Perfil Profesional ---
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Perfil profesional", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E5A7A))
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = carrera,
                            onValueChange = { carrera = it },
                            label = { Text("Carrera") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = universidad,
                            onValueChange = { universidad = it },
                            label = { Text("Universidad") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = semestre,
                            onValueChange = { semestre = it },
                            label = { Text("Semestre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = habilidades,
                            onValueChange = { habilidades = it },
                            label = { Text("Habilidades") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        OutlinedTextField(
                            value = experiencia,
                            onValueChange = { experiencia = it },
                            label = { Text("Experiencia (solo egresados)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedTextField(
                                value = disponibilidad,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Disponibilidad") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                disponibilidadOptions.forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            disponibilidad = opcion
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (errorMessage != null || errorViewModel != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                        Text(
                            errorMessage ?: errorViewModel ?: "",
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (password.isNotBlank() && password != confirmPassword) {
                            errorMessage = "Las contraseñas no coinciden"
                            return@Button
                        }
                        isLoadingSave = true
                        val userId = tokenManager.getUserId()
                        if (userId != null) {
                            // 1. Actualizar datos personales
                            val usuarioActualizado = ActualizarUsuario(
                                nombre = nombre,
                                apellido = apellido,
                                email = tokenManager.getUserEmail() ?: "",
                                telefono = telefono.ifBlank { null },
                                password = if (password.isNotBlank()) password else null
                            )

                            viewModel.actualizarUsuario(
                                userId,
                                usuarioActualizado,
                                onSuccess = {
                                    // 2. Actualizar perfil profesional si existe
                                    if (perfilId != null) {
                                        val perfilActualizado = ActualizarPerfilUsuario(
                                            idPerfil = perfilId!!,
                                            carrera = carrera,
                                            universidad = universidad,
                                            semestre = semestre.ifBlank { null },
                                            habilidades = habilidades,
                                            experiencia = experiencia.ifBlank { null },
                                            cvUrl = null,
                                            disponibilidad = disponibilidad,
                                            idUsuario = userId
                                        )
                                        viewModel.actualizarPerfil(
                                            perfilId!!,
                                            perfilActualizado,
                                            onSuccess = {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("✅ Datos actualizados correctamente")
                                                    // ✅ Actualizar token con todos los campos (incluido teléfono)
                                                    tokenManager.saveToken(
                                                        token = tokenManager.getToken() ?: "",
                                                        email = tokenManager.getUserEmail() ?: "",
                                                        rol = tokenManager.getUserRole() ?: "",
                                                        idUsuario = userId,
                                                        nombre = nombre,
                                                        apellido = apellido,
                                                        telefono = telefono
                                                    )
                                                    isLoadingSave = false
                                                    navController.popBackStack()
                                                }
                                            },
                                            onError = {
                                                errorMessage = it
                                                isLoadingSave = false
                                            }
                                        )
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✅ Datos personales actualizados")
                                            tokenManager.saveToken(
                                                token = tokenManager.getToken() ?: "",
                                                email = tokenManager.getUserEmail() ?: "",
                                                rol = tokenManager.getUserRole() ?: "",
                                                idUsuario = userId,
                                                nombre = nombre,
                                                apellido = apellido,
                                                telefono = telefono
                                            )
                                            isLoadingSave = false
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                onError = {
                                    errorMessage = it
                                    isLoadingSave = false
                                }
                            )
                        } else {
                            errorMessage = "Sesión no válida"
                            isLoadingSave = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5A7A)),
                    enabled = !isLoadingSave && !isLoadingViewModel
                ) {
                    if (isLoadingSave) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

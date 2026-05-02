package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModel

private val AzulOscuro = Color(0xFF001F3F)
private val AzulMedio = Color(0xFF0056D2)
private val FondoGris = Color(0xFFF8FAFF)
private val TextoGrisPerfil = Color(0xFF8A94A6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    viewModel: PerfilViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUsuarioActualizado: (Usuario) -> Unit = {}
) {
    val esEstudiante = viewModel.tipoUsuario == "ESTUDIANTE"
    val scrollState = rememberScrollState()
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }

    // Cargar datos al entrar
    LaunchedEffect(usuario.idUsuario) {
        viewModel.cargarUsuario(usuario)
        usuario.idUsuario?.let { viewModel.cargarPerfil(it) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.uiState) {
        if (viewModel.uiState is PerfilState.Success) {
            snackbarHostState.showSnackbar("✅ Cambios guardados correctamente")
            viewModel.resetState()
        }
    }

    // ── Diálogo cerrar sesión ─────────────────────────────────────────────
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro que deseas cerrar sesión? Tendrás que iniciar sesión nuevamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = false },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Text("Cancelar", color = Color.Black)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        onLogout()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = FondoGris,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AzulOscuro
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F5FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = AzulOscuro,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoCerrarSesion = true }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AzulOscuro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar Sesión",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        viewModel.guardarCambiosUsuario { usuarioActualizado ->
                            onUsuarioActualizado(usuarioActualizado)
                        }
                        viewModel.guardarCambios()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                    enabled = viewModel.uiState !is PerfilState.Loading
                ) {
                    if (viewModel.uiState is PerfilState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Guardar cambios",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
// ── Header con gradiente ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Brush.verticalGradient(colors = listOf(AzulOscuro, AzulMedio)))
            )

// Avatar flotante sobre el fondo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-48).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Círculo avatar con borde blanco
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(AzulMedio, AzulOscuro)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.nombreUsuario.take(1).uppercase(),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nombre con ícono de editar
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.wrapContentWidth()
                        ) {

                            BasicTextField(
                                value = viewModel.nombreUsuario,
                                onValueChange = { viewModel.nombreUsuario = it },
                                textStyle = LocalTextStyle.current.copy(
                                    color = AzulOscuro,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                cursorBrush = SolidColor(AzulMedio),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (viewModel.nombreUsuario.isEmpty()) {
                                                Text(
                                                    "Nombre completo",
                                                    color = Color.Gray,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            innerTextField()
                                        }


                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(9.dp))

                    // Fila: Correo | Tipo usuario
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Campo correo
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                .background(Color(0xFFF0F4FF))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                value = viewModel.emailUsuario,
                                onValueChange = { viewModel.emailUsuario = it },
                                textStyle = LocalTextStyle.current.copy(
                                    color = AzulOscuro,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                ),
                                cursorBrush = SolidColor(AzulMedio),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (viewModel.emailUsuario.isEmpty()) {
                                            Text(
                                                "Correo electrónico",
                                                color = Color.Gray,
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Dropdown tipo usuario
                        ExposedDropdownMenuBox(
                            expanded = expandedTipo,
                            onExpandedChange = { expandedTipo = !expandedTipo },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                .background(AzulOscuro)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .menuAnchor()
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (viewModel.tipoUsuario == "ESTUDIANTE") "Estudiante" else "Egresado",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            ExposedDropdownMenu(
                                expanded = expandedTipo,
                                onDismissRequest = { expandedTipo = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                listOf("ESTUDIANTE", "EGRESADO").forEach { tipo ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (tipo == "ESTUDIANTE")
                                                        Icons.Default.School
                                                    else
                                                        Icons.Default.WorkspacePremium,
                                                    contentDescription = null,
                                                    tint = AzulOscuro,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    if (tipo == "ESTUDIANTE") "Estudiante" else "Egresado",
                                                    color = AzulOscuro,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.tipoUsuario = tipo
                                            expandedTipo = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────
            if (viewModel.uiState is PerfilState.Error) {
                Text(
                    text = (viewModel.uiState as PerfilState.Error).mensaje,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Información Académica ─────────────────────────────────────
            SeccionPerfil(titulo = "Información Académica", icono = Icons.Default.School) {
                CampoEditable(
                    label = "Universidad / Institución",
                    value = viewModel.universidad,
                    onValueChange = { viewModel.universidad = it },
                    icono = Icons.Default.LocationCity
                )
                Spacer(modifier = Modifier.height(12.dp))
                CampoEditable(
                    label = if (esEstudiante) "Carrera" else "Título Obtenido",
                    value = viewModel.carrera,
                    onValueChange = { viewModel.carrera = it },
                    icono = Icons.Default.MenuBook
                )
                if (esEstudiante) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CampoEditable(
                        label = "Semestre Actual",
                        value = viewModel.semestre,
                        onValueChange = { viewModel.semestre = it },
                        icono = Icons.Default.CalendarToday
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CampoEditable(
                        label = "Promedio Acumulado",
                        value = viewModel.promedio,
                        onValueChange = { viewModel.promedio = it },
                        icono = Icons.Default.StarBorder,
                        keyboardType = KeyboardType.Decimal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Disponibilidad y CV ───────────────────────────────────────
            SeccionPerfil(titulo = "Disponibilidad y CV", icono = Icons.Default.AccessTime) {
                CampoEditable(
                    label = "Disponibilidad",
                    value = viewModel.disponibilidad,
                    onValueChange = { viewModel.disponibilidad = it },
                    icono = Icons.Default.AccessTime
                )
                Spacer(modifier = Modifier.height(12.dp))
                CampoEditable(
                    label = "Enlace Hoja de Vida",
                    value = viewModel.cvUrl,
                    onValueChange = { viewModel.cvUrl = it },
                    icono = Icons.Default.Link,
                    keyboardType = KeyboardType.Uri
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Habilidades ───────────────────────────────────────────────
            SeccionPerfil(titulo = "Habilidades", icono = Icons.Default.Star) {
                CampoEditable(
                    label = "Habilidades (separadas por coma)",
                    value = viewModel.habilidades,
                    onValueChange = { viewModel.habilidades = it },
                    icono = Icons.Default.Code,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Experiencia ───────────────────────────────────────────────
            SeccionPerfil(titulo = "Experiencia Laboral", icono = Icons.Default.Work) {
                CampoEditable(
                    label = "Describe tu experiencia",
                    value = viewModel.experiencia,
                    onValueChange = { viewModel.experiencia = it },
                    icono = Icons.Default.Work,
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

// ── Componentes reutilizables ─────────────────────────────────────────────────

@Composable
fun SeccionPerfil(
    titulo: String,
    icono: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = AzulMedio,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun CampoEditable(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icono: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                icono,
                contentDescription = null,
                tint = TextoGrisPerfil,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulMedio,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedLabelColor = AzulMedio,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}
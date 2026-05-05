package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    val scrollState = rememberScrollState()
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    LaunchedEffect(usuario.idUsuario) {
        viewModel.cargarUsuario(usuario)
        usuario.idUsuario?.let { viewModel.cargarPerfil(it) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.uiState) {
        if (viewModel.uiState is PerfilState.Success) {
            snackbarHostState.showSnackbar("✅ Perfil actualizado correctamente")
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (viewModel.isEditing) "EDITAR PERFIL" else "MI PERFIL",
                        fontWeight = FontWeight.ExtraBold, 
                        color = AzulOscuro,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (viewModel.isEditing) viewModel.cancelarEdicion() else onBack() }) {
                        Icon(if (viewModel.isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (!viewModel.isEditing) {
                        IconButton(onClick = { viewModel.isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AzulMedio)
                        }
                        IconButton(onClick = { mostrarDialogoCerrarSesion = true }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Salir", tint = Color.Red)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (viewModel.isEditing) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.cancelarEdicion() }, 
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancelar", color = Color.Black)
                        }
                        Button(
                            onClick = {
                                viewModel.guardarCambiosUsuario { onUsuarioActualizado(it) }
                            },
                            modifier = Modifier.weight(1.5f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                            shape = RoundedCornerShape(8.dp),
                            enabled = viewModel.uiState !is PerfilState.Loading
                        ) {
                            if (viewModel.uiState is PerfilState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Guardar", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState)) {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Brush.verticalGradient(listOf(AzulOscuro, AzulMedio)))) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 50.dp).size(100.dp).clip(CircleShape).background(Color.White).padding(4.dp)) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(FondoGris), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = AzulMedio)
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))

            if (!viewModel.isEditing) {
                DatosVista(viewModel)
            } else {
                DatosEdicion(viewModel)
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }

    if (mostrarDialogoCerrarSesion) DialogoCerrarSesion({ mostrarDialogoCerrarSesion = false }, onLogout)
}

@Composable
fun DatosVista(viewModel: PerfilViewModel) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(viewModel.nombreUsuario, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = AzulOscuro)
        Text(viewModel.emailUsuario, color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SeccionPerfil("DATOS DE CONTACTO", Icons.Default.ContactPhone) {
            InfoRowPerfil("Identificación", viewModel.identificacionUsuario)
            InfoRowPerfil("Teléfono", viewModel.telefonoUsuario)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SeccionPerfil("INFORMACIÓN ACADÉMICA", Icons.Default.School) {
            InfoRowPerfil("Universidad", viewModel.universidad)
            InfoRowPerfil("Carrera", viewModel.carrera)
            if (viewModel.tipoUsuario == "ESTUDIANTE") {
                InfoRowPerfil("Semestre", viewModel.semestre)
                InfoRowPerfil("Promedio", viewModel.promedio)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        SeccionPerfil("PERFIL PROFESIONAL", Icons.Default.Work) {
            InfoRowPerfil("Disponibilidad", viewModel.disponibilidad)
            InfoRowPerfil("Habilidades", viewModel.habilidades)
            InfoRowPerfil("Hoja de Vida", viewModel.cvUrl)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DatosEdicion(viewModel: PerfilViewModel) {
    var skillInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        Text("DATOS BÁSICOS", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelLarge)
        
        FieldLabelPerfil("Nombre completo")
        CampoRegistroPerfil(viewModel.nombreUsuario, { viewModel.nombreUsuario = it }, Icons.Default.Person)
        
        FieldLabelPerfil("Identificación")
        CampoRegistroPerfil(viewModel.identificacionUsuario, { viewModel.identificacionUsuario = it }, Icons.Default.Badge, KeyboardType.Number)

        FieldLabelPerfil("Teléfono")
        CampoRegistroPerfil(viewModel.telefonoUsuario, { viewModel.telefonoUsuario = it }, Icons.Default.Phone, KeyboardType.Phone)

        FieldLabelPerfil("Email institucional")
        CampoRegistroPerfil(viewModel.emailUsuario, { viewModel.emailUsuario = it }, Icons.Default.Email, KeyboardType.Email)
        
        FieldLabelPerfil("Tipo de Usuario")
        Row(modifier = Modifier.fillMaxWidth()) {
            SelectorTipoPerfil("Estudiante", Icons.Default.School, viewModel.tipoUsuario == "ESTUDIANTE", { viewModel.tipoUsuario = "ESTUDIANTE" }, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            SelectorTipoPerfil("Egresado", Icons.Default.WorkspacePremium, viewModel.tipoUsuario == "EGRESADO", { viewModel.tipoUsuario = "EGRESADO" }, Modifier.weight(1f))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FondoGris)
        
        Text("DETALLES ACADÉMICOS", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelLarge)
        
        FieldLabelPerfil("Universidad / Institución")
        CampoRegistroPerfil(viewModel.universidad, { viewModel.universidad = it }, Icons.Default.LocationCity)
        
        FieldLabelPerfil(if (viewModel.tipoUsuario == "ESTUDIANTE") "Carrera" else "Título Obtenido")
        DropdownCampoPerfil(viewModel.carrera, { viewModel.carrera = it }, viewModel.opcionesCarreras, Icons.Default.MenuBook, isSearchable = true)
        
        if (viewModel.tipoUsuario == "ESTUDIANTE") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    FieldLabelPerfil("Semestre")
                    DropdownCampoPerfil(viewModel.semestre, { viewModel.semestre = it }, viewModel.opcionesSemestres, Icons.Default.CalendarMonth)
                }
                Column(Modifier.weight(1f)) {
                    FieldLabelPerfil("Promedio")
                    CampoRegistroPerfil(viewModel.promedio, { viewModel.promedio = it }, Icons.Default.Star, KeyboardType.Decimal)
                }
            }
        }

        Text("DISPONIBILIDAD Y SKILLS", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelLarge)
        
        FieldLabelPerfil("Disponibilidad horaria")
        DropdownCampoPerfil(viewModel.disponibilidad, { viewModel.disponibilidad = it }, viewModel.opcionesDisponibilidad, Icons.Default.Timer)
        
        FieldLabelPerfil("Hoja de Vida (URL)")
        CampoRegistroPerfil(viewModel.cvUrl, { viewModel.cvUrl = it }, Icons.Default.Link, KeyboardType.Uri)

        FieldLabelPerfil("Agregar Habilidades")
        OutlinedTextField(
            value = skillInput,
            onValueChange = { if (it.length <= 30) skillInput = it },
            placeholder = { Text("Ej. Java, Liderazgo...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    if (skillInput.isNotBlank()) {
                        val lista = viewModel.habilidades.split(",").map { it.trim() }.toMutableList()
                        if (!lista.contains(skillInput.trim())) {
                            lista.add(skillInput.trim())
                            viewModel.habilidades = lista.filter { it.isNotEmpty() }.joinToString(", ")
                        }
                        skillInput = ""
                    }
                }) { Icon(Icons.Default.Add, null) }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (skillInput.isNotBlank()) {
                    val lista = viewModel.habilidades.split(",").map { it.trim() }.toMutableList()
                    if (!lista.contains(skillInput.trim())) {
                        lista.add(skillInput.trim())
                        viewModel.habilidades = lista.filter { it.isNotEmpty() }.joinToString(", ")
                    }
                    skillInput = ""
                }
            })
        )
        
        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.sugerenciasHabilidades.forEach { skill ->
                val isSelected = viewModel.habilidades.split(",").map { it.trim() }.contains(skill)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val lista = viewModel.habilidades.split(",").map { it.trim() }.toMutableList()
                        if (lista.contains(skill)) lista.remove(skill) else lista.add(skill)
                        viewModel.habilidades = lista.filter { it.isNotEmpty() }.joinToString(", ")
                    },
                    label = { Text(skill, fontSize = 12.sp) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Experiencia Laboral
        Row(verticalAlignment = Alignment.CenterVertically) {
            FieldLabelPerfil("Experiencia Laboral")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = viewModel.tieneExperiencia,
                onCheckedChange = { viewModel.tieneExperiencia = it }
            )
        }

        if (viewModel.tieneExperiencia) {
            viewModel.listaExperiencia.forEachIndexed { index, exp ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoGris),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Experiencia #${index + 1}", fontWeight = FontWeight.Bold, color = AzulOscuro)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.listaExperiencia.removeAt(index) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }

                        OutlinedTextField(
                            value = exp.empresa,
                            onValueChange = { viewModel.listaExperiencia[index] = exp.copy(empresa = it) },
                            label = { Text("Empresa") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = exp.cargo,
                            onValueChange = { viewModel.listaExperiencia[index] = exp.copy(cargo = it) },
                            label = { Text("Cargo") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = exp.duracion,
                            onValueChange = { viewModel.listaExperiencia[index] = exp.copy(duracion = it) },
                            label = { Text("Duración (ej. 1 año, 6 meses)") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    viewModel.listaExperiencia.add(com.example.frontend_bolsa_empleo_universitaria.viewModel.ExperienciaLaboral())
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Text(" Agregar otra experiencia")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFFBC02D))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmación Requerida", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                }
                Text("Ingresa tu contraseña actual para guardar los cambios.", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                CampoPasswordPerfil("Contraseña actual", viewModel.passwordUsuario, { viewModel.passwordUsuario = it })
            }
        }
        
        if (viewModel.uiState is PerfilState.Error) {
            Text(
                (viewModel.uiState as PerfilState.Error).mensaje, 
                color = Color.Red, 
                fontSize = 13.sp, 
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp), 
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun SeccionPerfil(titulo: String, icono: ImageVector, contenido: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = AzulMedio, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(titulo, fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = FondoGris),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                contenido()
            }
        }
    }
}

@Composable
fun InfoRowPerfil(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextoGrisPerfil, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(if (value.isBlank()) "No especificado" else value, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black)
    }
}

@Composable
fun CampoRegistroPerfil(value: String, onValueChange: (String) -> Unit, icono: ImageVector, kType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        leadingIcon = { Icon(icono, null, modifier = Modifier.size(20.dp), tint = Color.LightGray) },
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = kType, imeAction = ImeAction.Next), 
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulMedio,
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}

@Composable
fun CampoPasswordPerfil(label: String, value: String, onValueChange: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value, onValueChange = onValueChange, placeholder = { Text(label) },
        trailingIcon = { IconButton(onClick = { visible = !visible }) { Icon(if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color.Gray) } },
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), 
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCampoPerfil(value: String, onValueChange: (String) -> Unit, opciones: List<String>, icono: ImageVector, isSearchable: Boolean = false) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value, 
            onValueChange = { if (isSearchable) onValueChange(it) }, 
            readOnly = !isSearchable,
            leadingIcon = { Icon(icono, null, modifier = Modifier.size(20.dp), tint = Color.LightGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(), 
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulMedio,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val filtered = if (isSearchable) opciones.filter { it.contains(value, ignoreCase = true) } else opciones
            filtered.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt, style = MaterialTheme.typography.bodyMedium) }, 
                    onClick = { onValueChange(opt); expanded = false }
                )
            }
        }
    }
}

@Composable
fun SelectorTipoPerfil(titulo: String, icono: ImageVector, sel: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = if (sel) AzulOscuro else Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (sel) AzulOscuro else Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, null, tint = if (sel) Color.White else Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(titulo, color = if (sel) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun FieldLabelPerfil(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun DialogoCerrarSesion(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, 
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Cerrar Sesión") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("¿Cerrar Sesión?", fontWeight = FontWeight.Bold) }, 
        text = { Text("Tu sesión actual finalizará.") },
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {

    // Usuario de prueba
    val usuarioMock = Usuario(
        idUsuario = 1,
        nombre = "Mónica",
        apellido = "Poveda",
        email = "monica@uni.edu.co",
        telefono = "3001234567",
        tipoUsuario = "ESTUDIANTE",
        fechaRegistro = "2025-01-01",
        estado = true,
        password = "1234"
    )

    // ViewModel falso (mock manual)
    val viewModel = object : PerfilViewModel() {

        init {
            // Estado inicial de prueba
            nombreUsuario = "Mónica Poveda"
            emailUsuario = "monica@uni.edu.co"
            identificacionUsuario = "123456789"
            telefonoUsuario = "3001234567"
            tipoUsuario = "ESTUDIANTE"

            universidad = "Universidad de Cundinamarca"
            carrera = "Ingeniería de Sistemas"
            semestre = "8"
            promedio = "4.2"

            disponibilidad = "Tiempo completo"
            habilidades = "Java, Spring Boot, SQL"
            cvUrl = "https://mi-cv.com"

            tieneExperiencia = true

            listaExperiencia.add(
                com.example.frontend_bolsa_empleo_universitaria.viewModel.ExperienciaLaboral(
                    empresa = "Google",
                    cargo = "Intern",
                    duracion = "6 meses"
                )
            )
        }
    }

    MaterialTheme {
        PerfilScreen(
            usuario = usuarioMock,
            viewModel = viewModel,
            onBack = {},
            onLogout = {}
        )
    }
}

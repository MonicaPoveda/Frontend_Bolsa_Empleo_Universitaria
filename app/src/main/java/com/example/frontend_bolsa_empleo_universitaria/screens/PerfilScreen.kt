package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.frontend_bolsa_empleo_universitaria.viewModel.ExperienciaLaboral
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModel

private val AzulOscuro = Color(0xFF001F3F)
private val AzulMedio = Color(0xFF0056D2)
private val FondoGris = Color(0xFFF8FAFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    viewModel: PerfilViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToPostulations: () -> Unit = {},
    onUsuarioActualizado: (Usuario) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

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
        containerColor = FondoGris,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "PERFIL UNIVERSITARIO",
                        fontWeight = FontWeight.ExtraBold,
                        color = AzulOscuro,
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (viewModel.isEditing) viewModel.cancelarEdicion() else onBack() }) {
                        Icon(if (viewModel.isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = AzulOscuro)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { mostrarDialogoCerrarSesion = true },
                        modifier = Modifier.padding(end = 8.dp).size(40.dp).background(Color(0xFFFFEBEE), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Salir", tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                listOf(
                    Triple("Buscar", Icons.Default.Search, "busqueda"),
                    Triple("Postulaciones", Icons.Default.AssignmentTurnedIn, "postulaciones"),
                    Triple("Perfil", Icons.Default.Person, "perfil")
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == "perfil",
                        onClick = {
                            when (route) {
                                "busqueda" -> onBack()
                                "postulaciones" -> onNavigateToPostulations()
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AzulOscuro,
                            selectedTextColor = AzulOscuro,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = AzulMedio.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Principal: Foto y Datos Básicos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    if (!viewModel.isEditing) {
                        IconButton(
                            onClick = { viewModel.isEditing = true },
                            modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Perfil", tint = AzulMedio)
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // "Foto" de perfil (Iniciales)
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FondoGris)
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val iniciales = (viewModel.nombre.take(1) + viewModel.apellido.take(1)).uppercase()
                            Text(
                                text = iniciales.ifEmpty { "U" },
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "${viewModel.nombre} ${viewModel.apellido}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AzulOscuro,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 34.sp
                        )

                        Text(
                            text = if (viewModel.tipoUsuario == "EGRESADO") 
                                "Título: ${viewModel.carrera.ifBlank { "No especificado" }}" 
                            else 
                                viewModel.carrera.ifBlank { "Carrera no especificada" },
                            fontSize = 18.sp,
                            color = AzulMedio,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, null, tint = AzulMedio, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = viewModel.universidad.ifBlank { "Universidad no especificada" },
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (viewModel.isEditing) {
                DatosEdicion(viewModel)
                
                Button(
                    onClick = { viewModel.guardarCambiosUsuario { onUsuarioActualizado(it) } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                    shape = RoundedCornerShape(12.dp),
                    enabled = viewModel.uiState !is PerfilState.Loading
                ) {
                    if (viewModel.uiState is PerfilState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                    }
                }
                
                OutlinedButton(
                    onClick = { viewModel.cancelarEdicion() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar", color = AzulOscuro)
                }
            } else {
                // Card: Información de Contacto
                SeccionCardPerfil(
                    titulo = "Información de Contacto",
                    icono = Icons.Default.ContactPage
                ) {
                    ItemInfoPerfil(Icons.Default.Email, "Correo Electrónico", viewModel.emailUsuario)
                    ItemInfoPerfil(Icons.Default.Phone, "Teléfono", viewModel.telefonoUsuario.ifBlank { "No registrado" })
                }

                // Card: Datos Académicos
                SeccionCardPerfil(
                    titulo = "Datos Académicos",
                    icono = Icons.Default.School
                ) {
                    ItemInfoPerfil(Icons.Default.LocationCity, "Universidad", viewModel.universidad.ifBlank { "No especificada" })
                    ItemInfoPerfil(Icons.Default.MenuBook, if (viewModel.tipoUsuario == "EGRESADO") "Título Obtenido" else "Carrera", viewModel.carrera.ifBlank { "No especificada" })
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = AzulMedio, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Estado actual: ", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            text = if (viewModel.tipoUsuario == "ESTUDIANTE") "Estudiante de ${viewModel.semestre}° Semestre" else "Egresado / Graduado",
                            fontWeight = FontWeight.Bold,
                            color = AzulMedio,
                            fontSize = 13.sp
                        )
                    }
                }

                // Card: Experiencia Laboral
                SeccionCardPerfil(
                    titulo = "Trayectoria Profesional",
                    icono = Icons.Default.Work
                ) {
                    if (viewModel.tieneExperiencia && viewModel.listaExperiencia.isNotEmpty()) {
                        viewModel.listaExperiencia.forEachIndexed { index, exp ->
                            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(12.dp).background(AzulMedio, CircleShape))
                                    if (index < viewModel.listaExperiencia.size - 1) {
                                        Box(modifier = Modifier.width(2.dp).weight(1f).background(Color(0xFFE0E0E0)))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(exp.cargo, fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 16.sp)
                                    Text(exp.empresa, color = Color.DarkGray, fontSize = 14.sp)
                                    Text(exp.duracion, color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Text("Sin experiencia laboral registrada.", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                // Card: Habilidades
                SeccionCardPerfil(
                    titulo = "Habilidades y Áreas de Interés",
                    icono = Icons.Default.Psychology
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.habilidades.split(",").forEach { skill ->
                            if (skill.isNotBlank()) {
                                Surface(
                                    color = FondoGris,
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                                ) {
                                    Text(
                                        text = skill.trim(),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Card: Hoja de Vida
                SeccionCardPerfil(
                    titulo = "Documentación",
                    icono = Icons.Default.Folder
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (viewModel.cvUrl.isNotBlank()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        try {
                                            uriHandler.openUri(if (viewModel.cvUrl.startsWith("http")) viewModel.cvUrl else "https://${viewModel.cvUrl}")
                                        } catch (e: Exception) {}
                                    },
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, AzulMedio.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Link, null, tint = AzulMedio, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Ver Hoja de Vida en línea", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AzulMedio)
                                        Text(viewModel.cvUrl, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textDecoration = TextDecoration.Underline)
                                    }
                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = AzulMedio, modifier = Modifier.size(18.dp))
                                }
                            }
                        } else {
                            Text("No se ha proporcionado un enlace a la hoja de vida.", color = Color.Gray, fontSize = 14.sp)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Disponibilidad: ", color = Color.Gray, fontSize = 14.sp)
                            Text(viewModel.disponibilidad.ifBlank { "No definida" }, fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (mostrarDialogoCerrarSesion) DialogoCerrarSesion({ mostrarDialogoCerrarSesion = false }, onLogout)
}

@Composable
fun SeccionCardPerfil(
    titulo: String,
    icono: ImageVector? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icono != null) {
                    Box(
                        modifier = Modifier.size(36.dp).background(FondoGris, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icono, null, tint = AzulOscuro, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            contenido()
        }
    }
}

@Composable
fun ItemInfoPerfil(icono: ImageVector, label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", color = Color.Gray, fontSize = 13.sp)
        Text(text = valor, fontWeight = FontWeight.Medium, color = AzulOscuro, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DatosEdicion(viewModel: PerfilViewModel) {
    var skillInput by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        Text("DATOS DE CONTACTO", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelSmall)
        
        CampoRegistroPerfil(viewModel.nombre, { viewModel.nombre = it }, Icons.Default.Person, label = "Nombres", placeholder = "Ej. Juan")
        CampoRegistroPerfil(viewModel.apellido, { viewModel.apellido = it }, Icons.Default.Person, label = "Apellidos", placeholder = "Ej. Pérez")
        CampoRegistroPerfil(viewModel.telefonoUsuario, { viewModel.telefonoUsuario = it }, Icons.Default.Phone, KeyboardType.Phone, label = "Teléfono", placeholder = "Ej. 3001234567")
        CampoRegistroPerfil(viewModel.emailUsuario, { viewModel.emailUsuario = it }, Icons.Default.Email, KeyboardType.Email, label = "Correo Electrónico", placeholder = "ejemplo@correo.com")
        
        Row(modifier = Modifier.fillMaxWidth()) {
            SelectorTipoPerfil("Estudiante", Icons.Default.School, viewModel.tipoUsuario == "ESTUDIANTE", { viewModel.tipoUsuario = "ESTUDIANTE" }, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            SelectorTipoPerfil("Egresado", Icons.Default.WorkspacePremium, viewModel.tipoUsuario == "EGRESADO", { viewModel.tipoUsuario = "EGRESADO" }, Modifier.weight(1f))
        }

        Text("DETALLES ACADÉMICOS", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelSmall)
        
        CampoRegistroPerfil(viewModel.universidad, { viewModel.universidad = it }, Icons.Default.LocationCity, label = "Universidad", placeholder = "Ej. Universidad Nacional")
        DropdownCampoPerfil(viewModel.carrera, { viewModel.carrera = it }, viewModel.opcionesCarreras, Icons.Default.MenuBook, isSearchable = true, label = if (viewModel.tipoUsuario == "EGRESADO") "Título Obtenido" else "Carrera", placeholder = "Selecciona o escribe")
        
        if (viewModel.tipoUsuario == "ESTUDIANTE") {
            DropdownCampoPerfil(viewModel.semestre, { viewModel.semestre = it }, viewModel.opcionesSemestres, Icons.Default.CalendarMonth, label = "Semestre Actual")
        }

        Text("DETALLES PROFESIONALES", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelSmall)
        
        CampoRegistroPerfil(viewModel.cvUrl, { viewModel.cvUrl = it }, Icons.Default.Link, KeyboardType.Uri, label = "Enlace Hoja de Vida", placeholder = "https://drive.google.com/...")

        DropdownCampoPerfil(viewModel.disponibilidad, { viewModel.disponibilidad = it }, viewModel.opcionesDisponibilidad, Icons.Default.Timer, label = "Disponibilidad", placeholder = "Selecciona disponibilidad")

        Text("EXPERIENCIA LABORAL", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelSmall)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = viewModel.tieneExperiencia, onCheckedChange = { viewModel.tieneExperiencia = it })
            Text("Tengo experiencia laboral", fontSize = 14.sp)
        }

        if (viewModel.tieneExperiencia) {
            viewModel.listaExperiencia.forEachIndexed { index, exp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoGris)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Experiencia #${index + 1}", fontWeight = FontWeight.Bold, color = AzulMedio)
                            IconButton(onClick = { viewModel.listaExperiencia.removeAt(index) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        CampoRegistroPerfil(exp.empresa, { viewModel.listaExperiencia[index] = exp.copy(empresa = it) }, Icons.Default.Business, label = "Empresa", placeholder = "Nombre de la empresa")
                        Spacer(modifier = Modifier.height(12.dp))
                        CampoRegistroPerfil(exp.cargo, { viewModel.listaExperiencia[index] = exp.copy(cargo = it) }, Icons.Default.Work, label = "Cargo", placeholder = "Cargo ocupado")
                        Spacer(modifier = Modifier.height(12.dp))
                        CampoRegistroPerfil(exp.duracion, { viewModel.listaExperiencia[index] = exp.copy(duracion = it) }, Icons.Default.Timer, label = "Duración", placeholder = "Ej. 1 año, 6 meses")
                    }
                }
            }
            Button(
                onClick = { viewModel.listaExperiencia.add(ExperienciaLaboral()) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Experiencia")
            }
        }

        Text("HABILIDADES", fontWeight = FontWeight.ExtraBold, color = AzulOscuro, style = MaterialTheme.typography.labelSmall)

        OutlinedTextField(
            value = skillInput,
            onValueChange = { if (it.length <= 30) skillInput = it },
            placeholder = { Text("Agregar Habilidad...") },
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
            }
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.habilidades.split(",").forEach { skill ->
                if (skill.isNotBlank()) {
                    InputChip(
                        selected = true,
                        onClick = {
                            val lista = viewModel.habilidades.split(",").map { it.trim() }.toMutableList()
                            lista.remove(skill.trim())
                            viewModel.habilidades = lista.filter { it.isNotEmpty() }.joinToString(", ")
                        },
                        label = { Text(skill.trim()) },
                        trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }
        
        if (viewModel.uiState is PerfilState.Error) {
            Text(
                (viewModel.uiState as PerfilState.Error).mensaje, 
                color = Color.Red, 
                fontSize = 12.sp, 
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CampoRegistroPerfil(
    value: String, 
    onValueChange: (String) -> Unit, 
    icono: ImageVector, 
    kType: KeyboardType = KeyboardType.Text,
    label: String = "",
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = if (label.isNotEmpty()) { { Text(label) } } else null,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
        leadingIcon = { Icon(icono, null, modifier = Modifier.size(20.dp), tint = Color.LightGray) },
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = kType, imeAction = ImeAction.Next), 
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulMedio,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
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
fun DropdownCampoPerfil(
    value: String, 
    onValueChange: (String) -> Unit, 
    opciones: List<String>, 
    icono: ImageVector, 
    isSearchable: Boolean = false,
    label: String = "",
    placeholder: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value, 
            onValueChange = { if (isSearchable) onValueChange(it) }, 
            readOnly = !isSearchable,
            label = if (label.isNotEmpty()) { { Text(label) } } else null,
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(icono, null, modifier = Modifier.size(20.dp), tint = Color.LightGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(), 
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulMedio,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) }, 
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

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    val usuarioMock = Usuario(idUsuario = 1, nombre = "Andrés Felipe", apellido = "Gómez", email = "andres.gomez@uni.edu.co", tipoUsuario = "ESTUDIANTE", telefono = "", fechaRegistro = "", estado = true, password = "")
    val viewModel = object : PerfilViewModel() {
        init {
            nombre = "Andrés Felipe"
            apellido = "Gómez"
            emailUsuario = "andres.gomez@uni.edu.co"
            carrera = "Ingeniería de Sistemas"
            universidad = "Universidad Nacional de Colombia"
            semestre = "8"
            habilidades = "Desarrollo Web, Bases de Datos, Inteligencia Artificial, Cloud"
        }
    }
    MaterialTheme {
        PerfilScreen(usuario = usuarioMock, viewModel = viewModel, onBack = {}, onLogout = {})
    }
}

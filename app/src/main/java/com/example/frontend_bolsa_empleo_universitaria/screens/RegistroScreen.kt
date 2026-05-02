package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.viewModel.ExperienciaLaboral
import com.example.frontend_bolsa_empleo_universitaria.viewModel.RegistroState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.RegistroViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onNavigateBack: () -> Unit,
    onRegistroSuccess: (com.example.frontend_bolsa_empleo_universitaria.model.Usuario) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    val uiState = viewModel.uiState
    val primaryBlue = Color(0xFF001F3F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header con pasos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val pasoActualStr = "Paso ${viewModel.pasoActual} de 2"
            val tituloPaso = if (viewModel.pasoActual == 2) "PERFIL PROFESIONAL" else "DATOS BÁSICOS"

            Column {
                Text(pasoActualStr, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    if (viewModel.pasoActual == 2) viewModel.volverAlPaso1() else onNavigateBack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                    Text(" Volver", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
            Text(tituloPaso, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = primaryBlue)
        }

        // Barra de progreso visual
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Box(modifier = Modifier.weight(1f).height(4.dp).background(primaryBlue))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.weight(1f).height(4.dp).background(if (viewModel.pasoActual == 2) primaryBlue else Color(0xFFE0E0E0)))
        }

        AnimatedContent(
            targetState = viewModel.pasoActual == 2,
            label = "RegistroSteps"
        ) { isPaso2 ->
            if (isPaso2) {
                Paso2Perfil(viewModel)
            } else {
                Paso1DatosBasicos(viewModel, onNavigateBack)
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is RegistroState.Success) {
            onRegistroSuccess(uiState.usuario)
        }
    }
}

@Composable
fun Paso1DatosBasicos(viewModel: RegistroViewModel, onNavigateBack: () -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF001F3F)
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Text(
            text = "Crea tu Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Ingresa tus datos para comenzar a explorar oportunidades.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        if (viewModel.uiState is RegistroState.Error) {
            Text(
                (viewModel.uiState as RegistroState.Error).mensaje,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Perfil Académico", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Surface(
                modifier = Modifier.weight(1f).clickable { viewModel.tipoUsuario = "ESTUDIANTE" },
                color = if (viewModel.tipoUsuario == "ESTUDIANTE") primaryBlue else Color.White,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (viewModel.tipoUsuario == "ESTUDIANTE") primaryBlue else Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, null, tint = if (viewModel.tipoUsuario == "ESTUDIANTE") Color.White else Color.Gray)
                    Text(
                        "Estudiante\nActivo",
                        textAlign = TextAlign.Center,
                        color = if (viewModel.tipoUsuario == "ESTUDIANTE") Color.White else Color.Black,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.weight(1f).clickable { viewModel.tipoUsuario = "EGRESADO" },
                color = if (viewModel.tipoUsuario == "EGRESADO") primaryBlue else Color.White,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (viewModel.tipoUsuario == "EGRESADO") primaryBlue else Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = if (viewModel.tipoUsuario == "EGRESADO") Color.White else Color.Gray)
                    Text(
                        "Egresado\nGraduado",
                        textAlign = TextAlign.Center,
                        color = if (viewModel.tipoUsuario == "EGRESADO") Color.White else Color.Black,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FieldLabel("Nombre completo")
        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            placeholder = { Text("Ej. Juan Pérez") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel("Identificación")
        OutlinedTextField(
            value = viewModel.identificacion,
            onValueChange = { viewModel.identificacion = it },
            placeholder = { Text("Número de documento") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel("Teléfono de contacto")
        OutlinedTextField(
            value = viewModel.telefono,
            onValueChange = { viewModel.telefono = it },
            placeholder = { Text("Ej. 3001234567") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color.LightGray) },
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel("Correo institucional")
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            placeholder = { Text("usuario@universidad.edu.co") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.LightGray) },
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel("Contraseña")
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.registrarPaso1() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
            enabled = viewModel.uiState !is RegistroState.Loading
        ) {
            if (viewModel.uiState is RegistroState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Siguiente", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("¿Ya tienes una cuenta? ", color = Color.Gray)
            Text("Inicia sesión", color = primaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigateBack() })
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Paso2Perfil(viewModel: RegistroViewModel) {
    val tipoUsuario = viewModel.datosUsuario?.tipoUsuario ?: "ESTUDIANTE"
    val esEstudiante = tipoUsuario == "ESTUDIANTE"
    val primaryBlue = Color(0xFF001F3F)
    val scrollState = rememberScrollState()

    // Estados para los dropdowns
    var expandedCarrera by remember { mutableStateOf(false) }
    var expandedSemestre by remember { mutableStateOf(false) }
    var expandedDispo by remember { mutableStateOf(false) }
    var skillInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Text(
            text = if (esEstudiante) "Completa tu información académica" else "Completa tu información profesional",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Esta información ayudará a las empresas a encontrarte fácilmente.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Universidad
        FieldLabel("Universidad / Institución")
        OutlinedTextField(
            value = viewModel.universidad,
            onValueChange = { viewModel.universidad = it },
            placeholder = { Text("Ej. Universidad Nacional") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.LocationCity, null, tint = Color.LightGray) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Carrera Dropdown
        FieldLabel(if (esEstudiante) "Carrera o Programa de Estudio" else "Título Obtenido")
        ExposedDropdownMenuBox(
            expanded = expandedCarrera,
            onExpandedChange = { expandedCarrera = !expandedCarrera }
        ) {
            OutlinedTextField(
                value = viewModel.carrera,
                onValueChange = { viewModel.carrera = it },
                placeholder = { Text("Selecciona o escribe tu carrera") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                leadingIcon = { Icon(Icons.Default.School, null, tint = Color.LightGray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCarrera) },
                shape = RoundedCornerShape(8.dp),
                readOnly = false // Permitimos escribir si no está en la lista
            )
            ExposedDropdownMenu(
                expanded = expandedCarrera,
                onDismissRequest = { expandedCarrera = false }
            ) {
                viewModel.opcionesCarreras.filter { it.contains(viewModel.carrera, ignoreCase = true) }.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            viewModel.carrera = selection
                            expandedCarrera = false
                        }
                    )
                }
            }
        }

        if (esEstudiante) {
            Spacer(modifier = Modifier.height(16.dp))

            // Semestre Dropdown
            FieldLabel("Semestre Actual")
            ExposedDropdownMenuBox(
                expanded = expandedSemestre,
                onExpandedChange = { expandedSemestre = !expandedSemestre }
            ) {
                OutlinedTextField(
                    value = viewModel.semestre,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona tu semestre") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = Color.LightGray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSemestre) },
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedSemestre,
                    onDismissRequest = { expandedSemestre = false }
                ) {
                    viewModel.opcionesSemestres.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = {
                                viewModel.semestre = selection
                                expandedSemestre = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel("Promedio Acumulado")
            OutlinedTextField(
                value = viewModel.promedio,
                onValueChange = { viewModel.promedio = it },
                placeholder = { Text("Ej. 4.2") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.StarBorder, null, tint = Color.LightGray) },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Disponibilidad Dropdown
        FieldLabel("Disponibilidad")
        ExposedDropdownMenuBox(
            expanded = expandedDispo,
            onExpandedChange = { expandedDispo = !expandedDispo }
        ) {
            OutlinedTextField(
                value = viewModel.disponibilidad,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona disponibilidad") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = Color.LightGray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDispo) },
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedDispo,
                onDismissRequest = { expandedDispo = false }
            ) {
                viewModel.opcionesDisponibilidad.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            viewModel.disponibilidad = selection
                            expandedDispo = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CV URL
        FieldLabel("Enlace Hoja de Vida (Drive, Cloud, etc.)")
        OutlinedTextField(
            value = viewModel.cvUrl,
            onValueChange = { viewModel.cvUrl = it },
            placeholder = { Text("https://drive.google.com/...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Link, null, tint = Color.LightGray) },
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Habilidades
        FieldLabel("Habilidades y Palabras Clave")
        Text("Selecciona sugerencias o agrega palabras clave (máx 30 caracteres cada una).", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        OutlinedTextField(
            value = skillInput,
            onValueChange = { if (it.length <= 30) skillInput = it },
            placeholder = { Text("Agregar habilidad...") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    if (skillInput.isNotBlank() && !viewModel.areasInteres.contains(skillInput.trim())) {
                        viewModel.areasInteres.add(skillInput.trim())
                        skillInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, null)
                }
            },
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (skillInput.isNotBlank() && !viewModel.areasInteres.contains(skillInput.trim())) {
                    viewModel.areasInteres.add(skillInput.trim())
                    skillInput = ""
                }
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(modifier = Modifier.fillMaxWidth()) {
            // Sugerencias
            viewModel.sugerenciasHabilidades.forEach { skill ->
                val isSelected = viewModel.areasInteres.contains(skill)
                FilterChip(
                    selected = isSelected,
                    onClick = { if (isSelected) viewModel.areasInteres.remove(skill) else viewModel.areasInteres.add(skill) },
                    label = { Text(skill) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            // Personalizadas agregadas
            viewModel.areasInteres.filter { !viewModel.sugerenciasHabilidades.contains(it) }.forEach { skill ->
                InputChip(
                    selected = true,
                    onClick = { viewModel.areasInteres.remove(skill) },
                    label = { Text(skill) },
                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Experiencia Laboral
        Row(verticalAlignment = Alignment.CenterVertically) {
            FieldLabel("Experiencia Laboral")
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Experiencia #${index + 1}", fontWeight = FontWeight.Bold, color = primaryBlue)
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
                    viewModel.listaExperiencia.add(ExperienciaLaboral())
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Text(" Agregar otra experiencia")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.volverAlPaso1() },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Atrás", color = Color.Black)
            }
            Button(
                onClick = { viewModel.finalizarRegistro() },
                modifier = Modifier.weight(1.5f).height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                enabled = viewModel.uiState !is RegistroState.Loading
            ) {
                if (viewModel.uiState is RegistroState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Finalizar Registro", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        if (viewModel.uiState is RegistroState.Error) {
            Text(
                (viewModel.uiState as RegistroState.Error).mensaje,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(modifier = modifier) {
        content()
    }
}

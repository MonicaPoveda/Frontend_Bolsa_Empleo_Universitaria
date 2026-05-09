package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.text.ifEmpty

private val BlueGradientStart = Color(0xFF0056D2)

@Composable
fun AgregarOfertaScreen(
    padding: PaddingValues,
    idEmpresa: Long,
    onOfertaAgregada: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var salario by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf("") }

    // Estados para la fecha de cierre
    var selectedYear by remember { mutableStateOf(2026) }
    var selectedMonth by remember { mutableStateOf(5) }
    var selectedDay by remember { mutableStateOf(30) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val scope = rememberCoroutineScope()

    // Fecha actual para fechaPublicacion (fija)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val fechaActual = dateFormat.format(calendar.time)

    // Listas para los selectores
    val years = (2024..2030).toList()
    val months = (1..12).toList()
    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    // Calcular días máximos según mes y año seleccionado
    val maxDays = when (selectedMonth) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || selectedYear % 400 == 0) 29 else 28
        else -> 31
    }

    val days = (1..maxDays).toList()

    // Formatear fecha seleccionada
    val fechaCierreSeleccionada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp)
    ) {
        Text(
            text = "Publicar Nueva Oferta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la oferta *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Área / Cargo
        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Área / Cargo *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Salario y Modalidad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = salario,
                onValueChange = { salario = it },
                label = { Text("Salario (USD) *") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = modalidad,
                onValueChange = { modalidad = it },
                label = { Text("Modalidad *") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fecha de Cierre (con selectores independientes)
        Text(
            text = "Fecha de cierre *",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selector de Año
            DateSelector(
                value = selectedYear.toString(),
                label = "Año",
                items = years.map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = {
                    selectedYear = it.toInt()
                }
            )

            // Selector de Mes
            DateSelector(
                value = monthNames[selectedMonth - 1],
                label = "Mes",
                items = monthNames,
                modifier = Modifier.weight(1f),
                onItemSelected = {
                    selectedMonth = monthNames.indexOf(it) + 1
                }
            )

            // Selector de Día
            DateSelector(
                value = selectedDay.toString(),
                label = "Día",
                items = days.map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = {
                    selectedDay = it.toInt()
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Mostrar fecha seleccionada
        Text(
            text = "Fecha seleccionada: $fechaCierreSeleccionada",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción del puesto *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 4
        )

        // Mostrar fecha de publicación (solo informativa)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "📅 Fecha de publicación: $fechaActual (automática)",
            fontSize = 11.sp,
            color = Color.Gray
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Publicar
        Button(
            onClick = {
                when {
                    titulo.isBlank() -> errorMessage = "Ingresa el título de la oferta"
                    area.isBlank() -> errorMessage = "Ingresa el área/cargo"
                    salario.isBlank() -> errorMessage = "Ingresa el salario"
                    descripcion.isBlank() -> errorMessage = "Ingresa la descripción"
                    else -> {
                        isLoading = true
                        errorMessage = null

                        val nuevaOferta = OfertaLaboral(
                            idOferta = 0,
                            titulo = titulo,
                            descripcion = descripcion,
                            area = area,
                            salario = salario.toDoubleOrNull() ?: 0.0,
                            modalidad = modalidad.ifEmpty { "Presencial" },
                            fechaPublicacion = fechaActual,
                            fechaCierre = fechaCierreSeleccionada,
                            estado = true,
                            idEmpresa = idEmpresa
                        )

                        println("📝 Creando nueva oferta:")
                        println("   Título: ${nuevaOferta.titulo}")
                        println("   Fecha Publicación: ${nuevaOferta.fechaPublicacion}")
                        println("   Fecha Cierre: ${nuevaOferta.fechaCierre}")

                        scope.launch {
                            try {
                                val response = repository.guardarOferta(nuevaOferta)

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response != null) {
                                        println("✅ Oferta creada exitosamente")
                                        showSuccess = true
                                    } else {
                                        errorMessage = "Error al crear la oferta"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMessage = "Error de conexión: ${e.message}"
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = BlueGradientStart)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Oferta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Diálogo de éxito
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("¡Oferta Publicada!") },
            text = { Text("La oferta ha sido publicada exitosamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onOfertaAgregada()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun DateSelector(
    value: String,
    label: String,
    items: List<String>,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Seleccionar",
                modifier = Modifier.clickable { expanded = true }
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center
        )
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.height(200.dp)
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item, fontSize = 14.sp) },
                onClick = {
                    onItemSelected(item)
                    expanded = false
                }
            )
        }
    }
}
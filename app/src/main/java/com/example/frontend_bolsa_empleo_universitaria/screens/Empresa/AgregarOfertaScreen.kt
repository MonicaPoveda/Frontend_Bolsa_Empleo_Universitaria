package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralRequest
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.text.ifEmpty

private val BlueGradientStart = UniEmpleoColors.Blue

@Composable
fun AgregarOfertaScreen(
    padding: PaddingValues,
    idEmpresa: Long,
    onOfertaAgregada: () -> Unit
) {
    val context = LocalContext.current
    val token = remember { Token(context) }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var salario by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf("") }

    var selectedYear by remember { mutableStateOf(2026) }
    var selectedMonth by remember { mutableStateOf(5) }
    var selectedDay by remember { mutableStateOf(30) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val scope = rememberCoroutineScope()

    val fechaActual = remember {
        DateFormat.format("yyyy-MM-dd", Date()).toString()
    }

    val years = (2024..2030).toList()
    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val maxDays = remember(selectedYear, selectedMonth) {
        when (selectedMonth) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || selectedYear % 400 == 0) 29 else 28
            else -> 31
        }
    }

    val days = (1..maxDays).toList()
    val fechaCierreSeleccionada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)

    LaunchedEffect(Unit) {
        println("=== DIAGNÓSTICO AL ABRIR PANTALLA ===")
        println("Token: ${token.getToken()?.take(60) ?: "NULL ❌"}")
        println("Rol: '${token.getUserRole()}'")
        println("EmpresaId: ${token.getEmpresaId()}")
        println("isEmpresa(): ${token.isEmpresa()}")
        println("idEmpresa param: $idEmpresa")
        println("======================================")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Publicar Nueva Oferta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la oferta *", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = BlueGradientStart,
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Área / Cargo *", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = BlueGradientStart,
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = salario,
                onValueChange = { salario = it },
                label = { Text("Salario (USD) *", color = Color.DarkGray) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = BlueGradientStart,
                    unfocusedBorderColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = modalidad,
                onValueChange = { modalidad = it },
                label = { Text("Modalidad *", color = Color.DarkGray) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = BlueGradientStart,
                    unfocusedBorderColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            DateSelector(
                value = selectedYear.toString(),
                label = "Año",
                items = years.map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedYear = it.toInt() }
            )

            DateSelector(
                value = monthNames[selectedMonth - 1],
                label = "Mes",
                items = monthNames,
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedMonth = monthNames.indexOf(it) + 1 }
            )

            DateSelector(
                value = selectedDay.toString(),
                label = "Día",
                items = days.map { it.toString() },
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedDay = it.toInt() }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "📅 Fecha de cierre seleccionada: $fechaCierreSeleccionada",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción del puesto *", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = BlueGradientStart,
                unfocusedBorderColor = Color.Gray
            )
        )

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

        Spacer(modifier = Modifier.height(32.dp))

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

                        val nuevaOferta = OfertaLaboralRequest(
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

                        scope.launch {
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    repository.guardarOferta(nuevaOferta)
                                }
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
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueGradientStart,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Publicar Oferta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("¡Oferta Publicada!", fontWeight = FontWeight.Bold) },
            text = { Text("La oferta ha sido publicada exitosamente.", fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onOfertaAgregada()
                }) {
                    Text("Aceptar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 12.sp, color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar",
                    tint = BlueGradientStart,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center,
                color = Color.Black
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueGradientStart,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White, RoundedCornerShape(12.dp)),
            containerColor = Color.White
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontSize = 14.sp,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                )
            }
        }
    }
}

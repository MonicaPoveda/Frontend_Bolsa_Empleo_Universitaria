package com.example.frontend_bolsa_empleo_universitaria.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import kotlinx.coroutines.launch

@Composable
fun ProfilePhotoSection(
    photoUrl: String,
    title: String = "Foto de perfil",
    placeholderIcon: ImageVector = Icons.Default.Person,
    editable: Boolean = true,
    hasUploadedPhoto: Boolean = false,
    onUpload: suspend (Uri, Boolean) -> Result<Unit>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    // Cambiado mutableLongStateOf → mutableStateOf para evitar problemas de compatibilidad
    var cacheBuster by remember { mutableStateOf(System.currentTimeMillis()) }
    var localHasPhoto by remember { mutableStateOf(hasUploadedPhoto) }

    LaunchedEffect(hasUploadedPhoto) {
        localHasPhoto = hasUploadedPhoto
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            isUploading = true
            errorMessage = null
            successMessage = null
            val result = onUpload(uri, localHasPhoto)
            isUploading = false
            result.onSuccess {
                localHasPhoto = true
                cacheBuster = System.currentTimeMillis()
                successMessage = "Foto actualizada correctamente"
            }.onFailure {
                errorMessage = it.message ?: "No se pudo subir la foto"
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BolsaTokens.Palette.TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.Center) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("$photoUrl${if (cacheBuster > 0) "?t=$cacheBuster" else ""}")
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(BolsaTokens.Palette.Background),
                    contentScale = ContentScale.Crop,
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BolsaTokens.Palette.PrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(placeholderIcon, null, tint = BolsaTokens.Palette.Primary, modifier = Modifier.size(48.dp))
                        }
                    },
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
                        }
                    }
                )
                if (isUploading) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Formatos: JPG, PNG, WEBP, GIF. Máx. 10 MB.",
                fontSize = 12.sp,
                color = BolsaTokens.Palette.TextSecondary,
                textAlign = TextAlign.Center
            )

            if (editable) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { pickerLauncher.launch("image/*") },
                    enabled = !isUploading
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (localHasPhoto) "Cambiar foto" else "Seleccionar foto")
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = BolsaTokens.Palette.Error, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            successMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = BolsaTokens.Palette.Success, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun EmpresaDocumentSection(
    editable: Boolean = true,
    hasDocument: Boolean = false,
    onUpload: suspend (Uri, Boolean) -> Result<Unit>,
    onViewDocument: suspend () -> Result<Unit>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var isUploading by remember { mutableStateOf(false) }
    var isOpening by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var localHasDocument by remember { mutableStateOf(hasDocument) }

    LaunchedEffect(hasDocument) {
        localHasDocument = hasDocument
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            isUploading = true
            errorMessage = null
            successMessage = null
            val result = onUpload(uri, localHasDocument)
            isUploading = false
            result.onSuccess {
                localHasDocument = true
                successMessage = "Documento subido correctamente"
            }.onFailure {
                errorMessage = it.message ?: "No se pudo subir el documento"
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Documento empresarial (PDF)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Sube el documento legal o de registro de tu empresa. Solo PDF, máx. 10 MB.",
                fontSize = 12.sp,
                color = BolsaTokens.Palette.TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (editable) {
                    Button(
                        onClick = { pdfPicker.launch("application/pdf") },
                        enabled = !isUploading && !isOpening,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (localHasDocument) "Reemplazar PDF" else "Subir PDF")
                        }
                    }
                }
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isOpening = true
                            errorMessage = null
                            onViewDocument().onFailure {
                                errorMessage = it.message ?: "No se pudo abrir el documento"
                            }
                            isOpening = false
                        }
                    },
                    enabled = !isUploading && !isOpening,
                    modifier = if (editable) Modifier.weight(1f) else Modifier.fillMaxWidth()
                ) {
                    if (isOpening) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Ver documento")
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = BolsaTokens.Palette.Error, fontSize = 12.sp)
            }
            successMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = BolsaTokens.Palette.Success, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ProfilePhotoDisplay(
    photoUrl: String,
    cacheBuster: Long = 0L,
    placeholderIcon: ImageVector = Icons.Default.Person,
    size: Int = 90,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val url = if (cacheBuster > 0L) "$photoUrl?t=$cacheBuster" else photoUrl

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Foto de perfil",
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
        contentScale = ContentScale.Crop,
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(placeholderIcon, null, tint = Color.White, modifier = Modifier.size((size / 2).dp))
            }
        },
        loading = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
            }
        }
    )
}
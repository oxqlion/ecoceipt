package com.example.ecoceipt.ui.views.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(
    onImageCaptured: (Uri) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashEnabled by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        // Handle camera binding error
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Receipt Frame Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f) // Receipt-like aspect ratio
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            // Corner indicators
            CornerIndicators()
        }

        // Top Bar
        TopAppBar(
            title = { Text("Scan Receipt") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        camera?.let { cam ->
                            isFlashEnabled = !isFlashEnabled
                            cam.cameraControl.enableTorch(isFlashEnabled)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Capture Button
            Button(
                onClick = {
                    if (!isCapturing) {
                        captureImage(
                            imageCapture = imageCapture,
                            context = context,
                            cameraExecutor = cameraExecutor,
                            onImageCaptured = onImageCaptured,
                            onCaptureStart = { isCapturing = true },
                            onCaptureComplete = { isCapturing = false }
                        )
                    }
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = !isCapturing
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Instructions Text
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Position the receipt within the frame and tap to capture",
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CornerIndicators() {
    val cornerSize = 20.dp
    val strokeWidth = 3.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset((-2).dp, (-2).dp)
        ) {
            Canvas(modifier = Modifier.size(cornerSize)) {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, strokeWidth.toPx()),
                    end = Offset(cornerSize.toPx() * 0.7f, strokeWidth.toPx()),
                    strokeWidth = strokeWidth.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(strokeWidth.toPx(), 0f),
                    end = Offset(strokeWidth.toPx(), cornerSize.toPx() * 0.7f),
                    strokeWidth = strokeWidth.toPx()
                )
            }
        }

        // Top Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(2.dp, (-2).dp)
        ) {
            Canvas(modifier = Modifier.size(cornerSize)) {
                drawLine(
                    color = Color.White,
                    start = Offset(cornerSize.toPx(), strokeWidth.toPx()),
                    end = Offset(cornerSize.toPx() * 0.3f, strokeWidth.toPx()),
                    strokeWidth = strokeWidth.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(cornerSize.toPx() - strokeWidth.toPx(), 0f),
                    end = Offset(cornerSize.toPx() - strokeWidth.toPx(), cornerSize.toPx() * 0.7f),
                    strokeWidth = strokeWidth.toPx()
                )
            }
        }

        // Bottom Left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset((-2).dp, 2.dp)
        ) {
            Canvas(modifier = Modifier.size(cornerSize)) {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, cornerSize.toPx() - strokeWidth.toPx()),
                    end = Offset(cornerSize.toPx() * 0.7f, cornerSize.toPx() - strokeWidth.toPx()),
                    strokeWidth = strokeWidth.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(strokeWidth.toPx(), cornerSize.toPx()),
                    end = Offset(strokeWidth.toPx(), cornerSize.toPx() * 0.3f),
                    strokeWidth = strokeWidth.toPx()
                )
            }
        }

        // Bottom Right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(2.dp, 2.dp)
        ) {
            Canvas(modifier = Modifier.size(cornerSize)) {
                drawLine(
                    color = Color.White,
                    start = Offset(cornerSize.toPx(), cornerSize.toPx() - strokeWidth.toPx()),
                    end = Offset(cornerSize.toPx() * 0.3f, cornerSize.toPx() - strokeWidth.toPx()),
                    strokeWidth = strokeWidth.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(cornerSize.toPx() - strokeWidth.toPx(), cornerSize.toPx()),
                    end = Offset(cornerSize.toPx() - strokeWidth.toPx(), cornerSize.toPx() * 0.3f),
                    strokeWidth = strokeWidth.toPx()
                )
            }
        }
    }
}

private fun captureImage(
    imageCapture: ImageCapture?,
    context: Context,
    cameraExecutor: ExecutorService,
    onImageCaptured: (Uri) -> Unit,
    onCaptureStart: () -> Unit,
    onCaptureComplete: () -> Unit
) {
    val imageCapture = imageCapture ?: return

    onCaptureStart()

    // Create unique filename
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())

    val photoFile = File(
        context.getExternalFilesDir(null),
        "receipt_$name.jpg"
    )

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputFileOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onCaptureComplete()
                onImageCaptured(Uri.fromFile(photoFile))
            }

            override fun onError(exception: ImageCaptureException) {
                onCaptureComplete()
                // Handle error - you might want to show a toast or error message
            }
        }
    )
}
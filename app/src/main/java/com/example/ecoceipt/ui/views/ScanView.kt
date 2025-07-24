package com.example.ecoceipt.ui.views

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecoceipt.ui.views.camera.CameraScreen
import com.example.ecoceipt.ui.viewmodels.OCRViewModel
import com.example.ecoceipt.ui.viewmodels.OCRViewModelFactory

@Composable
fun ScanView(navController: NavController) {

    val context = LocalContext.current
    val factory = remember { OCRViewModelFactory(context) }
    val viewModel: OCRViewModel = viewModel(factory = factory)

    val extractedText by remember { viewModel::extractedText }
    val errorText by remember { viewModel::error }

    LaunchedEffect(extractedText) {
        extractedText?.let {
            navController.navigate("summary/${Uri.encode(it)}")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CameraScreen(
            onImageCaptured = { uri ->
                viewModel.processCapturedImage(uri, context)
            },
            onNavigateBack = {
                Toast.makeText(context, "Back navigation not implemented", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        extractedText?.let {
            Text("Extracted:\n$it")
            Log.d("ScanView", "Extracted Text: $it")
        }

        errorText?.let {
            Text("Error:\n$it")
            Log.e("ScanView", "Error: $it")
        }
    }
}
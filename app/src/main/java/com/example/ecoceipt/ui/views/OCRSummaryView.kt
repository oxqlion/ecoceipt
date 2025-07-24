package com.example.ecoceipt.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tim_sam_2.utils.Screen

@Composable
fun OCRSummaryView(
    extractedText: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = {
            navController.navigate(Screen.Scan.route) {
                popUpTo(Screen.Scan.route) { inclusive = true } // avoid backstack stacking
                launchSingleTop = true
            }
        }) {
            Text("Scan Another Receipt")
        }
        Text("Extracted Text:")
        Text(text = extractedText)

        Spacer(modifier = Modifier.height(24.dp))

    }
}

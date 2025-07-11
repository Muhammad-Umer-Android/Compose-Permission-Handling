package com.application.permissionhandling

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.permissionhandling.ui.theme.PermissionHandlingTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionHandlingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val showDialog = mainViewModel.showDialog.collectAsState().value

                    val launchAppSettings = mainViewModel.launchAppSettings.collectAsState().value

                    val permissionResultActivityLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions(),
                        onResult = { result->
                            permissions.forEach { permission ->
                                if (result[permission] == false) {
                                    if (!shouldShowRequestPermissionRationale(permission)) {
                                        mainViewModel.updateLaunchAppSettings(true)
                                    }
                                    mainViewModel.updateShowDialog(true)
                                }
                            }
                        }
                    )

                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ){
                        Button(onClick = {
                            permissions.forEach { permission ->
                                val isGranted = checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!isGranted){
                                    if (shouldShowRequestPermissionRationale(permission)){
                                        mainViewModel.updateShowDialog(true)
                                    } else {
                                        permissionResultActivityLauncher.launch(permissions)
                                    }
                                }
                            }
                        }
                        ) {
                            Text(text = "Request Permissions")
                        }
                    }

                    if (showDialog) {
                        PermissionDialog(
                            onDismiss = { mainViewModel.updateShowDialog(false) },
                            onConfirm = {
                                mainViewModel.updateShowDialog(false)
                                if (launchAppSettings) {
                                    // Logic to open app settings
                                    startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", packageName, null)
                                        )
                                    )
                                    mainViewModel.updateLaunchAppSettings(false)
                                } else {
                                    permissionResultActivityLauncher.launch(permissions)
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(text = "Ok")
            }
        },
        title = {
            Text(text = "Camera and Microphone Permissions Required",
                fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(text = "This app requires Camera and Microphone permissions to function properly. Please grant the permissions in the app settings.")
        }
    )

}


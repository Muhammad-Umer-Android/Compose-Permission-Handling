package com.application.permissionhandling

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @Author: Umer Dev
 * @Created: 11/07/2025
 * @File: MainViewModel.kt
 */
class MainViewModel : ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _launchAppSettings = MutableStateFlow(false)
    val launchAppSettings = _launchAppSettings.asStateFlow()

    fun updateShowDialog(show: Boolean) {
        _showDialog.value = show
    }

    fun updateLaunchAppSettings(launch: Boolean) {
        _launchAppSettings.value = launch
    }


}
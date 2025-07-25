package io.jadu.remindmex.remindMe.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.jadu.remindmex.remindMe.presentation.screens.ThemeOption

class ThemeViewModel : ViewModel() {
    private val _selectedTheme = mutableStateOf(ThemeOption.SYSTEM)
    val selectedTheme: State<ThemeOption> = _selectedTheme

    fun setTheme(option: ThemeOption) {
        Log.d("ThemeViewModel", "Theme changed to: $option")
        _selectedTheme.value = option
    }
}

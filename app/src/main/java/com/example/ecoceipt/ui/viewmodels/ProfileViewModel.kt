package com.example.ecoceipt.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ecoceipt.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val user: UserModel? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _uiState.value = ProfileUiState(
            user = UserModel(
                name = "Samuel Lie",
                email = "samlie@gmail.com",
                businessName = "Ricebowl Maknyus"
            )
        )
    }
}
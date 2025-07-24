package com.example.ecoceipt.ui.viewmodels

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
        // In the future, this will fetch user data from Firebase.
        // For now, we use a dummy model.
        _uiState.value = ProfileUiState(
            user = UserModel(
                name = "Samuel Lie",
                email = "samlie@gmail.com",
                businessName = "Ricebowl Maknyus"
            )
        )
    }
}
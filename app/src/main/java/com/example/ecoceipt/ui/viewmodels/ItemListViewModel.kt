package com.example.ecoceipt.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.ItemModel
import com.example.ecoceipt.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemListUiState(
    val items: List<ItemModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isSheetOpen: Boolean = false,
    val selectedItem: ItemModel? = null
)

class ItemListViewModel(
    private val repository: ItemRepository = ItemRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemListUiState())
    val uiState: StateFlow<ItemListUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val items = repository.getItems()
                _uiState.update { it.copy(items = items, isLoading = false, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load items: ${e.message}") }
            }
        }
    }

    fun addItem(name: String, priceStr: String, description: String) {
        viewModelScope.launch {
            try {
                val price = priceStr.toDoubleOrNull() ?: 0.0
                val newItem = ItemModel(
                    name = name,
                    price = price,
                    description = description
                )
                val success = repository.addItem(newItem)
                if (success) {
                    loadItems()
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to add item.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun updateItem(id: String, name: String, priceStr: String, description: String) {
        viewModelScope.launch {
            try {
                val originalItem = _uiState.value.items.find { it.id == id } ?: return@launch
                val price = priceStr.toDoubleOrNull() ?: 0.0

                val updatedItem = originalItem.copy(
                    name = name,
                    price = price,
                    description = description,
                    updatedAt = System.currentTimeMillis()
                )

                val success = repository.updateItem(updatedItem)
                if (success) {
                    loadItems()
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to update item.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                val success = repository.deleteItem(itemId)
                if (success) {
                    loadItems()
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to delete item.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun onOpenSheet(item: ItemModel? = null) {
        _uiState.update {
            it.copy(isSheetOpen = true, selectedItem = item)
        }
    }

    fun onDismissSheet() {
        _uiState.update {
            it.copy(isSheetOpen = false, selectedItem = null)
        }
    }
}
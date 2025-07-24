package com.example.ecoceipt.viewmodels

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
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val items = repository.getItems()
                _uiState.update { it.copy(items = items, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to load items: ${e.message}", isLoading = false) }
            }
        }
    }

    fun addItem(name: String, priceStr: String, description: String) {
        viewModelScope.launch {
            try {
                val price = priceStr.toDoubleOrNull() ?: 0.0
                val newItem = ItemModel(name = name, price = price, description = description)
                repository.addItem(newItem)
                fetchItems()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add item: ${e.message}") }
            }
        }
    }

    fun updateItem(id: String, name: String, priceStr: String, description: String) {
        viewModelScope.launch {
            try {
                val price = priceStr.toDoubleOrNull() ?: 0.0
                val itemToUpdate = ItemModel(
                    id = id,
                    name = name,
                    price = price,
                    description = description,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateItem(itemToUpdate)
                fetchItems()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update item: ${e.message}") }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.deleteItem(itemId)
                fetchItems()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete item: ${e.message}") }
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
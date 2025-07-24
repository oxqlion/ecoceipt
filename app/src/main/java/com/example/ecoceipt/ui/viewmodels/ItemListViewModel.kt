package com.example.ecoceipt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.ItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ItemListUiState(
    val items: List<ItemModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isSheetOpen: Boolean = false,
    val selectedItem: ItemModel? = null
)

class ItemListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ItemListUiState())
    val uiState: StateFlow<ItemListUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val dummyItems = createDummyItems()
            _uiState.update { it.copy(items = dummyItems, isLoading = false) }
        }
    }

    // UPDATED: This function is now only for editing an existing item.
    fun onOpenSheet(item: ItemModel) {
        _uiState.update {
            it.copy(isSheetOpen = true, selectedItem = item)
        }
    }

    fun onDismissSheet() {
        _uiState.update {
            it.copy(isSheetOpen = false, selectedItem = null)
        }
    }

    // REMOVED: The addItem function is no longer needed.

    fun updateItem(id: String, name: String, priceStr: String, description: String) {
        val price = priceStr.toDoubleOrNull() ?: 0.0
        _uiState.update { currentState ->
            val updatedItems = currentState.items.map { item ->
                if (item.id == id) {
                    item.copy(
                        name = name,
                        price = price,
                        description = description,
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    item
                }
            }
            currentState.copy(items = updatedItems)
        }
    }

    fun deleteItem(itemId: String) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.filter { it.id != itemId }
            currentState.copy(items = updatedItems)
        }
    }

    private fun createDummyItems(): List<ItemModel> {
        return listOf(
            ItemModel(id = "1", name = "Nasi Goreng Spesial", price = 25000.0, description = "Nasi goreng dengan telur, ayam, dan bakso."),
            ItemModel(id = "2", name = "Mie Ayam Komplit", price = 20000.0, description = "Mie dengan topping ayam, pangsit, dan bakso."),
            ItemModel(id = "3", name = "Es Teh Manis", price = 5000.0, description = ""), // Example with no description
            ItemModel(id = "4", name = "Sate Ayam (10 tusuk)", price = 30000.0, description = "Sate ayam dengan bumbu kacang khas.")
        )
    }
}


//package com.example.ecoceipt.ui.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.ecoceipt.models.ItemModel
//import com.example.ecoceipt.repository.ItemRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class ItemListViewModel (
//    private val repository: ItemRepository = ItemRepository()
//): ViewModel() {
//
//    private val _items = MutableStateFlow<List<ItemModel>>(emptyList())
//    val items: StateFlow<List<ItemModel>> = _items
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage
//
//    init {
//        fetchItems()
//    }
//
//    private fun fetchItems() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                _items.value = repository.getItems()
//                _errorMessage.value = null
//            } catch (e: Exception) {
//                _errorMessage.value = "Failed to  load items: " + e.message
//            } finally {
//                _isLoading.value = false
//
//            }
//        }
//    }
//
//    fun addItem(item: ItemModel) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val success = repository.addItem(item)
//            if (success) {
//                fetchItems()
//            } else {
//                _errorMessage.value = "Failed to add item"
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun deleteItem(itemId: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val success = repository.deleteItem(itemId)
//            if (success) {
//                fetchItems()
//            } else {
//                _errorMessage.value = "Failed to delete item"
//            }
//            _isLoading.value = false
//        }
//    }
//}

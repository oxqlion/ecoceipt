package com.example.ecoceipt.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoceipt.models.ItemModel
import com.example.ecoceipt.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemListViewModel (
    private val repository: ItemRepository = ItemRepository()
): ViewModel() {

    private val _items = MutableStateFlow<List<ItemModel>>(emptyList())
    val items: StateFlow<List<ItemModel>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _items.value = repository.getItems()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to  load items: " + e.message
            } finally {
                _isLoading.value = false

            }
        }
    }

    fun addItem(item: ItemModel) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.addItem(item)
            if (success) {
                fetchItems()
            } else {
                _errorMessage.value = "Failed to add item"
            }
            _isLoading.value = false
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteItem(itemId)
            if (success) {
                fetchItems()
            } else {
                _errorMessage.value = "Failed to delete item"
            }
            _isLoading.value = false
        }
    }
}
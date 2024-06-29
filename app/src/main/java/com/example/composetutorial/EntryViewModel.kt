package com.example.composetutorial

import Collectionn
import Entry
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EntryViewModel: ViewModel() {
    private val _entries = MutableLiveData<List<Entry>>()
    private val _collections = MutableLiveData<List<Collectionn>>()
    val entries: LiveData<List<Entry>> get() = _entries
    val collections: LiveData<List<Collectionn>> get() = _collections

    init {
        getEntries()
    }

    private fun getEntries() {
        viewModelScope.launch {
            val entries = RetrofitClient.instance.getEntries()
            _entries.value = entries
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            try {
                val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTEsIm5hbWUiOiIxNDM3IiwiYWRtaW4iOmZhbHNlLCJpYXQiOjE3MTk2MzA2NTR9.TzDt_5tLIr3_XTN_Bb_YpPaqLePdzZUsffvLQNKp7bw"
                val response = RetrofitClient.instance.getProfile(token)
                _collections.value = response.collections
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }


    fun toggleFavorite(entry: Entry) {
        val updatedEntries = _entries.value?.map {
            if (it.id == entry.id) {
                val updatedEntry = it.copy(isFavorite = !it.isFavorite)
                Log.d(
                    "EntryViewModel",
                    "Entry ${updatedEntry.id} isFavorite: ${updatedEntry.isFavorite}"
                )
                updatedEntry
            } else {
                it
            }
        } ?: emptyList()
        _entries.value = updatedEntries
    }
}


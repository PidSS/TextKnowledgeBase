package com.example.composetutorial

import Entry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EntryViewModel: ViewModel() {
    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> get() = _entries

    init {
        getEntries()
    }

    private fun getEntries() {
        viewModelScope.launch {
            val entries = RetrofitClient.instance.getEntries()
            _entries.value = entries
        }
    }
}


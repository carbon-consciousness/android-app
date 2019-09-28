package com.carbonconciousness.app.ui.forest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the forest Fragment"
    }
    val text: LiveData<String> = _text
}
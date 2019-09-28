package com.carbonconciousness.app.ui.footprint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FootprintViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the footprint Fragment"
    }
    val text: LiveData<String> = _text
}
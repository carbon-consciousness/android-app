package com.carbonconciousness.app.ui.forest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.carbonconciousness.app.R

class ForestFragment : Fragment() {

    private lateinit var forestViewModel: ForestViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        forestViewModel =
                ViewModelProviders.of(this).get(ForestViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_forest, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        forestViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
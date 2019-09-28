package com.carbonconciousness.app.ui.footprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.carbonconciousness.app.R

class FootprintFragment : Fragment() {

    private lateinit var footprintViewModel: FootprintViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        footprintViewModel =
                ViewModelProviders.of(this).get(FootprintViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_footprint, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        footprintViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
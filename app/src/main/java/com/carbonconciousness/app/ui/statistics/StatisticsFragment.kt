package com.carbonconciousness.app.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.carbonconciousness.app.R
import com.carbonconciousness.app.networking.ApiService
import com.carbonconciousness.app.networking.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StatisticsFragment : Fragment() {

    private var disposable: Disposable? = null
    private val apiService by lazy { ApiService.create() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disposable = apiService.stat_data()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> drawStatisticalData(result) },
                { error -> Toast.makeText(context!!, error.message, Toast.LENGTH_SHORT).show() })

    }

    private fun drawStatisticalData(statData: List<Model.Result>) {
        
    }
}
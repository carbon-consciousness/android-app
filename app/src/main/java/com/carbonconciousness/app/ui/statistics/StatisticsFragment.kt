package com.carbonconciousness.app.ui.statistics

import android.graphics.Color
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
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ir.farshid_roohi.linegraph.ChartEntity
import kotlinx.android.synthetic.main.fragment_statistics.*

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
        // Draw the carbon footprint
        var data = ArrayList<Float>()
        var legend = ArrayList<String>()
        var i = 1
        for (elem in statData) {
            data.add(elem.carbon_footprint)
            legend.add(i.toString())
            i++
        }
        val chartEntity = ChartEntity (context?.getColor(R.color.colorPrimary)!!, data.toFloatArray())
        val list = ArrayList<ChartEntity>()
        list.add(chartEntity)
        lineChart.legendArray = legend.toTypedArray()
        lineChart.setList(list)

        // Draw number of steps taken
        var stepData = ArrayList<DataPoint>()
        var j = 1.0
        for (elem in statData) {
            stepData.add(DataPoint(j++, elem.step_counter.toDouble()))
        }
        var series = BarGraphSeries<DataPoint>(stepData.toTypedArray())
        graph_view.addSeries(series)
    }
}
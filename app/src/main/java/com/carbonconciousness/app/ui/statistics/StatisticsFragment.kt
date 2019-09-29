package com.carbonconciousness.app.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.carbonconciousness.app.R
import com.carbonconciousness.app.networking.ApiService
import com.carbonconciousness.app.networking.Model
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
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
        // Draw the carbon footprint and the steps taken
        var i = 0
        var lineEntries = ArrayList<Entry>()
        var barEntries = ArrayList<BarEntry>()
        for (elem in statData) {
            lineEntries.add(Entry(i.toFloat(), elem.carbon_footprint, i.toString()))
            barEntries.add(BarEntry(i.toFloat(), elem.step_counter, i.toString()))
            i++
        }

        val animationDuration = 2000

        // Line Chart
        var lineDataSet = LineDataSet(lineEntries, "Carbon Footprint")
        var lineData = LineData(lineDataSet)
        lineDataSet.colors = listOf(context!!.getColor(R.color.colorPrimary))
        lineDataSet.lineWidth = 5f
        lineDataSet.setDrawCircleHole(true)
        lineDataSet.circleColors = listOf(context!!.getColor(R.color.colorPrimary))
        lineChart.animateX(animationDuration)
        lineChart.data = lineData
        lineChart.setPinchZoom(true)


        // Bar Chart
        var bardataset = BarDataSet(barEntries, "Step Count")
        var bardata = BarData(bardataset)
        bardataset.colors = listOf(context!!.getColor(R.color.colorPrimary))
        barChart.animateXY(animationDuration, animationDuration)
        barChart.data = bardata
        barChart.setPinchZoom(true)
    }

}
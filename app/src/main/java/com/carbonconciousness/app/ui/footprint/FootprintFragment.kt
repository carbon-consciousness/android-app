package com.carbonconciousness.app.ui.footprint

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.carbonconciousness.app.R
import com.carbonconciousness.app.background.PedometerService
import com.carbonconciousness.app.networking.ApiService
import com.carbonconciousness.app.networking.Model
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_footprint.*
import java.util.concurrent.TimeUnit

class FootprintFragment : Fragment(), SensorEventListener {


    // Number of seconds between each poll
    private val POLL_TICK: Long = 30
    private val DEMO_TICK: Long = 5
    // API Services
    private val apiService by lazy { ApiService.create() }
    private var disposables: CompositeDisposable = CompositeDisposable()

    // Step counters
    private var currentStepCount: Float? = 0f
    private var currentStepGoal: Float = 10000f
    private lateinit var sensorManager: SensorManager



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_footprint, container, false)
        // TODO

        return root
    }


    override fun onResume() {
        super.onResume()

        // Start polling server for status
        disposables.add(Observable.interval(0, POLL_TICK, TimeUnit.SECONDS)
            .flatMap { tick -> apiService.status().subscribeOn(Schedulers.io()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> updateStepCounter(currentStepCount, result.step_counter) },
                { error -> showError(error.message) }))

        // Start polling the server for demo data
        disposables.add(Observable.interval(0, DEMO_TICK, TimeUnit.SECONDS)
            .flatMap { tick -> apiService.demo_data().subscribeOn(Schedulers.io()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->  updateFootprint(result) },
                { error -> showError(error.message) })
        )

        // Poll podometer (for UI bizness)
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepsSensor == null) {
            Toast.makeText(context, "No Steps Counter available :(", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }

        // Stop the background pedometer service
        val intent = Intent(context, PedometerService::class.java)
        context?.stopService(intent)
    }

    override fun onPause() {
        super.onPause()
        disposables.dispose()
        sensorManager?.unregisterListener(this)

        // Launch the podometer service to monitor the user's steps even when not in the app
        val intent = Intent(context, PedometerService::class.java)
        context?.startService(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        updateStepCounter(event?.values?.get(0), currentStepGoal)
    }

    private fun updateStepCounter(newStepCount: Float?, newStepGoal: Float ) {
        currentStepCount = newStepCount
        currentStepGoal = newStepGoal
        text_view_step_count.text = getString(R.string.step_count, currentStepCount, currentStepGoal)
        val copy = currentStepCount
        if (copy != null) pedometer_percentage.setCompletedPerentage(copy / currentStepGoal)
    }

    private fun updateFootprint(result: List<Model.Result>) {

    }

    private fun showError(error: String?) = Toast.makeText(context, error, Toast.LENGTH_SHORT).show()

}
package com.carbonconciousness.app

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import com.carbonconciousness.app.background.PedometerService
import com.carbonconciousness.app.networking.ApiService
import com.carbonconciousness.app.networking.Model
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var appBarConfiguration: AppBarConfiguration


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_footprint, R.id.nav_forest, R.id.nav_statistics), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepsSensor == null) {
            Toast.makeText(this, "No Steps Counter available :(", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }

        // Stop the background pedometer service
        val intent = Intent(this, PedometerService::class.java)
        stopService(intent)
    }

    override fun onPause() {
        super.onPause()
        disposables.dispose()
        sensorManager?.unregisterListener(this)

        // Launch the podometer service to monitor the user's steps even when not in the app
        val intent = Intent(this, PedometerService::class.java)
        startService(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        updateStepCounter(event?.values?.get(0), currentStepGoal)
    }

    private fun updateStepCounter(newStepCount: Float?, newStepGoal: Float ) {
        /*
        currentStepCount = newStepCount
        currentStepGoal = newStepGoal
        text_view_step_count.text = getString(R.string.step_count, currentStepCount, currentStepGoal)
        val copy = currentStepCount
        if (copy != null) pedometer_percentage.setCompletedPerentage(copy / currentStepGoal)
        */
    }

    private fun updateFootprint(result: Model.DemoResult) {

    }

    private fun showError(error: String?) = Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

}

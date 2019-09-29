package com.carbonconciousness.app.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.carbonconciousness.app.MainActivity
import com.carbonconciousness.app.R

class PedometerService : Service(), SensorEventListener {

    private val NOTIFICATION_ID = 42

    lateinit var notificationManger : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder

    lateinit var sensorManager: SensorManager

    override fun onCreate() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onBind(intent: Intent?): IBinder? {
        // No binding provided, so return null
        return null
    }

    fun setupNotifications() {
        notificationManger = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel = NotificationChannel(
            getString(R.string.notification_channel),
            getString(R.string.notification_description),
            NotificationManager.IMPORTANCE_LOW)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationManger.createNotificationChannel(notificationChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create notification shtuff
        setupNotifications()
        builder = Notification.Builder(this, getString(R.string.notification_channel))
            .setContentTitle("Step Counter")
            .setContentText("Carbon Forest is tracking your steps")
            .setSmallIcon(R.drawable.ic_stat_icon)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            .setOngoing(true)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(pendingIntent)
        notificationManger.notify(NOTIFICATION_ID, builder.build())

        // Spin up the pedometer listner
        setupPedometerListner()

        // Start counting the steps
        return START_STICKY
    }

    fun setupPedometerListner() {
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepsSensor == null) {
            Toast.makeText(this, "No Steps Counter available :(", Toast.LENGTH_SHORT).show()
            stopSelf()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(this)
        notificationManger.cancel(NOTIFICATION_ID)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.i("PedometerService", "sensor value change" + event?.values?.get(0))
    }

}
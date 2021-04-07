package com.kotlin.latigo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.content.pm.ActivityInfo
import android.hardware.SensorEventListener
import android.hardware.Sensor
import android.widget.Toast
import java.io.IOException


class MainActivity : AppCompatActivity(), SensorEventListener, MediaPlayer.OnCompletionListener {
    private val sensorAccelerometer: Sensor? = null
    var player: MediaPlayer? = null
    private var sm: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playLatigo()
        Toast.makeText(this, R.string.instructions, Toast.LENGTH_LONG).show()

    }

    fun playLatigo() {
        player = MediaPlayer()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        volumeControlStream = AudioManager.STREAM_MUSIC
        val manager = this.assets
        var descriptor: AssetFileDescriptor? = null

        try {
            descriptor = manager.openFd("latigo2.mp3")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            player?.setDataSource(descriptor!!.fileDescriptor, descriptor.startOffset, descriptor.length)
        } catch (e: IOException) {
            e.printStackTrace()
         }

    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[Sensor.TYPE_ACCELEROMETER]
        if (x < -4 && x > -6) {
            if (!player?.isPlaying!!) {
                try {
                    player?.prepare()
                } catch (e: Exception) {
                }

                player?.start()
                player?.setOnCompletionListener(this)
            }

        }
    }

    override fun onResume() {

        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensors = sm?.getSensorList(Sensor.TYPE_ACCELEROMETER)
        if (sensors?.size!! > 0) {
            sm?.registerListener(this, sensors[0], SensorManager.SENSOR_DELAY_FASTEST)
        }
        super.onResume()
    }

    override fun onPause() {
        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        sm?.unregisterListener(this, sensorAccelerometer)
        super.onPause()
    }

    override fun onStop() {
        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        sm?.unregisterListener(this, sensorAccelerometer)
        super.onStop()
        System.exit(0)
        finish()

    }

    override fun onCompletion(mp: MediaPlayer) {
        mp.stop()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //To change body of created functions use File | Settings | File Templates.
    }
}

package com.example.chessmac.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {
    private var lastUpdate: Long = 0  //Time of last sensor event
    private var last_x = 0f           //Last known accelerometer readings
    private var last_y = 0f
    private var last_z = 0f
    private val SHAKE_THRESHOLD = 800

    //Called when a sensor event occurs
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val current_time = System.currentTimeMillis()
            val time_difference = current_time - lastUpdate

            //If 0,1 sec have passed, calculate shake intensity based on new accelerometer readings
            if (time_difference > 100) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val shake = Math.abs(x + y + z - last_x - last_y - last_z) / time_difference * 10000

                //If it can be considered a shake, call the onShake method
                //onShake is a function passed as parameter in ChessGameScreen
                if (shake > SHAKE_THRESHOLD) {
                    onShake()
                }

                last_x = x
                last_y = y
                last_z = z
                lastUpdate = current_time
            }
        }
    }

    //Has to be here because of the SensorEventListener interface.
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}
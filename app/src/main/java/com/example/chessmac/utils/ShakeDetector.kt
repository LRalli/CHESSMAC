package com.example.chessmac.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {
    private var lastUpdate: Long = 0
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f
    private val SHAKE_THRESHOLD = 800

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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}
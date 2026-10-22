package com.example.smartstudy

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class EnvironmentActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var lightSensor: Sensor? = null
    private var accelerometer: Sensor? = null

    private lateinit var tvLight: TextView
    private lateinit var tvMovement: TextView
    private lateinit var tvEnvironmentStatus: TextView

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private var firstReading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_environment)

        // Connect views
        tvLight = findViewById(R.id.tvLight)
        tvMovement = findViewById(R.id.tvMovement)
        tvEnvironmentStatus =
            findViewById(R.id.tvEnvironmentStatus)

        // Back button
        val btnBack =
            findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Sensor Manager
        sensorManager =
            getSystemService(SENSOR_SERVICE) as SensorManager

        // Light sensor
        lightSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT
            )

        // Accelerometer
        accelerometer =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            )

        // Light sensor check
        if (lightSensor == null) {

            tvLight.text = "Unavailable"

            tvEnvironmentStatus.text =
                "⚠️ Light sensor is unavailable"
        }

        // Accelerometer check
        if (accelerometer == null) {

            tvMovement.text =
                "Unavailable"
        }
    }

    override fun onResume() {
        super.onResume()

        // Start light sensor
        lightSensor?.let {

            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Start accelerometer
        accelerometer?.let {

            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()

        // Stop sensors
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        if (event == null) return

        when (event.sensor.type) {

            // =========================
            // LIGHT SENSOR
            // =========================

            Sensor.TYPE_LIGHT -> {

                val lux =
                    event.values[0]

                tvLight.text =
                    String.format(
                        "%.1f lux",
                        lux
                    )

                updateEnvironmentStatus(lux)
            }

            // =========================
            // ACCELEROMETER
            // =========================

            Sensor.TYPE_ACCELEROMETER -> {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                if (firstReading) {

                    lastX = x
                    lastY = y
                    lastZ = z

                    firstReading = false

                    tvMovement.text =
                        "✓ Device Stable"

                    tvMovement.setTextColor(
                        Color.rgb(
                            76,
                            175,
                            80
                        )
                    )

                    return
                }

                val differenceX =
                    abs(x - lastX)

                val differenceY =
                    abs(y - lastY)

                val differenceZ =
                    abs(z - lastZ)

                val movement =
                    differenceX +
                            differenceY +
                            differenceZ

                // Movement threshold
                if (movement > 2.0f) {

                    tvMovement.text =
                        "📱 Device Moving"

                    tvMovement.setTextColor(
                        Color.rgb(
                            220,
                            80,
                            80
                        )
                    )

                } else {

                    tvMovement.text =
                        "✓ Device Stable"

                    tvMovement.setTextColor(
                        Color.rgb(
                            76,
                            175,
                            80
                        )
                    )
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    // =========================
    // ENVIRONMENT STATUS
    // =========================

    private fun updateEnvironmentStatus(
        lux: Float
    ) {

        when {

            lux < 50 -> {

                tvEnvironmentStatus.text =
                    "🌙 Room is too dark\n\n" +
                            "Try turning on a lamp or increasing the room lighting."

                tvEnvironmentStatus.setTextColor(
                    Color.rgb(
                        120,
                        80,
                        180
                    )
                )
            }

            lux < 300 -> {

                tvEnvironmentStatus.text =
                    "💡 Good lighting for studying\n\n" +
                            "Your environment is suitable for focused study."

                tvEnvironmentStatus.setTextColor(
                    Color.rgb(
                        60,
                        150,
                        80
                    )
                )
            }

            lux < 1000 -> {

                tvEnvironmentStatus.text =
                    "☀️ Room is bright\n\n" +
                            "Your study area has plenty of light."

                tvEnvironmentStatus.setTextColor(
                    Color.rgb(
                        210,
                        140,
                        30
                    )
                )
            }

            else -> {

                tvEnvironmentStatus.text =
                    "⚠️ Room may be too bright\n\n" +
                            "Consider reducing the brightness to avoid eye strain."

                tvEnvironmentStatus.setTextColor(
                    Color.rgb(
                        220,
                        90,
                        60
                    )
                )
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // Not required
    }
}
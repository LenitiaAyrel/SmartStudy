package com.example.smartstudy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Start Studying button
        val btnStartStudy = findViewById<Button>(R.id.btnStartStudy)

        btnStartStudy.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }

        // Study History button
        val btnHistory = findViewById<Button>(R.id.btnHistory)

        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Study Environment button
        val btnEnvironment = findViewById<Button>(R.id.btnEnvironment)

        btnEnvironment.setOnClickListener {
            val intent = Intent(this, EnvironmentActivity::class.java)
            startActivity(intent)
        }

        // Study Statistics button
        val btnStatistics = findViewById<Button>(R.id.btnStatistics)

        btnStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}
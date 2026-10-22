package com.example.smartstudy

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class StatisticsActivity : AppCompatActivity() {

    private lateinit var tvTotalSessions: TextView
    private lateinit var tvTotalMinutes: TextView
    private lateinit var tvAverageSession: TextView
    private lateinit var tvMotivation: TextView
    private lateinit var tvSubjectStats: TextView
    private lateinit var btnBack: MaterialButton

    // Custom View
    private lateinit var studyProgressView: StudyProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_statistics)

        // --------------------------------
        // CONNECT VIEWS
        // --------------------------------

        tvTotalSessions =
            findViewById(R.id.tvTotalSessions)

        tvTotalMinutes =
            findViewById(R.id.tvTotalMinutes)

        tvAverageSession =
            findViewById(R.id.tvAverageSession)

        tvMotivation =
            findViewById(R.id.tvMotivation)

        tvSubjectStats =
            findViewById(R.id.tvSubjectStatistics)

        // Back button
        btnBack =
            findViewById(R.id.btnBack)

        // Custom View
        studyProgressView =
            findViewById(R.id.studyProgressView)


        // --------------------------------
        // BACK BUTTON
        // --------------------------------

        btnBack.setOnClickListener {
            finish()
        }


        // --------------------------------
        // LOAD STATISTICS
        // --------------------------------

        loadStatistics()
    }


    private fun loadStatistics() {

        val database =
            AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {

            val sessions =
                database.studySessionDao()
                    .getAllSessions()


            // --------------------------------
            // OVERALL STATISTICS
            // --------------------------------

            val totalSessions =
                sessions.size

            val totalMinutes =
                sessions.sumOf {
                    it.durationMinutes
                }

            val averageMinutes =
                if (totalSessions > 0) {
                    totalMinutes / totalSessions
                } else {
                    0
                }


            // --------------------------------
            // DISPLAY OVERALL STATISTICS
            // --------------------------------

            tvTotalSessions.text =
                totalSessions.toString()

            tvTotalMinutes.text =
                "$totalMinutes min"

            tvAverageSession.text =
                "$averageMinutes min"


            // --------------------------------
            // SUBJECT-WISE STATISTICS
            // --------------------------------

            if (sessions.isEmpty()) {

                tvSubjectStats.text =
                    "No study sessions yet."

                // Empty chart
                studyProgressView.setData(
                    emptyList(),
                    emptyList()
                )

            } else {

                // Group sessions by subject
                val subjectGroups =
                    sessions.groupBy {
                        it.subject
                    }


                // Sort subjects alphabetically
                val sortedSubjects =
                    subjectGroups.keys.sorted()


                // --------------------------------
                // PREPARE CHART DATA
                // --------------------------------

                val chartSubjects =
                    mutableListOf<String>()

                val chartMinutes =
                    mutableListOf<Int>()


                // --------------------------------
                // SUBJECT TEXT
                // --------------------------------

                val subjectText =
                    StringBuilder()


                for (subject in sortedSubjects) {

                    val subjectSessions =
                        subjectGroups[subject]
                            ?: emptyList()


                    val sessionCount =
                        subjectSessions.size


                    val subjectMinutes =
                        subjectSessions.sumOf {
                            it.durationMinutes
                        }


                    // Add data to CustomView
                    chartSubjects.add(subject)

                    chartMinutes.add(subjectMinutes)


                    // Add data to subject statistics
                    subjectText.append("📖 ")
                    subjectText.append(subject)
                    subjectText.append("\n")

                    subjectText.append(sessionCount)
                    subjectText.append(" session(s) • ")
                    subjectText.append(subjectMinutes)
                    subjectText.append(" min")

                    subjectText.append("\n\n")
                }


                // --------------------------------
                // DISPLAY SUBJECT STATISTICS
                // --------------------------------

                tvSubjectStats.text =
                    subjectText
                        .toString()
                        .trim()


                // --------------------------------
                // SEND DATA TO CUSTOM VIEW
                // --------------------------------

                studyProgressView.setData(
                    chartSubjects,
                    chartMinutes
                )
            }


            // --------------------------------
            // MOTIVATION MESSAGE
            // --------------------------------

            tvMotivation.text =
                when {

                    totalSessions == 0 ->
                        "Start your first study session! 🚀"

                    totalSessions < 5 ->
                        "Great start! Keep building your study habit! 🌱"

                    totalSessions < 10 ->
                        "Amazing dedication! Keep it up! 🔥"

                    else ->
                        "Excellent consistency! You're doing great! 🏆"
                }
        }
    }
}
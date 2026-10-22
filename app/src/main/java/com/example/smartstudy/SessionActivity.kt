package com.example.smartstudy

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvSubject: TextView
    private lateinit var tvCompletionMessage: TextView

    private lateinit var btnPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnStudyAgain: Button
    private lateinit var btnHome: Button

    private var timer: CountDownTimer? = null

    private var isPaused = false
    private var isStopped = false
    private var sessionSaved = false

    private var durationMinutes = 25

    private var remainingTime: Long =
        25 * 60 * 1000L

    private lateinit var subject: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session)


        // --------------------------------
        // CONNECT VIEWS
        // --------------------------------

        tvTimer =
            findViewById(R.id.tvTimer)

        tvStatus =
            findViewById(R.id.tvStatus)

        tvSubject =
            findViewById(R.id.tvSubject)

        tvCompletionMessage =
            findViewById(R.id.tvCompletionMessage)

        btnPause =
            findViewById(R.id.btnPause)

        btnStop =
            findViewById(R.id.btnStop)

        btnStudyAgain =
            findViewById(R.id.btnStudyAgain)

        btnHome =
            findViewById(R.id.btnHome)


        // --------------------------------
        // RECEIVE DATA FROM STUDY ACTIVITY
        // --------------------------------

        subject =
            intent.getStringExtra("SUBJECT")
                ?: "Study Session"

        durationMinutes =
            intent.getIntExtra(
                "DURATION",
                25
            )


        // --------------------------------
        // DISPLAY SUBJECT
        // --------------------------------

        tvSubject.text = subject


        // --------------------------------
        // SET INITIAL TIMER
        // --------------------------------

        remainingTime =
            durationMinutes * 60 * 1000L

        updateTimerDisplay()


        // --------------------------------
        // START TIMER
        // --------------------------------

        startTimer()


        // --------------------------------
        // PAUSE / RESUME BUTTON
        // --------------------------------

        btnPause.setOnClickListener {

            if (isStopped) {
                return@setOnClickListener
            }

            if (isPaused) {

                // Resume session

                isPaused = false

                btnPause.text =
                    "Pause"

                tvStatus.text =
                    "Session Running"

                startTimer()

            } else {

                // Pause session

                isPaused = true

                timer?.cancel()

                btnPause.text =
                    "Resume"

                tvStatus.text =
                    "Session Paused"
            }
        }


        // --------------------------------
        // STOP SESSION BUTTON
        // --------------------------------

        btnStop.setOnClickListener {

            stopSession()
        }


        // --------------------------------
        // STUDY AGAIN BUTTON
        // --------------------------------

        btnStudyAgain.setOnClickListener {

            returnResult(
                action = "STUDY_AGAIN",
                status = if (isStopped) {
                    "Session Finished"
                } else {
                    "Session Stopped"
                }
            )
        }


        // --------------------------------
        // BACK TO HOME BUTTON
        // --------------------------------

        btnHome.setOnClickListener {

            returnResult(
                action = "HOME",
                status = if (isStopped) {
                    "Session Finished"
                } else {
                    "Session Stopped"
                }
            )
        }
    }


    // ====================================
    // START TIMER
    // ====================================

    private fun startTimer() {

        timer?.cancel()

        timer =
            object : CountDownTimer(
                remainingTime,
                1000
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    remainingTime =
                        millisUntilFinished

                    updateTimerDisplay()
                }


                override fun onFinish() {

                    remainingTime = 0

                    updateTimerDisplay()

                    completeSession()
                }

            }.start()
    }


    // ====================================
    // UPDATE TIMER DISPLAY
    // ====================================

    private fun updateTimerDisplay() {

        val totalSeconds =
            remainingTime / 1000

        val minutes =
            totalSeconds / 60

        val seconds =
            totalSeconds % 60

        tvTimer.text =
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
            )
    }


    // ====================================
    // STOP SESSION EARLY
    // ====================================

    private fun stopSession() {

        if (isStopped) {
            return
        }

        timer?.cancel()

        isStopped = true
        isPaused = false

        // Save session
        saveSession()


        // --------------------------------
        // STOPPED MESSAGE
        // --------------------------------

        tvStatus.text =
            "⏹ Session Stopped"

        tvCompletionMessage.text =
            "You stopped this study session.\n\n" +
                    "Good effort! You can start another " +
                    "session whenever you're ready. 💪"

        tvCompletionMessage.visibility =
            TextView.VISIBLE


        // Hide pause and stop buttons

        btnPause.visibility =
            Button.GONE

        btnStop.visibility =
            Button.GONE


        // Show navigation buttons

        btnStudyAgain.visibility =
            Button.VISIBLE

        btnHome.visibility =
            Button.VISIBLE
    }


    // ====================================
    // SESSION COMPLETED NORMALLY
    // ====================================

    private fun completeSession() {

        if (isStopped) {
            return
        }

        isStopped = true
        isPaused = false

        // Make sure timer shows 00:00
        remainingTime = 0

        updateTimerDisplay()


        // Save session
        saveSession()


        // --------------------------------
        // COMPLETION MESSAGE
        // --------------------------------

        tvStatus.text =
            "🎉 Session Complete!"

        tvCompletionMessage.text =
            "Great work! Keep studying! ✨\n\n" +
                    "You completed your focused study session! 💪"

        tvCompletionMessage.visibility =
            TextView.VISIBLE


        // Hide pause and stop buttons

        btnPause.visibility =
            Button.GONE

        btnStop.visibility =
            Button.GONE


        // Show navigation buttons

        btnStudyAgain.visibility =
            Button.VISIBLE

        btnHome.visibility =
            Button.VISIBLE
    }


    // ====================================
    // SEND DATA BACK TO STUDY ACTIVITY
    // ====================================

    private fun returnResult(
        action: String,
        status: String
    ) {

        timer?.cancel()

        val resultIntent =
            Intent().apply {

                putExtra(
                    "SESSION_ACTION",
                    action
                )

                putExtra(
                    "SESSION_STATUS",
                    status
                )

                putExtra(
                    "SESSION_SUBJECT",
                    subject
                )

                putExtra(
                    "SESSION_DURATION",
                    durationMinutes
                )
            }


        // Send result back to the
        // Activity that launched this Activity

        setResult(
            RESULT_OK,
            resultIntent
        )


        // IMPORTANT:
        // Finish SessionActivity so that
        // StudyActivity receives the result.

        finish()
    }


    // ====================================
    // SAVE SESSION TO ROOM
    // ====================================

    private fun saveSession() {

        // Prevent duplicate saving
        if (sessionSaved) {
            return
        }

        sessionSaved = true


        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale.getDefault()
            )

        val currentDate =
            dateFormat.format(Date())


        val session =
            StudySession(
                subject = subject,
                durationMinutes = durationMinutes,
                date = currentDate
            )


        val database =
            AppDatabase.getDatabase(
                applicationContext
            )


        lifecycleScope.launch {

            database.studySessionDao()
                .insertSession(session)
        }
    }


    // ====================================
    // CLEAN UP TIMER
    // ====================================

    override fun onDestroy() {

        super.onDestroy()

        timer?.cancel()
    }
}
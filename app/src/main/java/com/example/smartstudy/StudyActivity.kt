package com.example.smartstudy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class StudyActivity : AppCompatActivity() {

    // Activity Result Launcher
    private lateinit var sessionResultLauncher:
            ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_study)


        // --------------------------------
        // REGISTER ACTIVITY RESULT LAUNCHER
        // --------------------------------

        sessionResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->

                if (result.resultCode == Activity.RESULT_OK) {

                    val data = result.data

                    val action =
                        data?.getStringExtra(
                            "SESSION_ACTION"
                        ) ?: ""

                    val status =
                        data?.getStringExtra(
                            "SESSION_STATUS"
                        ) ?: "Session Finished"

                    val subject =
                        data?.getStringExtra(
                            "SESSION_SUBJECT"
                        ) ?: ""

                    val duration =
                        data?.getIntExtra(
                            "SESSION_DURATION",
                            25
                        ) ?: 25


                    // --------------------------------
                    // HANDLE SESSION RESULT
                    // --------------------------------

                    when (action) {

                        "STUDY_AGAIN" -> {

                            // Start another session
                            // using the same subject
                            // and duration

                            val intent =
                                Intent(
                                    this,
                                    SessionActivity::class.java
                                )

                            intent.putExtra(
                                "SUBJECT",
                                subject
                            )

                            intent.putExtra(
                                "DURATION",
                                duration
                            )

                            sessionResultLauncher.launch(
                                intent
                            )
                        }


                        "HOME" -> {

                            Toast.makeText(
                                this,
                                "$status: $subject",
                                Toast.LENGTH_LONG
                            ).show()


                            val intent =
                                Intent(
                                    this,
                                    MainActivity::class.java
                                )

                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP

                            startActivity(intent)

                            finish()
                        }


                        else -> {

                            Toast.makeText(
                                this,
                                "$status: $subject",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }


        // --------------------------------
        // CONNECT VIEWS
        // --------------------------------

        val subjectInput =
            findViewById<EditText>(
                R.id.etSubject
            )

        val durationSpinner =
            findViewById<Spinner>(
                R.id.spinnerDuration
            )

        val startButton =
            findViewById<Button>(
                R.id.btnStartSession
            )


        // --------------------------------
        // DURATION OPTIONS
        // --------------------------------

        val durations = arrayOf(
            "15 minutes",
            "25 minutes",
            "30 minutes",
            "45 minutes",
            "60 minutes"
        )


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                durations
            )


        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )


        durationSpinner.adapter =
            adapter


        // --------------------------------
        // START STUDY SESSION
        // --------------------------------

        startButton.setOnClickListener {

            val subject =
                subjectInput.text
                    .toString()
                    .trim()


            if (subject.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a subject",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val selectedDuration =
                durationSpinner
                    .selectedItem
                    .toString()


            val durationMinutes =
                selectedDuration
                    .split(" ")[0]
                    .toInt()


            // --------------------------------
            // SEND DATA TO SESSION ACTIVITY
            // --------------------------------

            val intent =
                Intent(
                    this,
                    SessionActivity::class.java
                )


            intent.putExtra(
                "SUBJECT",
                subject
            )


            intent.putExtra(
                "DURATION",
                durationMinutes
            )


            // Start SessionActivity
            // and wait for the result

            sessionResultLauncher.launch(
                intent
            )
        }
    }
}
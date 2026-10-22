package com.example.smartstudy

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        // Connect views
        recyclerHistory =
            findViewById(R.id.recyclerHistory)

        tvEmptyHistory =
            findViewById(R.id.tvEmptyHistory)

        // Back button
        val btnBack =
            findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Clear All History button
        val btnClearHistory =
            findViewById<Button>(R.id.btnClearHistory)

        btnClearHistory.setOnClickListener {
            showClearAllConfirmation()
        }

        // RecyclerView
        adapter = HistoryAdapter(emptyList()) { session ->
            showDeleteConfirmation(session)
        }

        recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        recyclerHistory.adapter = adapter

        // Load sessions
        loadSessions()
    }

    private fun loadSessions() {

        val database =
            AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {

            val sessions =
                database.studySessionDao()
                    .getAllSessions()

            updateHistoryUI(sessions)
        }
    }

    private fun updateHistoryUI(
        sessions: List<StudySession>
    ) {

        if (sessions.isEmpty()) {

            tvEmptyHistory.visibility =
                TextView.VISIBLE

            recyclerHistory.visibility =
                RecyclerView.GONE

        } else {

            tvEmptyHistory.visibility =
                TextView.GONE

            recyclerHistory.visibility =
                RecyclerView.VISIBLE

            adapter.updateSessions(sessions)
        }
    }

    private fun showDeleteConfirmation(
        session: StudySession
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Study Session?")
            .setMessage(
                "Delete the ${session.durationMinutes}-minute " +
                        "session for \"${session.subject}\"?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->

                deleteSession(session)
            }
            .show()
    }

    private fun deleteSession(
        session: StudySession
    ) {

        val database =
            AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {

            database.studySessionDao()
                .deleteSession(session)

            loadSessions()
        }
    }

    private fun showClearAllConfirmation() {

        AlertDialog.Builder(this)
            .setTitle("Clear All History?")
            .setMessage(
                "This will permanently delete all " +
                        "study sessions from your history."
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear All") { _, _ ->

                clearAllHistory()
            }
            .show()
    }

    private fun clearAllHistory() {

        val database =
            AppDatabase.getDatabase(applicationContext)

        lifecycleScope.launch {

            database.studySessionDao()
                .deleteAllSessions()

            loadSessions()
        }
    }
}
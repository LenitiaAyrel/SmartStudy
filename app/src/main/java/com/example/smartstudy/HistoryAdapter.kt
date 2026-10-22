package com.example.smartstudy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private var sessions: List<StudySession>,
    private val onDeleteClick: (StudySession) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val subject: TextView =
            itemView.findViewById(R.id.tvSubject)

        val duration: TextView =
            itemView.findViewById(R.id.tvDuration)

        val date: TextView =
            itemView.findViewById(R.id.tvDate)

        val deleteButton: ImageButton =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_study_session,
                parent,
                false
            )

        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SessionViewHolder,
        position: Int
    ) {

        val session = sessions[position]

        holder.subject.text = session.subject

        holder.duration.text =
            "${session.durationMinutes} minutes"

        holder.date.text = session.date

        holder.deleteButton.setOnClickListener {
            onDeleteClick(session)
        }
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    fun updateSessions(newSessions: List<StudySession>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}
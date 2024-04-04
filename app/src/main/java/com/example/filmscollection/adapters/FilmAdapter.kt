package com.example.filmscollection.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.filmscollection.R
import com.example.filmscollection.models.Film

class FilmAdapter(
    private val films: List<Film>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FilmAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(film: Film)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = films[position]
        holder.titleTextView.text = film.title
        holder.yearTextView.text = film.director + " - " + film.year.toString()
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(film)
        }
    }

    override fun getItemCount(): Int {
        return films.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val yearTextView: TextView = itemView.findViewById(R.id.yearTextView)
    }
}
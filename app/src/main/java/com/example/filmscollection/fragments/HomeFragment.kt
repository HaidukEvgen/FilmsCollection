package com.example.filmscollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmscollection.R
import com.example.filmscollection.adapters.FilmAdapter
import com.example.filmscollection.models.Film
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(), FilmAdapter.OnItemClickListener {

    private lateinit var adapter: FilmAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var filmList: MutableList<Film>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.home_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        filmList = mutableListOf()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val database = FirebaseFirestore.getInstance()
        val filmsCollection = database.collection("films")

        filmsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val filmList = mutableListOf<Film>()
                for (document in querySnapshot.documents) {
                    val film = document.toObject(Film::class.java)
                    film?.let {
                        filmList.add(it)
                    }
                }

                adapter = FilmAdapter(filmList, this@HomeFragment)
                recyclerView.adapter = adapter
            }
        return rootView
    }

    override fun onItemClick(film: Film) {
        val fragment = FilmInfoFragment.newInstance(film, false)
        fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }
}
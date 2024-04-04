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
import com.example.filmscollection.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment(), FilmAdapter.OnItemClickListener {

    private lateinit var filmList: MutableList<Film>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FilmAdapter
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.favorites_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        filmList = mutableListOf()

        getList()

        return rootView
    }

    private fun getList() {
        getUser { user ->
            if (user != null) {
                for (movieIdToFind in user.favorites) {
                    val moviesCollection = FirebaseFirestore.getInstance().collection("films")
                    val query = moviesCollection.whereEqualTo("id", movieIdToFind.toDouble())
                    query.get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                val film = document.toObject(Film::class.java)
                                film?.let {
                                    if (!filmList.contains(it)) {
                                        filmList.add(it)
                                    }
                                }
                            }
                            adapter = FilmAdapter(filmList, this@FavoritesFragment)
                            recyclerView.adapter = adapter
                        }
                }
            }
        }
    }

    private fun getUser(completion: (User?) -> Unit) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            val query = usersCollection.whereEqualTo("email", currentUserEmail)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userDocument = querySnapshot.documents.first()
                        val user = userDocument.toObject(User::class.java)
                        completion(user)
                    }
                }
                .addOnFailureListener { e ->
                    completion(null)
                }
        } else {
            completion(null)
        }
    }

    override fun onItemClick(film: Film) {
        val fragment = FilmInfoFragment.newInstance(film, true)
        fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {

            }
    }
}
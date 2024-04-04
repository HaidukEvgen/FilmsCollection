package com.example.filmscollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.filmscollection.R
import com.example.filmscollection.adapters.SliderAdapter
import com.example.filmscollection.models.Film
import com.example.filmscollection.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FilmInfoFragment : DialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var userSnapshot: DocumentSnapshot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.film_info_fragment, container, false)
        displayInformation(rootView)
        var btn = rootView.findViewById<Button>(R.id.favouritesButton)
        getUser { user ->
            if (user != null) {
                val id = arguments?.getInt("id")!!
                val isInFavorites = user.favorites.contains(id)
                if (isInFavorites) {
                    btn.text = "Added to favorites"
                } else {
                    btn.text = "To favorites"
                }
                btn.setOnClickListener {
                    if (isInFavorites) {
                        user.favorites.remove(id)
                        btn.text = "To favorites"
                    } else {
                        user.favorites.add(id)
                        btn.text = "Added to favorites"
                    }
                    updateUserFavorites(user)
                }
            }
        }
        return rootView
    }

    private fun getUser(completion: (User?) -> Unit) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            usersCollection.document(currentUserEmail)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    completion(user)
                    userSnapshot = documentSnapshot
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Failed to retrieve user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    completion(null)
                }
        }
    }

    private fun updateUserFavorites(user: User) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            usersCollection.document(currentUserEmail)
                .set(user)
        }
    }

    private fun displayInformation(rootView: View) {
        viewPager = rootView.findViewById<ViewPager2>(R.id.viewPager)
        sliderAdapter = SliderAdapter()
        viewPager.adapter = sliderAdapter

        val title = rootView.findViewById<TextView>(R.id.titleTextView)
        val director = rootView.findViewById<TextView>(R.id.directorTextView)
        val description = rootView.findViewById<TextView>(R.id.descriptionTextView)
        val year = rootView.findViewById<TextView>(R.id.yearTextView)

        title.text = arguments?.getString("title")
        director.text = arguments?.getString("director")
        description.text = arguments?.getString("description")
        year.text = arguments?.getInt("year").toString()

        val id = arguments?.getInt("id")
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/$id")
        val list = storageReference.listAll()
        list.addOnSuccessListener { result ->
            result.items.forEach { imageReference ->
                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    sliderAdapter.addImage(imageUrl.toString())
                }
            }
        }

        val isFavorite = arguments?.getBoolean("isFavorite")!!
        val btn = rootView.findViewById<Button>(R.id.favouritesButton)
        if (isFavorite) {
            btn.text = "Added to favorites"
        } else {
            btn.text = "Add to favorites"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(data: Film, isFavorite: Boolean): FilmInfoFragment {
            val fragment = FilmInfoFragment()
            val args = Bundle()
            args.putInt("id", data.id)
            args.putString("title", data.title)
            args.putString("description", data.description)
            args.putString("director", data.director)
            args.putInt("year", data.year)
            args.putBoolean("isFavorite", isFavorite)
            fragment.arguments = args
            return fragment
        }
    }
}
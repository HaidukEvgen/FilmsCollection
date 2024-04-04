package com.example.filmscollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.filmscollection.R
import com.example.filmscollection.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AccountFragment : Fragment() {

    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var nickname: EditText
    private lateinit var phone: EditText
    private lateinit var address: EditText
    private lateinit var city: EditText
    private lateinit var country: EditText
    private lateinit var age: EditText
    private lateinit var gender: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.account_fragment, container, false)

        name = rootView.findViewById(R.id.nameEdit)
        surname = rootView.findViewById(R.id.surnameEdit)
        nickname = rootView.findViewById(R.id.nicknameEdit)
        phone = rootView.findViewById(R.id.phoneEdit)
        address = rootView.findViewById(R.id.addressEdit)
        city = rootView.findViewById(R.id.cityEdit)
        country = rootView.findViewById(R.id.countryEdit)
        age = rootView.findViewById(R.id.ageEdit)
        gender = rootView.findViewById(R.id.genderEdit)

        val btn = rootView.findViewById<Button>(R.id.saveButton)

        btn.setOnClickListener {
            updateUserData()
        }

        getUserData()

        return rootView
    }

    private fun getUserData() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            usersCollection.whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userDocument = querySnapshot.documents.first()
                        val user = userDocument.toObject(User::class.java)
                        user?.let {
                            name.setText(it.name)
                            surname.setText(it.surname)
                            nickname.setText(it.nickname)
                            phone.setText(it.phoneNumber)
                            address.setText(it.address)
                            city.setText(it.city)
                            country.setText(it.country)
                            age.setText(it.age.toString())
                            gender.setText(it.gender)
                        }
                    }
                }
        }
    }

    private fun updateUserData() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            val userData = hashMapOf(
                "name" to name.text.toString(),
                "surname" to surname.text.toString(),
                "nickname" to nickname.text.toString(),
                "phoneNumber" to phone.text.toString(),
                "address" to address.text.toString(),
                "city" to city.text.toString(),
                "country" to country.text.toString(),
                "age" to age.text.toString().toInt(),
                "gender" to gender.text.toString()
            )
            usersCollection.document(currentUserEmail)
                .set(userData, SetOptions.merge())
        }
    }
}
package com.example.final_project.component

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class AuthSimpleViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val db = FirebaseFirestore.getInstance()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _success = MutableLiveData<Boolean?>()
    val success: LiveData<Boolean?> get() = _success

    fun rememberedUsername(): String =
        prefs.getString("username", "") ?: ""

    fun currentUsername(): String? =
        prefs.getString("username", null)

    fun login(username: String, password: String, remember: Boolean) {
        if (username.isBlank() || password.isBlank()) {
            _error.value = "Please fill all fields"
            return
        }

        db.collection("users").document(username).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val savedPass = doc.getString("password")
                    if (savedPass == password) {
                        if (remember) {
                            prefs.edit().putString("username", username).apply()
                        }
                        _success.value = true
                    } else {
                        _error.value = "Wrong password"
                    }
                } else {
                    _error.value = "User not found"
                }
            }
            .addOnFailureListener { e ->
                _error.value = "Error: ${e.message}"
            }
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _error.value = "Please fill all fields"
            return
        }

        db.collection("users").document(username).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _error.value = "Username already taken"
                } else {
                    val data = mapOf("password" to password)
                    db.collection("users").document(username).set(data)
                        .addOnSuccessListener {
                            prefs.edit().putString("username", username).apply()
                            _success.value = true
                        }
                        .addOnFailureListener { e ->
                            _error.value = "Error: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { e ->
                _error.value = "Error: ${e.message}"
            }
    }
}

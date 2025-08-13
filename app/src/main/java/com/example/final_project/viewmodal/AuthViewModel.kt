package com.example.final_project.viewmodal

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val db = FirebaseFirestore.getInstance()

    // ✅ Also keep SessionManager in sync with the same truth
    private val session = com.example.final_project.auth.SessionManager(app)

    companion object {
        private const val KEY_CURRENT = "current_username"
        private const val KEY_REMEMBERED = "remembered_username"
    }

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _success = MutableLiveData<Boolean?>()
    val success: LiveData<Boolean?> get() = _success

    fun clearAuthFlags() {
        _success.value = null
        _error.value = null
    }

    fun rememberedUsername(): String? = prefs.getString(KEY_REMEMBERED, null)
    fun currentUsername(): String? = prefs.getString(KEY_CURRENT, null)

    fun tryAutoLogin() {
        val remembered = rememberedUsername()
        if (!remembered.isNullOrBlank()) {
            prefs.edit().putString(KEY_CURRENT, remembered).apply()
            session.username = remembered              // ✅ keep SessionManager in sync
            _success.value = true
        }
    }

    fun login(username: String, password: String, remember: Boolean) {
        val uname = username.trim()
        if (uname.isBlank() || password.isBlank()) {
            _error.value = "Please fill all fields"
            return
        }

        db.collection("users").document(uname).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    _error.value = "User not found"
                    return@addOnSuccessListener
                }
                val savedPass = doc.getString("password")
                if (savedPass == password) {
                    prefs.edit().putString(KEY_CURRENT, uname).apply()
                    session.username = uname            // ✅ keep SessionManager in sync

                    if (remember) {
                        prefs.edit().putString(KEY_REMEMBERED, uname).apply()
                    } else {
                        prefs.edit().remove(KEY_REMEMBERED).apply()
                    }

                    _success.value = true
                } else {
                    _error.value = "Wrong password"
                }
            }
            .addOnFailureListener { e ->
                _error.value = "Error: ${e.message}"
            }
    }

    fun register(username: String, password: String) {
        val uname = username.trim()
        if (uname.isBlank() || password.isBlank()) {
            _error.value = "Please fill all fields"
            return
        }

        db.collection("users").document(uname).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _error.value = "Username already taken"
                } else {
                    db.collection("users").document(uname)
                        .set(mapOf("password" to password))
                        .addOnSuccessListener {
                            prefs.edit().putString(KEY_CURRENT, uname).apply()
                            session.username = uname        // ✅ keep SessionManager in sync
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

    fun logout(clearRemembered: Boolean = true) {
        prefs.edit().apply {
            remove(KEY_CURRENT)
            if (clearRemembered) remove(KEY_REMEMBERED)
        }.apply()
        session.clear()                 // ✅ clear SessionManager as well
        clearAuthFlags()               // ✅ avoid stale success=true
    }

    fun isLoggedIn(): Boolean = !currentUsername().isNullOrBlank()
}

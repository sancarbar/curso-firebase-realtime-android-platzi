package com.platzi.realtimetrader.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.model.User
import com.platzi.realtimetrader.network.Callback
import com.platzi.realtimetrader.network.FirebaseService
import kotlinx.android.synthetic.main.activity_login.*

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val TAG = "LoginActivity"

    lateinit var firebaseService: FirebaseService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseService = FirebaseService(FirebaseFirestore.getInstance())
        auth.signOut()
    }


    fun onStartClicked(view: View) {
        view.isEnabled = false
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login exitóso, actualizar la vista con la información del usuario
                    Log.d(TAG, "signInAnonymously:success")
                    val username = username.text.toString()

                    val user = User()
                    user.username = username
                    saveUserAndStartMainActivity(user, username, view)

                } else {
                    showErrorMessage(button)
                    view.isEnabled = true
                }
            }
    }

    private fun saveUserAndStartMainActivity(userDocument: User, username: String, view: View) {
        firebaseService.setDocument(userDocument, "users", userDocument.username, object : Callback<Void> {
            override fun onSuccess(result: Void?) {
                startMainActivity(username)
            }

            override fun onFailed(e: Exception) {
                showErrorMessage(view)
            }
        })
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}
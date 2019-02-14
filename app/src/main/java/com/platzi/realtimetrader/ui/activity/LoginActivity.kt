package com.platzi.realtimetrader.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.platzi.realtimetrader.R
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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
                    startMainActivity(username)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    showErrorMessage(button)
                    view.isEnabled = true
                }
            }
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

package com.example.progettoandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        // Controllo se l'utente è già registrato
        val flag = sharedPreferences.getBoolean("isUserRegistered", false)

        //Nel caso in cui l'utente fosse registrato avvio la LoginActivity altrimenti RegistrationActivity
        val intent = if (flag) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, RegistrazioneActivity::class.java)
        }
        startActivity(intent)
        finish()  // Chiudo questa activity in modo che l'utente non possa tornare indietro
    }
}

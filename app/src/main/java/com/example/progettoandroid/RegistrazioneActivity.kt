package com.example.progettoandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity



class RegistrazioneActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var bottoneRegistrati: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrazione)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        username=findViewById(R.id.editTextUsername)
        password=findViewById(R.id.editTextPassword)
        bottoneRegistrati=findViewById(R.id.button)
        bottoneRegistrati.setOnClickListener {
            // Salvo le credenziali nelle SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean("isUserRegistered", true)
            editor.putString("username", username.text.toString())
            editor.putString("password", password.text.toString())
            editor.apply()
            // Passa all'activity di login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
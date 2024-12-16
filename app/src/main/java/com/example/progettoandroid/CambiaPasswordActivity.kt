package com.example.progettoandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CambiaPasswordActivity : AppCompatActivity() {

    private lateinit var vecchiaPassword: EditText
    private lateinit var nuovaPassword: EditText
    private lateinit var confermaNuovaPassword: EditText
    private lateinit var cambiaPasswordBottone: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambia_password)
        vecchiaPassword = findViewById(R.id.editTextOldPassword)
        nuovaPassword = findViewById(R.id.editTextNewPassword)
        confermaNuovaPassword = findViewById(R.id.editTextConfirmNewPassword)
        cambiaPasswordBottone= findViewById(R.id.buttonChangePassword)

        cambiaPasswordBottone.setOnClickListener {
            cambiaPassword()
        }
    }

    private fun cambiaPassword() {
        val vecchia = vecchiaPassword.text.toString()
        val nuovaP1 = nuovaPassword.text.toString()
        val nuovaP2 = confermaNuovaPassword.text.toString()

        // prendo la password salvata
        val passwordSalvata = getVecchiaPassword()

        if (vecchia == passwordSalvata) {
            // Vecchia password corretta
            if (nuovaP1 == nuovaP2) {
                // Nuove password coincidono, procedo con il cambio
                salvaNuovaPassword(nuovaP1)
                Toast.makeText(this, "Password cambiata con successo.", Toast.LENGTH_SHORT).show()

                // Intent per tornare alla LoginActivity
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()//chiudo l'acitvity ChangePassword..
            } else {
                Toast.makeText(this, "Le nuove password non coincidono.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Vecchia password errata. Riprova.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getVecchiaPassword(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("password", "") ?: ""
    }

    private fun salvaNuovaPassword(newPassword: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("password", newPassword)
        editor.apply()
    }
}

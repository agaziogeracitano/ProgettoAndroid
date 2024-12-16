package com.example.progettoandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var bottoneLogin: Button
    private lateinit var BottoneCambiaPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        username=findViewById(R.id.editTextUsernameLogin)
        password=findViewById(R.id.editTextPasswordLogin)
        bottoneLogin=findViewById(R.id.buttonLogin)
        BottoneCambiaPassword=findViewById(R.id.buttonChangePasswordLogin)

        bottoneLogin.setOnClickListener {
            val usernameInserito = username.text.toString()
            val passwordInserita = password.text.toString()

            // prendo le credenziali salvate
            val usernameSalvato = sharedPreferences.getString("username", "")
            val passwordSalvata = sharedPreferences.getString("password", "")

            if (usernameInserito == usernameSalvato && passwordInserita == passwordSalvata) {
                // Credenziali corrette, passo all'activity principale (MainActivity)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("NOME",usernameSalvato)
                startActivity(intent)
                finish()  // Chiudo questa activity in modo che l'utente non possa tornare indietro
            } else {
                // Credenziali errate
                Toast.makeText(this, "Credenziali non valide", Toast.LENGTH_SHORT).show()
            }
        }
        //se l'utente clicca sul bottone CambiaPassword passo all'activity cambia password
        BottoneCambiaPassword.setOnClickListener{
            val intent = Intent(this, CambiaPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}


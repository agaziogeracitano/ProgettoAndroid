package com.example.progettoandroid

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class ModificaNotaActivity : AppCompatActivity() {

    private lateinit var titoloNota: EditText
    private lateinit var testoNota: EditText
    private lateinit var bottoneSalva: Button
    private lateinit var bottoneLeggi: Button

    lateinit var tts: TextToSpeech
    private lateinit var locationManager: LocationManager

    private lateinit var db: NoteDatabaseHelper
    private var noteId: Int = -1

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifica_nota)
        db = NoteDatabaseHelper(this)

        titoloNota = findViewById(R.id.EditTitle)
        testoNota = findViewById(R.id.EditDescription)
        bottoneSalva = findViewById(R.id.SaveEditNote)
        bottoneLeggi = findViewById(R.id.buttonReadNote)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        val nota = db.getNotaById(noteId)
        titoloNota.setText(nota.title)
        testoNota.setText(nota.content)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.ITALIAN
                tts.setSpeechRate(1.0f)
            } else {
                Log.e("TTS", "Inizializzazione del TextToSpeech fallita")
            }
        })

        bottoneLeggi.setOnClickListener {
            val textToRead = testoNota.text.toString().trim()
            if (textToRead.isNotEmpty()) {
                tts.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null)
            } else {
                Log.e("TTS", "Il campo di testo è vuoto")
                Toast.makeText(this, "Inserisci del testo prima di leggere", Toast.LENGTH_SHORT).show()
            }
        }

        bottoneSalva.setOnClickListener {
            richiestaPermessiLocalizzazione()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun richiestaPermessiLocalizzazione() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun ottieniPosizioneInTempoReale() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                5.0f,
                object : android.location.LocationListener {
                    override fun onLocationChanged(location: Location) {
                        // Rimuovo gli aggiornamenti dopo aver ottenuto una posizione valida
                        locationManager.removeUpdates(this)
                        modificaNota(location)
                    }

                    override fun onProviderDisabled(provider: String) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }
            )
        } else {
            Toast.makeText(
                this,
                "Permessi di localizzazione non concessi. Impossibile ottenere la posizione.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun modificaNota(location: Location) {
        val nuovoTitolo = titoloNota.text.toString()
        val nuovoTesto = testoNota.text.toString()
        val modificaNota = Note(
            noteId,
            nuovoTitolo,
            nuovoTesto,
            System.currentTimeMillis(),
            location.latitude,
            location.longitude
        )
        db.modificaNota(modificaNota)
        finish()
        Toast.makeText(this@ModificaNotaActivity, "Modifiche salvate", Toast.LENGTH_SHORT).show()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ottieniPosizioneInTempoReale()
            } else {
                permessoNegato()
            }
        }
    }

    private fun permessoNegato() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Permesso Negato")
        dialogBuilder.setMessage("Per salvare le note con la posizione è necessaria l'autorizzazione alla posizione.")
        dialogBuilder.setPositiveButton("Impostazioni", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                apriImpostazioni()
            }
        })
        dialogBuilder.setNegativeButton("Esci", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
        dialogBuilder.setCancelable(false)
        dialogBuilder.show()
    }

    private fun apriImpostazioni() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


}



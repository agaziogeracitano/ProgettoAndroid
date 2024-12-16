
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

class AggiungiNotaActivity : AppCompatActivity() {

    private lateinit var titoloNota: EditText
    private lateinit var testoNota: EditText
    private lateinit var bottoneSalvaNota: Button
    private lateinit var bottoneLeggiNota: Button

    lateinit var tts: TextToSpeech

    private lateinit var db: NoteDatabaseHelper
    private lateinit var locationManager: LocationManager

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_nota)
        db = NoteDatabaseHelper(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        titoloNota = findViewById(R.id.editTextAddNoteTitle)
        testoNota = findViewById(R.id.editTextAddNoteDescription)
        bottoneSalvaNota = findViewById(R.id.buttonSaveNote)
        bottoneLeggiNota = findViewById(R.id.buttonReadNote)

        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.ITALIAN
                tts.setSpeechRate(1.0f)
            } else {
                Log.e("TTS", "Inizializzazione del TextToSpeech fallita")
            }
        })

        bottoneLeggiNota.setOnClickListener {
            val textToRead = testoNota.text.toString().trim()
            if (textToRead.isNotEmpty()) {
                tts.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null)
            } else {
                Log.e("TTS", "Il campo di testo è vuoto")
                Toast.makeText(this, "Inserisci del testo prima di leggere", Toast.LENGTH_SHORT).show()
            }
        }

        bottoneSalvaNota.setOnClickListener {
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
                LOCATION_PERMISSION_REQUEST_CODE)
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
                        salvataggioNota(location)
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


    private fun salvataggioNota(location: Location) {
        val nota = creaNota(location)
        db.inserisciNota(nota)
        finish()
        Toast.makeText(this, "Nota salvata", Toast.LENGTH_SHORT).show()
    }

    private fun creaNota(location: Location): Note {
        return Note(
            0,
            titoloNota.text.toString(),
            testoNota.text.toString(),
            System.currentTimeMillis(),
            location.latitude,
            location.longitude
        )
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



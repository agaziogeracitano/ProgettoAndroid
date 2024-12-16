package com.example.progettoandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var titolo: TextView
    private lateinit var listaNote: RecyclerView
    private lateinit var bottoneAggiungiNota: Button

    private lateinit var db: NoteDatabaseHelper
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titolo = findViewById(R.id.textViewMainTitle)
        listaNote=findViewById(R.id.recyclerViewNotes)
        bottoneAggiungiNota = findViewById(R.id.buttonAddNote)
        val nome=intent.getStringExtra("NOME")
        titolo.text="Benvenuto $nome!"

        db=NoteDatabaseHelper(this)
        noteAdapter= NoteAdapter(db.getNote(),this)
        listaNote.layoutManager=LinearLayoutManager(this)
        listaNote.adapter=noteAdapter

        bottoneAggiungiNota.setOnClickListener {
            val intent = Intent(this, AggiungiNotaActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume(){
        super.onResume()
        noteAdapter.refreshData(db.getNote())
    }
}

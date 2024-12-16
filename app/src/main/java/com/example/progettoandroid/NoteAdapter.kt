package com.example.progettoandroid


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter (private var notes: List<Note>, context:Context):
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val db:NoteDatabaseHelper= NoteDatabaseHelper(context)

    class NoteViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val titolo: TextView= itemView.findViewById(R.id.textViewTitle)
        val testo:TextView=itemView.findViewById(R.id.textViewContent)
        val modifica: ImageView=itemView.findViewById(R.id.imageViewEdit)
        val elimina:ImageView=itemView.findViewById(R.id.imageViewDelete)
        val dataOra: TextView = itemView.findViewById(R.id.textViewDateTime)
        val mappa:ImageView=itemView.findViewById(R.id.imageViewLocation)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.note, parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int =notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val nota = notes[position]
        holder.titolo.text = nota.title
        holder.testo.text = nota.content

        val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(nota.timestamp))

        holder.dataOra.text = formattedDateTime

        holder.modifica.setOnClickListener{
            val intent = Intent(holder.itemView.context,ModificaNotaActivity::class.java).apply{
                putExtra("note_id",nota.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.elimina.setOnClickListener{
            db.eliminaNota(nota.id)
            refreshData(db.getNote())
            Toast.makeText(holder.itemView.context,"Note delete",Toast.LENGTH_SHORT).show()
        }

        holder.mappa.setOnClickListener {
            val uri = "geo:${nota.latitude},${nota.longitude}?q=${nota.latitude},${nota.longitude}(${nota.title})"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            holder.itemView.context.startActivity(intent)
        }

    }
    fun refreshData(newNotes:List<Note>){
        notes=newNotes
        notifyDataSetChanged()
    }
}
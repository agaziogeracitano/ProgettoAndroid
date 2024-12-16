package com.example.progettoandroid


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class NoteDatabaseHelper(context: Context): SQLiteOpenHelper(context, NomeDatabase, null, versioneDatabase){

    companion object{
        private const val  NomeDatabase="notesapp.db"
        private const val  versioneDatabase= 6
        private const val  nomeTabella="allnotes"
        private const val  idColonna="id"
        private const val  titoloColonna="title"
        private const val  testoColonna="content"
        private const val timestampColonna = "timestamp"
        private const val latitudineColonna = "latitude"
        private const val longitudineColonna = "longitude"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val database = "CREATE TABLE $nomeTabella ($idColonna INTEGER primary key, $titoloColonna TEXT, $testoColonna TEXT, $timestampColonna INTEGER, $latitudineColonna REAL, $longitudineColonna REAL)"
        db?.execSQL(database)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val t="DROP TABLE IF EXISTS $nomeTabella"
        db?.execSQL(t)
        onCreate(db)
    }

    fun inserisciNota(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(titoloColonna, note.title)
            put(testoColonna, note.content)
            put(timestampColonna, note.timestamp)
            put(latitudineColonna,note.latitude)
            put(longitudineColonna,note.longitude)
        }
        db.insert(nomeTabella, null, values)
        db.close()
    }


    fun getNote(): List<Note> {
        val noteList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $nomeTabella"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(idColonna))
            val titolo = cursor.getString(cursor.getColumnIndexOrThrow(titoloColonna))
            val testo = cursor.getString(cursor.getColumnIndexOrThrow(testoColonna))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(timestampColonna))
            val latitudine=cursor.getDouble(cursor.getColumnIndexOrThrow(latitudineColonna))
            val longitudine=cursor.getDouble(cursor.getColumnIndexOrThrow(longitudineColonna))

            val note = Note(id, titolo, testo, timestamp,latitudine,longitudine)
            noteList.add(note)
        }
        cursor.close()
        db.close()
        return noteList
    }

    fun modificaNota(note:Note){
        val db=writableDatabase
        val values=ContentValues().apply {
            put(titoloColonna,note.title)
            put(testoColonna,note.content)
            put(timestampColonna,note.timestamp)
            put(latitudineColonna,note.latitude)
            put(longitudineColonna,note.longitude)
        }
        val whereClause="$idColonna= ?"
        val whereArgs= arrayOf(note.id.toString())
        db.update(nomeTabella,values,whereClause,whereArgs)
        db.close()
    }

    fun getNotaById(noteId: Int):Note{
        val db=readableDatabase
        val query="SELECT * FROM $nomeTabella WHERE $idColonna=$noteId"
        val cursor=db.rawQuery(query,null)
        cursor.moveToFirst()
        val id= cursor.getInt(cursor.getColumnIndexOrThrow(idColonna))
        val title=cursor.getString(cursor.getColumnIndexOrThrow(titoloColonna))
        val content=cursor.getString(cursor.getColumnIndexOrThrow(testoColonna))
        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(timestampColonna))
        val latitudine=cursor.getDouble(cursor.getColumnIndexOrThrow(latitudineColonna))
        val longitudine=cursor.getDouble(cursor.getColumnIndexOrThrow(longitudineColonna))

        cursor.close()
        db.close()
        return Note(id, title, content, timestamp,latitudine,longitudine)
    }

    fun eliminaNota(noteId:Int){
        val db=writableDatabase
        val whereClause="$idColonna= ?"
        val whereArgs= arrayOf(noteId.toString())
        db.delete(nomeTabella,whereClause,whereArgs)
        db.close()
    }
}
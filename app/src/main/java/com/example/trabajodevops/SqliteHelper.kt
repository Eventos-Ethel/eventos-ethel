package com.example.trabajodevops

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteHelper(context: Context): SQLiteOpenHelper(context, "registroUsuario.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion = "CREATE TABLE usuario (" +
                "nombreUsuario TEXT PRIMARY KEY, " +
                "nombreCompleto TEXT, " +
                "contraseña TEXT)"
        db!!.execSQL(ordenCreacion)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val ordenBorrado = "DROP TABLE IF EXISTS usuario"
        db!!.execSQL(ordenBorrado)
        onCreate(db)
    }

    fun loginUsuario(pNombreUsuario: String, pContraseña: String): ArrayList<Usuario> {
        val listaUsuario = ArrayList<Usuario>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE nombreUsuario = ? AND contraseña = ?",
            arrayOf(pNombreUsuario.lowercase(), pContraseña)
        )

        if (cursor.moveToFirst()) {
            do {
                val nombreCompleto =
                    cursor.getString(cursor.getColumnIndexOrThrow("nombreCompleto"))
                val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombreUsuario"))
                val contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))

                listaUsuario.add(Usuario(nombreCompleto, nombreUsuario, contraseña))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaUsuario
    }

    fun usuarioExiste(nombreUsuario: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE lower(nombreUsuario) = ?",
            arrayOf(nombreUsuario.lowercase())
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun registrarUsuario(param: Usuario): Boolean {
        if (usuarioExiste(param.NombreUsuario)) {
            return false
        }

        val datos = ContentValues().apply {
            put("nombreUsuario", param.NombreUsuario)
            put("nombreCompleto", param.NombreCompleto)
            put("contraseña", param.Contraseña)
        }

        val db = this.writableDatabase
        db.insert("usuario", null, datos)
        db.close()
        return true
    }

    fun obtenerUsuario(nombreUsuario: String): Usuario? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE lower(nombreUsuario) = ?",
            arrayOf(nombreUsuario.lowercase())
        )
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            val nombreCompleto = cursor.getString(cursor.getColumnIndexOrThrow("nombreCompleto"))
            val contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))
            usuario = Usuario(nombreCompleto, nombreUsuario, contraseña)
        }

        cursor.close()
        db.close()
        return usuario
    }
}
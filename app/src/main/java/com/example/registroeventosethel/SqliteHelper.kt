package com.example.registroeventosethel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SqliteHelper(context: Context) : SQLiteOpenHelper(context, "registroUsuario.db", null, 7) { // Cambiado a versión 4

    companion object {
        private const val DATE_FORMAT = "dd/MM/yyyy"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de usuarios
        db.execSQL("""
            CREATE TABLE usuario (
                nombreUsuario TEXT PRIMARY KEY,
                nombreCompleto TEXT,
                contraseña TEXT,
                esAdmin INTEGER DEFAULT 0
            )
        """)

        db.execSQL("""
            INSERT INTO usuario (nombreUsuario, nombreCompleto, contraseña, esAdmin)
            VALUES ('admin', 'Administrador General', 'admin123', 1)
        """)

        db.execSQL("""CREATE TABLE evento_servicio (
    id_evento INTEGER,
    id_servicio INTEGER,
    FOREIGN KEY(id_evento) REFERENCES eventos(id),
    FOREIGN KEY(id_servicio) REFERENCES servicios(id)
)
""")

        // Tabla de proveedores
        db.execSQL("""
            CREATE TABLE proveedores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT,
                tipo TEXT,
                nombre TEXT,
                direccion TEXT,
                provincia TEXT,
                distrito TEXT,
                telefono TEXT,
                correo TEXT,
                precio Double
            )
        """)

        // Tabla de servicios
        db.execSQL("""
            CREATE TABLE servicios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_proveedor INTEGER,
                codigo TEXT,
                nombre TEXT,
                tipo TEXT,
                costo DOUBLE,
                FOREIGN KEY(id_proveedor) REFERENCES proveedores(id)
            )
        """)

        // Tabla de eventos (actualizada con más campos)
        db.execSQL("""
            CREATE TABLE eventos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreCliente TEXT,
                dni TEXT,
                celular TEXT,
                descripcion TEXT,
                fecha TEXT,
                hora TEXT,
                tipo TEXT,
                invitados INTEGER,
                ubicacion TEXT,
                precio DOUBLE,
                id_servicio INTEGER,
                FOREIGN KEY(id_servicio) REFERENCES servicios(id)
            )
        """)

        db.execSQL("""CREATE TABLE IF NOT EXISTS evento_servicio (
    id_evento INTEGER,
    id_servicio INTEGER,
    FOREIGN KEY(id_evento) REFERENCES eventos(id),
    FOREIGN KEY(id_servicio) REFERENCES servicios(id)
)""")


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS proveedores")
        db.execSQL("DROP TABLE IF EXISTS servicios")
        db.execSQL("DROP TABLE IF EXISTS eventos")
        db.execSQL("DROP TABLE IF EXISTS evento_servicio")
        onCreate(db)
    }

    // Métodos para usuarios (sin cambios)
    fun loginUsuario(pNombreUsuario: String, pContraseña: String): ArrayList<Usuario> {
        val listaUsuario = ArrayList<Usuario>()
        val db = readableDatabase
        db.rawQuery(
            "SELECT * FROM usuario WHERE nombreUsuario = ? AND contraseña = ?",
            arrayOf(pNombreUsuario.lowercase(), pContraseña)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listaUsuario.add(Usuario(
                        cursor.getString(cursor.getColumnIndexOrThrow("nombreCompleto")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombreUsuario")),
                        cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))
                    ))
                } while (cursor.moveToNext())
            }
        }
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
            put("esAdmin", 0)
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
            val esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1

            usuario = Usuario(
                nombreCompleto = nombreCompleto,
                nombreUsuario = nombreUsuario,
                contraseña = contraseña,
                esAdmin = esAdmin
            )
        }


        cursor.close()
        db.close()
        return usuario
    }

    // Métodos para proveedores (sin cambios)
    fun registrarProveedor(
        codigo: String, tipo: String, nombre: String, direccion: String,
        provincia: String, distrito: String, telefono: String, correo: String
    ): Long {
        if (codigo.isBlank() || nombre.isBlank() || telefono.isBlank() || correo.isBlank()) {
            Log.e("DB_VALIDATION", "Campos requeridos vacíos")
            return -1
        }

        val values = ContentValues().apply {
            put("codigo", codigo)
            put("tipo", tipo)
            put("nombre", nombre)
            put("direccion", direccion)
            put("provincia", provincia)
            put("distrito", distrito)
            put("telefono", telefono)
            put("correo", correo)
        }

        return writableDatabase.insert("proveedores", null, values)
    }

    // Métodos para servicios (sin cambios)
    fun registrarServicio(
        idProveedor: Long, codigo: String, nombre: String, tipo: String, costo: Double
    ): Boolean {
        if (idProveedor <= 0 || codigo.isBlank() || nombre.isBlank() || tipo.isBlank()) {
            Log.e("DB_VALIDATION", "Datos de servicio inválidos")
            return false
        }

        val values = ContentValues().apply {
            put("id_proveedor", idProveedor)
            put("codigo", codigo)
            put("nombre", nombre)
            put("tipo", tipo)
            put("costo", costo)
        }

        return writableDatabase.insert("servicios", null, values) != -1L
    }


    // Métodos para eventos (actualizados)
    fun registrarEvento(
        nombre: String, dni: String, celular: String, descripcion: String,
        fecha: String, hora: String, tipo: String, invitados: Int,
        ubicacion: String, precio: Double, idServicio: Long
    ): Long {
        val values = ContentValues().apply {
            put("nombreCliente", nombre)
            put("dni", dni)
            put("celular", celular)
            put("descripcion", descripcion)
            put("fecha", fecha)
            put("hora", hora)
            put("tipo", tipo)
            put("invitados", invitados)
            put("ubicacion", ubicacion)
            put("precio", precio)
            put("id_servicio", idServicio)
        }

        return writableDatabase.insert("eventos", null, values)
    }

    // Método para obtener todos los eventos
    fun obtenerEventos(): List<Evento> {
        val lista = mutableListOf<Evento>()
        readableDatabase.rawQuery("SELECT * FROM eventos ORDER BY fecha", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(Evento(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombreCliente")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    hora = cursor.getString(cursor.getColumnIndexOrThrow("hora")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    celular = cursor.getString(cursor.getColumnIndexOrThrow("celular")),
                    invitados = cursor.getInt(cursor.getColumnIndexOrThrow("invitados")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                ))
            }
        }
        return lista
    }

    // Método para obtener eventos filtrados
    fun obtenerEventosFiltrados(
        fechaInicio: LocalDate? = null,
        fechaFin: LocalDate? = null,
        lugar: String? = null,
        nombreCliente: String? = null
    ): List<Evento> {
        val lista = mutableListOf<Evento>()
        val selection = StringBuilder()
        val selectionArgs = mutableListOf<String>()

        if (fechaInicio != null && fechaFin != null) {
            selection.append("fecha BETWEEN ? AND ?")
            selectionArgs.add(fechaInicio.format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
            selectionArgs.add(fechaFin.format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
        }

        if (lugar != null) {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("ubicacion LIKE ?")
            selectionArgs.add("%$lugar%")
        }

        if (nombreCliente != null) {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("nombreCliente LIKE ?")
            selectionArgs.add("%$nombreCliente%")
        }

        readableDatabase.query(
            "eventos",
            null,
            if (selection.isNotEmpty()) selection.toString() else null,
            if (selectionArgs.isNotEmpty()) selectionArgs.toTypedArray() else null,
            null, null, "fecha ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(Evento(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombreCliente")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    hora = cursor.getString(cursor.getColumnIndexOrThrow("hora")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    celular = cursor.getString(cursor.getColumnIndexOrThrow("celular")),
                    invitados = cursor.getInt(cursor.getColumnIndexOrThrow("invitados")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                ))
            }
        }
        return lista
    }

    // Método para obtener lugares únicos
    fun obtenerLugaresUnicos(): List<String> {
        val lugares = mutableListOf<String>()
        readableDatabase.rawQuery(
            "SELECT DISTINCT ubicacion FROM eventos WHERE ubicacion IS NOT NULL ORDER BY ubicacion",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lugares.add(cursor.getString(0))
            }
        }
        return lugares
    }

    fun obtenerProveedorPorId(id: Long): Proveedor? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM proveedores WHERE id = ?", arrayOf(id.toString()))
        var proveedor: Proveedor? = null

        if (cursor.moveToFirst()) {
            proveedor = Proveedor(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                provincia = cursor.getString(cursor.getColumnIndexOrThrow("provincia")),
                distrito = cursor.getString(cursor.getColumnIndexOrThrow("distrito")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            )
        }

        cursor.close()
        db.close()
        return proveedor
    }

    // Métodos adicionales para proveedores y servicios (sin cambios)
    fun obtenerProveedores(): List<Proveedor> {
        val lista = mutableListOf<Proveedor>()
        readableDatabase.rawQuery("SELECT * FROM proveedores", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(Proveedor(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    provincia = cursor.getString(cursor.getColumnIndexOrThrow("provincia")),
                    distrito = cursor.getString(cursor.getColumnIndexOrThrow("distrito")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                ))
            }
        }
        return lista
    }

    fun obtenerServicios(): List<Servicio> {
        val lista = mutableListOf<Servicio>()
        readableDatabase.rawQuery("SELECT * FROM servicios", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(Servicio(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                    idProveedor = cursor.getLong(cursor.getColumnIndexOrThrow("id_proveedor"))
                ))
            }
        }
        return lista
    }

    // Métodos para eliminar registros
    fun eliminarEvento(id: Long): Boolean {
        return writableDatabase.delete("eventos", "id = ?", arrayOf(id.toString())) > 0
    }

    fun actualizarProveedor(
        id: Long,
        codigo: String,
        tipo: String,
        nombre: String,
        direccion: String,
        provincia: String,
        distrito: String,
        telefono: String,
        correo: String
    ): Boolean {
        val db = this.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("codigo", codigo)
                put("tipo", tipo)
                put("nombre", nombre)
                put("direccion", direccion)
                put("provincia", provincia)
                put("distrito", distrito)
                put("telefono", telefono)
                put("correo", correo)
            }
            db.update("proveedores", values, "id = ?", arrayOf(id.toString()))
            db.close()
            true
        } catch (e: Exception) {
            Log.e("DB_EXCEPTION", "Excepción al actualizar proveedor: ${e.message}")
            db.close()
            false
        }
    }

    fun eliminarProveedor(id: Long): Boolean {
        val db = this.writableDatabase
        return try {
            db.delete("proveedores", "id = ?", arrayOf(id.toString()))
            db.close()
            true
        } catch (e: Exception) {
            Log.e("DB_EXCEPTION", "Excepción al eliminar proveedor: ${e.message}")
            db.close()
            false
        }
    }

    fun actualizarEvento(
        id: Long,
        nombre: String,
        dni: String,
        celular: String,
        descripcion: String,
        fecha: String,
        hora: String,
        tipo: String,
        invitados: Int,
        ubicacion: String,
        precio: Double
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombreCliente", nombre)
            put("dni", dni)
            put("celular", celular)
            put("descripcion", descripcion)
            put("fecha", fecha)
            put("hora", hora)
            put("tipo", tipo)
            put("invitados", invitados)
            put("ubicacion", ubicacion)
            put("precio", precio)
        }
        return db.update("eventos", values, "id = ?", arrayOf(id.toString())) > 0
    }

    fun generarCodigoProveedor(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM proveedores", null)
        var codigo = "PROV001"
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0) + 1
            codigo = "PROV" + String.format("%03d", count)
        }
        cursor.close()
        return codigo
    }

    fun actualizarProveedor(p: Proveedor): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", p.nombre)
            put("telefono", p.telefono)
            put("tipo", p.tipo)
            put("precio", p.precio)
        }
        return db.update("proveedores", values, "codigo = ?", arrayOf(p.codigo)) > 0
    }

    fun insertarProveedor(p: Proveedor): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("codigo", p.codigo)
            put("tipo", p.tipo)
            put("nombre", p.nombre)
            put("direccion", p.direccion)
            put("provincia", p.provincia)
            put("distrito", p.distrito)
            put("telefono", p.telefono)
            put("correo", p.correo)
            put("precio", p.precio)
        }
        return db.insert("proveedores", null, values)
    }

    fun generarCodigoServicio(): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM servicios", null)
        var codigo = "SERV001"
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0) + 1
            codigo = "SERV" + String.format("%03d", count)
        }
        cursor.close()
        return codigo
    }

    fun insertarEventoServicio(idEvento: Long, idServicio: Long): Boolean {
        val values = ContentValues().apply {
            put("id_evento", idEvento)
            put("id_servicio", idServicio)
        }
        return writableDatabase.insert("evento_servicio", null, values) != -1L
    }

    fun obtenerEventosPorSemana(): Map<String, Int> {
        val resultado = mutableMapOf<String, Int>()
        val query = """
        SELECT strftime('%Y-%W', fecha) AS semana, COUNT(*) AS total
        FROM eventos
        GROUP BY semana
        ORDER BY semana
    """.trimIndent()

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val semana = cursor.getString(cursor.getColumnIndexOrThrow("semana"))
                val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
                resultado[semana] = total
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return resultado
    }

    fun obtenerServicioPorProveedorId(idProveedor: Long): Servicio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM servicios WHERE id_proveedor = ? LIMIT 1",
            arrayOf(idProveedor.toString())
        )

        var servicio: Servicio? = null
        if (cursor.moveToFirst()) {
            servicio = Servicio(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                idProveedor = cursor.getLong(cursor.getColumnIndexOrThrow("id_proveedor"))
            )
        }

        cursor.close()
        db.close()
        return servicio
    }

    fun actualizarServicio(servicio: Servicio): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("codigo", servicio.codigo)
            put("nombre", servicio.nombre)
            put("tipo", servicio.tipo)
            put("costo", servicio.costo)
        }
        val resultado = db.update("servicios", values, "id = ?", arrayOf(servicio.id.toString()))
        db.close()
        return resultado > 0
    }

    fun obtenerServicioPorProveedor(idProveedor: Long): Servicio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM servicios WHERE id_proveedor = ? LIMIT 1",
            arrayOf(idProveedor.toString())
        )

        var servicio: Servicio? = null
        if (cursor.moveToFirst()) {
            servicio = Servicio(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                idProveedor = cursor.getLong(cursor.getColumnIndexOrThrow("id_proveedor"))
            )
        }

        cursor.close()
        return servicio
    }

    fun actualizarServicio(id: Long, nombre: String, tipo: String, costo: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("costo", costo)
        }
        return db.update("servicios", values, "id = ?", arrayOf(id.toString())) > 0
    }



}
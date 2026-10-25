
package com.example.pm01examen119140044;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactos.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CONTACTOS = "contactos";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_CONTACTOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pais TEXT NOT NULL, " +
                "nombre TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "nota TEXT NOT NULL, " +
                "imagen BLOB)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }

    public long insertarContacto(Contactos contacto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("pais", contacto.getPais());
        valores.put("nombre", contacto.getNombre());
        valores.put("telefono", contacto.getTelefono());
        valores.put("nota", contacto.getNota());
        valores.put("imagen", contacto.getImagen());

        long resultado = db.insert(TABLE_CONTACTOS, null, valores);
        db.close();
        return resultado;
    }

    // NUEVO: obtener un contacto por su ID para editarlo.
    public Contactos obtenerContactoPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CONTACTOS,
                new String[]{"id", "pais", "nombre",
                        "telefono", "nota", "imagen"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Contactos contacto = null;

        try {
            if (cursor.moveToFirst()) {
                contacto = new Contactos(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pais")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nota")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("imagen"))
                );
            }
        } finally {
            cursor.close();
            db.close();
        }

        return contacto;
    }

    public ArrayList<Contactos> obtenerContactos() {
        ArrayList<Contactos> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_CONTACTOS +
                        " ORDER BY nombre COLLATE NOCASE ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {
                Contactos contacto = new Contactos(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pais")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nota")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("imagen"))
                );

                lista.add(contacto);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return lista;
    }

    public int actualizarContacto(Contactos contacto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("pais", contacto.getPais());
        valores.put("nombre", contacto.getNombre());
        valores.put("telefono", contacto.getTelefono());
        valores.put("nota", contacto.getNota());
        valores.put("imagen", contacto.getImagen());

        int resultado = db.update(
                TABLE_CONTACTOS,
                valores,
                "id = ?",
                new String[]{String.valueOf(contacto.getId())}
        );

        db.close();
        return resultado;
    }

    public int eliminarContacto(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int resultado = db.delete(
                TABLE_CONTACTOS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return resultado;
    }
}

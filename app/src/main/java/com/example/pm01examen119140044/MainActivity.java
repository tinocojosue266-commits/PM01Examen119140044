
package com.example.pm01examen119140044;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int SELECCIONAR_IMAGEN = 100;

    private ImageView imgContacto;
    private Spinner spPais;
    private EditText etNombre, etTelefono, etNota;
    private DatabaseHelper databaseHelper;

    private byte[] imagenBytes = null;
    private int idEdicion = -1;

    private Button btnGuardar;

    private final String[] paises = {
            "Honduras (+504)",
            "Costa Rica (+506)",
            "Guatemala (+502)",
            "El Salvador (+503)",
            "Nicaragua (+505)",
            "Estados Unidos (+1)",
            "México (+52)",
            "Colombia (+57)",
            "España (+34)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(0xFFF1F6FC);
        getWindow().setNavigationBarColor(0xFFF1F6FC);

        WindowInsetsControllerCompat controlador =
                new WindowInsetsControllerCompat(
                        getWindow(), getWindow().getDecorView());

        controlador.setAppearanceLightStatusBars(true);
        controlador.setAppearanceLightNavigationBars(true);

        setContentView(R.layout.activity_main);

        ViewGroup contenido = findViewById(android.R.id.content);
        View raiz = contenido.getChildAt(0);

        final int paddingIzquierdo = raiz.getPaddingLeft();
        final int paddingSuperior = raiz.getPaddingTop();
        final int paddingDerecho = raiz.getPaddingRight();
        final int paddingInferior = raiz.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars());

            vista.setPadding(
                    paddingIzquierdo + barras.left,
                    paddingSuperior + barras.top,
                    paddingDerecho + barras.right,
                    paddingInferior + barras.bottom
            );

            return insets;
        });

        ViewCompat.requestApplyInsets(raiz);

        databaseHelper = new DatabaseHelper(this);

        imgContacto = findViewById(R.id.imgContacto);
        spPais = findViewById(R.id.spPais);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);

        Button btnImagen = findViewById(R.id.btnImagen);
        btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVerContactos = findViewById(R.id.btnVerContactos);

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paises
        );

        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spPais.setAdapter(adaptador);

        btnImagen.setOnClickListener(v -> seleccionarImagen());
        btnGuardar.setOnClickListener(v -> guardarContacto());

        btnVerContactos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ListaContactosActivity.class);
            startActivity(intent);
        });

        // Si recibimos un ID, abrimos el formulario en modo edición.
        idEdicion = getIntent().getIntExtra("editar_id", -1);

        if (idEdicion != -1) {
            cargarContactoParaEditar(idEdicion);
        }
    }

    private void cargarContactoParaEditar(int id) {
        Contactos contacto = databaseHelper.obtenerContactoPorId(id);

        if (contacto == null) {
            Toast.makeText(this,
                    "No se encontró el contacto",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etNombre.setText(contacto.getNombre());
        etTelefono.setText(contacto.getTelefono());
        etNota.setText(contacto.getNota());

        for (int i = 0; i < paises.length; i++) {
            if (paises[i].equals(contacto.getPais())) {
                spPais.setSelection(i);
                break;
            }
        }

        // Conservar la imagen original si no se selecciona otra.
        imagenBytes = contacto.getImagen();

        if (imagenBytes != null && imagenBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    imagenBytes, 0, imagenBytes.length);

            if (bitmap != null) {
                imgContacto.setImageBitmap(bitmap);
                imgContacto.setPadding(0, 0, 0, 0);
                imgContacto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }

        btnGuardar.setText("Actualizar contacto");
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_IMAGEN);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECCIONAR_IMAGEN
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {

            Uri uri = data.getData();

            try (InputStream input =
                         getContentResolver().openInputStream(uri)) {

                Bitmap bitmap = BitmapFactory.decodeStream(input);

                if (bitmap != null) {
                    ByteArrayOutputStream salida =
                            new ByteArrayOutputStream();

                    bitmap.compress(
                            Bitmap.CompressFormat.JPEG, 80, salida);

                    imagenBytes = salida.toByteArray();
                    imgContacto.setImageBitmap(bitmap);
                    imgContacto.setPadding(0, 0, 0, 0);
                    imgContacto.setScaleType(
                            ImageView.ScaleType.CENTER_CROP);
                }

            } catch (Exception e) {
                Toast.makeText(this,
                        "No se pudo cargar la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();
        String pais = spPais.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese el nombre");
            etNombre.requestFocus();
            return;
        }

        if (!Pattern.matches(
                "^[\\p{L}]+([ '-][\\p{L}]+)*$", nombre)) {
            etNombre.setError(
                    "Use letras, espacios, apóstrofes o guiones");
            etNombre.requestFocus();
            return;
        }

        if (telefono.isEmpty()) {
            etTelefono.setError("Ingrese el teléfono");
            etTelefono.requestFocus();
            return;
        }

        if (!Pattern.matches("^[0-9]{7,15}$", telefono)) {
            etTelefono.setError(
                    "Ingrese entre 7 y 15 dígitos, sin espacios");
            etTelefono.requestFocus();
            return;
        }

        if (nota.isEmpty()) {
            etNota.setError("Ingrese una nota");
            etNota.requestFocus();
            return;
        }

        Contactos contacto = new Contactos(
                idEdicion == -1 ? pais : pais,
                nombre,
                telefono,
                nota,
                imagenBytes
        );

        long resultado;

        if (idEdicion != -1) {
            contacto.setId(idEdicion);
            resultado = databaseHelper.actualizarContacto(contacto);
        } else {
            resultado = databaseHelper.insertarContacto(contacto);
        }

        if (resultado > 0) {
            String mensaje = idEdicion != -1
                    ? "Contacto actualizado correctamente"
                    : "Contacto guardado correctamente";

            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

            if (idEdicion != -1) {
                // Volver a la lista para ver los datos actualizados.
                finish();
                return;
            }

            limpiarFormulario();

        } else {
            Toast.makeText(this,
                    idEdicion != -1
                            ? "No se pudo actualizar el contacto"
                            : "Error al guardar el contacto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etTelefono.setText("");
        etNota.setText("");
        spPais.setSelection(0);

        imagenBytes = null;

        imgContacto.setImageResource(
                android.R.drawable.ic_menu_camera);
        imgContacto.setPadding(24, 24, 24, 24);
        imgContacto.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE);
    }
}

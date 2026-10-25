
package com.example.pm01examen119140044;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LlamarActivity extends AppCompatActivity {

    private String numeroCompleto;
    private String nombre;

    private static final int SOLICITUD_PERMISO_LLAMADA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");
        String pais = getIntent().getStringExtra("pais");

        if (nombre == null) nombre = "Contacto";
        if (telefono == null) telefono = "";
        if (pais == null) pais = "Honduras (+504)";

        String codigo = "+504";
        int inicio = pais.indexOf('+');
        int fin = pais.indexOf(')', inicio);

        if (inicio >= 0) {
            codigo = fin > inicio
                    ? pais.substring(inicio, fin)
                    : pais.substring(inicio);
        }

        numeroCompleto = codigo + telefono;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(0xFFF1F6FC);

        TextView titulo = new TextView(this);
        titulo.setText("Llamar a " + nombre);
        titulo.setTextSize(22);
        titulo.setGravity(Gravity.CENTER);
        titulo.setTextColor(0xFF123B63);

        TextView numero = new TextView(this);
        numero.setText(numeroCompleto);
        numero.setTextSize(18);
        numero.setGravity(Gravity.CENTER);
        numero.setTextColor(0xFF444444);

        Button llamar = new Button(this);
        llamar.setText("LLAMAR");
        llamar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF1769AA)
        );

        layout.addView(titulo);
        layout.addView(numero);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        parametros.topMargin = 12;
        layout.addView(llamar, parametros);

        Button regresar = new Button(this);
        regresar.setText("Regresar");
        layout.addView(regresar);

        setContentView(layout);

        llamar.setOnClickListener(v -> confirmarLlamada());
        regresar.setOnClickListener(v -> finish());
    }

    private void confirmarLlamada() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirmar llamada")
                .setMessage("¿Desea llamar a " + nombre + "?\n"
                        + numeroCompleto)
                .setNegativeButton("No", (dialog, which) ->
                        dialog.dismiss())
                .setPositiveButton("Sí", (dialog, which) ->
                        realizarLlamada())
                .show();
    }

    private void realizarLlamada() {
        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.CALL_PHONE},
                    SOLICITUD_PERMISO_LLAMADA
            );
            return;
        }

        ejecutarLlamada();
    }

    private void ejecutarLlamada() {
        try {
            Intent intent = new Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:" + numeroCompleto)
            );

            startActivity(intent);

        } catch (SecurityException e) {
            Toast.makeText(
                    this,
                    "No se concedió el permiso para llamar",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "No se pudo realizar la llamada",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == SOLICITUD_PERMISO_LLAMADA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                ejecutarLlamada();

            } else {
                Toast.makeText(
                        this,
                        "Debes conceder el permiso para realizar llamadas",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}

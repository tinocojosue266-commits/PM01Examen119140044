
package com.example.pm01examen119140044;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.ArrayList;
import java.util.Locale;

public class ListaContactosActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listaView;
    private EditText buscador;
    private ArrayList<Contactos> contactos;
    private ArrayList<Contactos> filtrados;
    private AdaptadorContactos adaptador;
    private LinearLayout contenedor;

    private final int azul = Color.rgb(25, 99, 179);
    private final int turquesa = Color.rgb(20, 145, 170);
    private final int fondo = Color.rgb(241, 246, 252);
    private final int textoOscuro = Color.rgb(18, 54, 99);
    private final int azulSeleccion = Color.rgb(214, 233, 255);

    private int seleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(fondo);
        getWindow().setNavigationBarColor(fondo);

        WindowInsetsControllerCompat controlador =
                new WindowInsetsControllerCompat(
                        getWindow(), getWindow().getDecorView());

        controlador.setAppearanceLightStatusBars(true);
        controlador.setAppearanceLightNavigationBars(true);

        db = new DatabaseHelper(this);

        construirPantalla();
        cargarContactos();
    }

    private int dp(float valor) {
        return (int) (valor * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable forma(int color, int radio) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radio));
        return drawable;
    }

    private TextView crearTitulo(String texto, int tamano) {
        TextView t = new TextView(this);
        t.setText(texto);
        t.setTextSize(tamano);
        t.setTextColor(textoOscuro);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    private Button crearBoton(String texto, int color) {
        Button b = new Button(this);
        b.setText(texto);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackground(forma(color, 24));
        b.setMinHeight(dp(46));
        b.setPadding(dp(8), dp(6), dp(8), dp(6));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1);

        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        b.setLayoutParams(lp);

        return b;
    }

    private void construirPantalla() {

        contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(20), dp(18), dp(20), dp(16));
        contenedor.setBackgroundColor(fondo);

        ViewCompat.setOnApplyWindowInsetsListener(
                contenedor, (vista, windowInsets) -> {

                    Insets barras = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    vista.setPadding(
                            dp(20),
                            dp(18) + barras.top,
                            dp(20),
                            dp(16) + barras.bottom
                    );

                    return windowInsets;
                });

        TextView titulo = crearTitulo("Gestión de contactos", 25);

        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        tituloParams.setMargins(0, dp(4), 0, dp(2));
        contenedor.addView(titulo, tituloParams);

        TextView subtitulo =
                crearTitulo("CONTACTOS GUARDADOS", 12);

        subtitulo.setTextColor(turquesa);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        subParams.setMargins(0, dp(2), 0, dp(14));
        contenedor.addView(subtitulo, subParams);

        buscador = new EditText(this);
        buscador.setSingleLine(true);
        buscador.setTextSize(15);
        buscador.setHint("Buscar contacto");
        buscador.setPadding(dp(14), dp(8), dp(14), dp(8));
        buscador.setBackground(forma(Color.WHITE, 14));

        LinearLayout.LayoutParams buscarParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        buscarParams.setMargins(0, 0, 0, dp(10));
        contenedor.addView(buscador, buscarParams);

        listaView = new ListView(this);
        listaView.setDividerHeight(dp(1));
        listaView.setDivider(new ColorDrawable(0xFFD6E2EF));
        listaView.setBackground(forma(Color.WHITE, 14));
        listaView.setPadding(dp(5), dp(5), dp(5), dp(5));
        listaView.setClipToOutline(true);
        listaView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        contenedor.addView(
                listaView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1));

        LinearLayout fila1 = new LinearLayout(this);
        fila1.setOrientation(LinearLayout.HORIZONTAL);

        Button actualizar = crearBoton("Actualizar", azul);
        Button eliminar = crearBoton(
                "Eliminar", Color.rgb(217, 83, 79));

        fila1.addView(actualizar);
        fila1.addView(eliminar);
        contenedor.addView(fila1);

        LinearLayout fila2 = new LinearLayout(this);
        fila2.setOrientation(LinearLayout.HORIZONTAL);

        Button compartir = crearBoton("Compartir", turquesa);
        Button verImagen = crearBoton("Ver imagen", azul);

        fila2.addView(compartir);
        fila2.addView(verImagen);
        contenedor.addView(fila2);

        Button llamar = crearBoton("Llamar", turquesa);

        LinearLayout.LayoutParams llamarParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        llamarParams.setMargins(dp(4), dp(4), dp(4), dp(4));
        llamar.setLayoutParams(llamarParams);
        contenedor.addView(llamar);

        Button atras = crearBoton(
                "← Regresar al formulario", azul);

        LinearLayout.LayoutParams atrasParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        atrasParams.setMargins(dp(4), dp(4), dp(4), 0);
        atras.setLayoutParams(atrasParams);
        contenedor.addView(atras);

        setContentView(contenedor);

        listaView.setOnItemClickListener(
                (parent, view, position, id) -> {

                    seleccionado = position;
                    adaptador.notifyDataSetChanged();

                    Toast.makeText(
                            this,
                            "Contacto seleccionado: "
                                    + filtrados.get(position).getNombre(),
                            Toast.LENGTH_SHORT
                    ).show();
                });

        buscador.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start,
                            int count, int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s, int start,
                            int before, int count) {

                        seleccionado = -1;
                        aplicarFiltro(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {
                    }
                });

        atras.setOnClickListener(v -> finish());

        actualizar.setOnClickListener(v -> {

            Contactos c = obtenerSeleccionado(seleccionado);
            if (c == null) return;

            Intent i = new Intent(
                    ListaContactosActivity.this,
                    MainActivity.class);

            i.putExtra("editar_id", c.getId());
            startActivity(i);
        });

        eliminar.setOnClickListener(v -> {

            Contactos c = obtenerSeleccionado(seleccionado);
            if (c == null) return;

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar contacto")
                    .setMessage("¿Desea eliminar a "
                            + c.getNombre() + "?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar", (d, w) -> {

                        db.eliminarContacto(c.getId());
                        seleccionado = -1;
                        cargarContactos();

                        aviso("Contacto eliminado");
                    })
                    .show();
        });

        compartir.setOnClickListener(v -> {

            Contactos c = obtenerSeleccionado(seleccionado);
            if (c == null) return;

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");

            i.putExtra(
                    Intent.EXTRA_TEXT,
                    "Nombre: " + c.getNombre()
                            + "\nPaís: " + c.getPais()
                            + "\nTeléfono: " + c.getTelefono()
                            + "\nNota: " + c.getNota());

            startActivity(
                    Intent.createChooser(i, "Compartir contacto"));
        });

        verImagen.setOnClickListener(v -> {

            Contactos c = obtenerSeleccionado(seleccionado);
            if (c == null) return;

            if (c.getImagen() == null
                    || c.getImagen().length == 0) {

                aviso("Este contacto no tiene imagen guardada");
                return;
            }

            Intent i = new Intent(this, VerImagenActivity.class);
            i.putExtra("imagen", c.getImagen());
            startActivity(i);
        });

        llamar.setOnClickListener(v -> {

            Contactos c = obtenerSeleccionado(seleccionado);
            if (c == null) return;

            Intent i = new Intent(this, LlamarActivity.class);

            i.putExtra("nombre", c.getNombre());
            i.putExtra("telefono", c.getTelefono());
            i.putExtra("pais", c.getPais());

            startActivity(i);
        });
    }

    private Contactos obtenerSeleccionado(int posicion) {

        if (filtrados == null
                || posicion < 0
                || posicion >= filtrados.size()) {

            aviso("Seleccione un contacto primero");
            return null;
        }

        return filtrados.get(posicion);
    }

    private void cargarContactos() {

        contactos = db.obtenerContactos();

        if (contactos == null) {
            contactos = new ArrayList<>();
        }

        aplicarFiltro(
                buscador == null
                        ? ""
                        : buscador.getText().toString());
    }

    private void aplicarFiltro(String texto) {

        filtrados = new ArrayList<>();

        String q = texto.toLowerCase(Locale.ROOT).trim();

        for (Contactos c : contactos) {

            String nombre = c.getNombre() == null
                    ? ""
                    : c.getNombre().toLowerCase(Locale.ROOT);

            String telefono = c.getTelefono() == null
                    ? ""
                    : c.getTelefono();

            String pais = c.getPais() == null
                    ? ""
                    : c.getPais().toLowerCase(Locale.ROOT);

            if (nombre.contains(q)
                    || telefono.contains(q)
                    || pais.contains(q)) {

                filtrados.add(c);
            }
        }

        seleccionado = -1;

        adaptador = new AdaptadorContactos();
        listaView.setAdapter(adaptador);
    }

    private class AdaptadorContactos extends BaseAdapter {

        @Override
        public int getCount() {
            return filtrados == null ? 0 : filtrados.size();
        }

        @Override
        public Object getItem(int position) {
            return filtrados.get(position);
        }

        @Override
        public long getItemId(int position) {
            return filtrados.get(position).getId();
        }

        @Override
        public View getView(
                int position, View convertView, ViewGroup parent) {

            LinearLayout fila;
            ImageView foto;
            LinearLayout textos;
            TextView nombre;
            TextView detalles;

            if (convertView == null) {

                fila = new LinearLayout(
                        ListaContactosActivity.this);

                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setGravity(Gravity.CENTER_VERTICAL);
                fila.setPadding(dp(10), dp(8), dp(10), dp(8));

                foto = new ImageView(
                        ListaContactosActivity.this);

                foto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                foto.setBackground(forma(0xFFE2ECF7, 12));
                foto.setClipToOutline(true);

                LinearLayout.LayoutParams fotoParams =
                        new LinearLayout.LayoutParams(
                                dp(64), dp(64));

                fotoParams.setMargins(0, 0, dp(12), 0);
                fila.addView(foto, fotoParams);

                textos = new LinearLayout(
                        ListaContactosActivity.this);

                textos.setOrientation(LinearLayout.VERTICAL);
                textos.setGravity(Gravity.CENTER_VERTICAL);

                nombre = new TextView(
                        ListaContactosActivity.this);

                nombre.setTextColor(textoOscuro);
                nombre.setTextSize(16);
                nombre.setTypeface(null, Typeface.BOLD);

                detalles = new TextView(
                        ListaContactosActivity.this);

                detalles.setTextColor(0xFF52677F);
                detalles.setTextSize(13);
                detalles.setPadding(0, dp(4), 0, 0);

                textos.addView(nombre);
                textos.addView(detalles);

                fila.addView(
                        textos,
                        new LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1));

                fila.setTag(new VistaContacto(
                        foto, nombre, detalles));

            } else {

                fila = (LinearLayout) convertView;
            }

            VistaContacto vistas = (VistaContacto) fila.getTag();
            Contactos c = filtrados.get(position);

            vistas.nombre.setText(c.getNombre() == null
                    ? "Sin nombre" : c.getNombre());

            String pais = c.getPais() == null ? "" : c.getPais();
            String telefono = c.getTelefono() == null
                    ? "" : c.getTelefono();

            vistas.detalles.setText(pais + "  " + telefono);

            byte[] imagen = c.getImagen();

            if (imagen != null && imagen.length > 0) {

                android.graphics.Bitmap bitmap =
                        BitmapFactory.decodeByteArray(
                                imagen, 0, imagen.length);

                if (bitmap != null) {
                    vistas.foto.setImageBitmap(bitmap);
                    vistas.foto.setBackground(
                            forma(Color.WHITE, 12));
                } else {
                    mostrarFotoVacia(vistas.foto);
                }

            } else {
                mostrarFotoVacia(vistas.foto);
            }

            if (position == seleccionado) {
                fila.setBackground(forma(azulSeleccion, 12));
            } else {
                fila.setBackground(forma(Color.WHITE, 12));
            }

            return fila;
        }
    }

    private static class VistaContacto {

        ImageView foto;
        TextView nombre;
        TextView detalles;

        VistaContacto(
                ImageView foto,
                TextView nombre,
                TextView detalles) {

            this.foto = foto;
            this.nombre = nombre;
            this.detalles = detalles;
        }
    }

    private void mostrarFotoVacia(ImageView foto) {
        foto.setImageDrawable(null);
        foto.setBackground(forma(0xFFE2ECF7, 12));
        foto.setImageResource(android.R.drawable.ic_menu_gallery);
        foto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        foto.setPadding(dp(12), dp(12), dp(12), dp(12));
    }

    private void aviso(String mensaje) {
        Toast.makeText(
                this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && buscador != null) {
            cargarContactos();
        }
    }
}

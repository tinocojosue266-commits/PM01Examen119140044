
package com.example.pm01examen119140044;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VerImagenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        byte[] imagen = getIntent().getByteArrayExtra("imagen");

        if (imagen == null || imagen.length == 0) {
            Toast.makeText(this, "No hay imagen guardada",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(0xFFF1F6FC);

        ImageView foto = new ImageView(this);
        foto.setImageBitmap(BitmapFactory.decodeByteArray(
                imagen, 0, imagen.length));
        foto.setAdjustViewBounds(true);
        foto.setScaleType(ImageView.ScaleType.FIT_CENTER);

        layout.addView(foto, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        setContentView(layout);
    }
}

package com.souzalima.fluxodeencomendas;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PhotoFullscreenActivity extends AppCompatActivity {

    private ImageView imageViewFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_fullscreen);

        imageViewFullscreen = findViewById(R.id.imageViewFullscreen);

        // Obtenha a URL da imagem passada pela Intent
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        // Carregue a imagem em tela cheia usando Glide (ou outra biblioteca)
        Glide.with(this)
                .load(imageUrl)
                .into(imageViewFullscreen);

        // Configurar clique na imagem para fechar a Activity
        imageViewFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fechar a Activity ao clicar na imagem
                finish();
            }
        });

        // Configurar a escala da imagem para ampliar em tela cheia
        imageViewFullscreen.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
}

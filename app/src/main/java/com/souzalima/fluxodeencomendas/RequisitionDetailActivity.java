package com.souzalima.fluxodeencomendas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequisitionDetailActivity extends AppCompatActivity {

    private TextView textViewUnit, textViewName, textViewDate, textViewTime, textViewDescription, textViewNF, textViewPlantonista, textViewStatus, textViewRecebedor, textViewHorarioEntrega;
    private ImageView imageViewPhoto;
    private Button buttonEntregue;

    private Order order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requisicoes_item);

        // Inicialização dos componentes da interface
        textViewUnit = findViewById(R.id.textViewUnit);
        textViewName = findViewById(R.id.textViewName);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewNF = findViewById(R.id.textViewNF);
        textViewPlantonista = findViewById(R.id.textViewPlantonista);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewRecebedor = findViewById(R.id.textViewRecebedor);
        textViewHorarioEntrega = findViewById(R.id.textViewHorarioEntrega);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        // Obtém a encomenda da intent
        Order order = (Order) getIntent().getSerializableExtra("order");

        // Verifica se a encomenda não é nula
        if (order != null) {
            // Define os textos nos TextViews com as informações da encomenda
            textViewUnit.setText("Unidade: " + order.getUnit());
            textViewName.setText("Destinatário: " + order.getName());
            textViewDate.setText("Data: " + order.getDate());
            textViewTime.setText("Hora: " + order.getTime());
            textViewDescription.setText("Descrição: " + order.getDescription());
            textViewNF.setText("Nota Fiscal: " + order.getNotaFiscal());
            textViewPlantonista.setText("Plantonista: " + order.getPlantonista());
            textViewStatus.setText("Status: " + order.getStatus());

            // Carregar a imagem usando Glide se houver uma URL da imagem
            if (order.getImageUrl() != null) {
                Glide.with(this)
                        .load(order.getImageUrl())
                        .placeholder(R.drawable.default_image) // Imagem padrão enquanto carrega
                        .error(R.drawable.default_image) // Imagem de erro, se houver problema no carregamento
                        .into(imageViewPhoto);
            } else {
                // Se não houver URL de imagem, pode definir uma imagem padrão
                imageViewPhoto.setImageResource(R.drawable.default_image);
            }

            // Configura o onClickListener para o botão "Entregue"
            findViewById(R.id.buttonEntregue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Altera o status para "Entregue" no objeto Order
                    order.setStatus("Entregue");

                    // Atualiza o Firebase Realtime Database
                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
                    ordersRef.child(order.getOrderId()).child("status").setValue("Entregue");

                    // Atualiza o textViewStatus e exibe um Toast
                    textViewStatus.setText("Status: " + order.getStatus());
                    textViewStatus.setTextColor(getResources().getColor(R.color.colorGreen)); // Define a cor verde
                    Toast.makeText(RequisitionDetailActivity.this, "Status alterado para Entregue", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

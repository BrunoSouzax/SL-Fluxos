package com.souzalima.fluxodeencomendas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequisitionsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RequisitionsAdapter requisitionsAdapter;
    private DatabaseReference database;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisitions);

        // Inicialização da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obter a ActionBar e configurar o botão de "voltar"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Exibir o botão de "voltar"
            actionBar.setDisplayShowHomeEnabled(true); // Exibir o ícone de "voltar"
        }

        // Configuração do DrawerLayout e NavigationView (opcional)
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Inicialização dos componentes da interface
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requisitionsAdapter = new RequisitionsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(requisitionsAdapter);

        database = FirebaseDatabase.getInstance().getReference().child("orders");

        // Recuperar dados do Firebase e atualizar o RecyclerView
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null && order.getStatus() != null && (order.getStatus().equals("requisitada") || order.getStatus().equals("pendente"))) {
                        orders.add(order);
                    }
                }
                requisitionsAdapter.setOrderList(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RequisitionsActivity.this, "Erro ao carregar as encomendas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Verificar se o item selecionado é o botão de "voltar"
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Voltar para a página anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.action_add) {
            startActivity(new Intent(RequisitionsActivity.this, CadastroActivity.class));
        } else if (id == R.id.action_consultar) {
            startActivity(new Intent(RequisitionsActivity.this, OrdersActivity.class));
        } else if (id == R.id.action_requisicoes) {
            // Já estamos na RequisitionsActivity
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

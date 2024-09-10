package com.souzalima.fluxodeencomendas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private CardView cardviewCadastro;
    private CardView cardviewConsulta;
    private CardView cardviewRequisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        cardviewCadastro = findViewById(R.id.cardviewCadastro);
        cardviewConsulta = findViewById(R.id.cardviewConsulta);
        cardviewRequisition = findViewById(R.id.cardviewRequisition);

        if (cardviewCadastro != null) {
            cardviewCadastro.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(intent);
            });
        }

        if (cardviewConsulta != null) {
            cardviewConsulta.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
                startActivity(intent);
            });
        }

        if (cardviewRequisition != null) {
            cardviewRequisition.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RequisitionsActivity.class);
                startActivity(intent);
            });
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.action_add) {
            // Navegar para a Activity de Adicionar Encomenda
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_consultar) {
            // Navegar para a Activity de Consultar Encomendas
            Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_requisicoes) {
            // Navegar para a Activity de Requisições
            Intent intent = new Intent(MainActivity.this, RequisitionsActivity.class);
            startActivity(intent);
        }

        // Fechar o Navigation Drawer após a seleção
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.souzalima.fluxodeencomendas;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private SearchView searchView;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        searchView = findViewById(R.id.searchView);
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Configura o listener do SearchView para capturar a unidade a ser pesquisada
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Aqui você pode executar a busca apenas quando o usuário submeter o texto
                searchOrdersByUnit(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Não fazer nada em cada mudança de texto
                return false;
            }
        });

        // Configuração para não expandir automaticamente e capturar a pesquisa ao clicar no ícone de pesquisa
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
    }

    private void searchOrdersByUnit(String unit) {
        // Limpa a lista atual de pedidos
        orderList.clear();

        // Consulta no Firebase para buscar pedidos com a unidade especificada
        Query query = ordersRef.orderByChild("unit").equalTo(unit);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged(); // Notifica o RecyclerView sobre as mudanças na lista
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchResultsActivity.this, "Erro ao buscar dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.souzalima.fluxodeencomendas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequisitionsAdapter extends RecyclerView.Adapter<RequisitionsAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;

    public RequisitionsAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_requisicao_resumido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textViewUnit.setText(order.getUnit());
        holder.textViewName.setText(order.getName());
        holder.textViewDate.setText(order.getDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequisitionDetailActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUnit, textViewName, textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUnit = itemView.findViewById(R.id.textViewUnit);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}

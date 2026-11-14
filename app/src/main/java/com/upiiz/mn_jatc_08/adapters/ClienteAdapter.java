package com.upiiz.mn_jatc_08.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upiiz.mn_jatc_08.R;
import com.upiiz.mn_jatc_08.models.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Cliente cliente);
    }

    private List<Cliente> listaClientes = new ArrayList<>();
    private final OnItemClickListener listener;

    public ClienteAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setClientes(List<Cliente> clientes) {
        this.listaClientes = clientes != null ? clientes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        holder.bind(cliente, listener);
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemId;
        private final TextView tvItemNombre;
        private final TextView tvItemCorreo;
        private final TextView tvItemTelefono;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvItemNombre = itemView.findViewById(R.id.tvItemNombre);
            tvItemCorreo = itemView.findViewById(R.id.tvItemCorreo);
            tvItemTelefono = itemView.findViewById(R.id.tvItemTelefono);
        }

        public void bind(Cliente cliente, OnItemClickListener listener) {
            tvItemId.setText("ID: " + (cliente.getId() != null ? cliente.getId() : "N/A"));
            tvItemNombre.setText(cliente.getNombre() != null ? cliente.getNombre() : "Sin nombre");
            tvItemCorreo.setText(cliente.getCorreo() != null ? cliente.getCorreo() : "Sin correo");
            tvItemTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "Sin teléfono");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(cliente);
                }
            });
        }
    }
}

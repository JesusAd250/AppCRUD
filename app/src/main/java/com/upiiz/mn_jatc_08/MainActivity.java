package com.upiiz.mn_jatc_08;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.upiiz.mn_jatc_08.adapters.ClienteAdapter;
import com.upiiz.mn_jatc_08.models.Cliente;
import com.upiiz.mn_jatc_08.models.RespuestaClientes;
import com.upiiz.mn_jatc_08.network.RetrofitClient;
import com.upiiz.mn_jatc_08.network.api.ApiCliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiCliente apiCliente;
    private TextView tvRespuestaApi;
    private ProgressBar progressBar;
    private RecyclerView rvClientes;
    private FloatingActionButton fabAgregar;

    private ClienteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvRespuestaApi = findViewById(R.id.tvRespuesta);
        progressBar = findViewById(R.id.progressBar);
        rvClientes = findViewById(R.id.rvClientes);
        fabAgregar = findViewById(R.id.fabAgregar);

        adapter = new ClienteAdapter(this::abrirDetalle);
        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        rvClientes.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioCliente.class);
            startActivity(intent);
        });

        apiCliente = RetrofitClient.obtenerApiCliente();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerClientes();
    }

    private void abrirDetalle(Cliente c) {
        Intent i = new Intent(this, Detalles_cliente.class);
        i.putExtra("id", c.getId() != null ? c.getId() : -1L);
        i.putExtra("nombre", c.getNombre());
        i.putExtra("correo", c.getCorreo());
        i.putExtra("telefono", c.getTelefono());
        i.putExtra("direccion", c.getDireccion());
        i.putExtra("registrado", c.isRegistrado());
        i.putExtra("links", c.getLinks() != null && !c.getLinks().isEmpty()
                ? c.getLinks().get(0).getHref() : "Sin links");
        startActivity(i);
    }

    private void obtenerClientes() {
        progressBar.setVisibility(View.VISIBLE);
        tvRespuestaApi.setVisibility(View.GONE);

        apiCliente.obtenerClientes().enqueue(new Callback<RespuestaClientes>() {
            @Override
            public void onResponse(Call<RespuestaClientes> call, Response<RespuestaClientes> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getDatos() != null &&
                            response.body().getDatos().getContent() != null &&
                            !response.body().getDatos().getContent().isEmpty()) {

                        List<Cliente> lista = response.body().getDatos().getContent();
                        adapter.setClientes(lista);
                        tvRespuestaApi.setVisibility(View.GONE);
                        rvClientes.setVisibility(View.VISIBLE);

                    } else {
                        tvRespuestaApi.setText("No hay datos de clientes");
                        tvRespuestaApi.setVisibility(View.VISIBLE);
                        rvClientes.setVisibility(View.GONE);
                    }
                } else {
                    tvRespuestaApi.setText("No existen clientes (" + response.code() + ")");
                    tvRespuestaApi.setVisibility(View.VISIBLE);
                    rvClientes.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "No existen clientes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaClientes> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvRespuestaApi.setText("Error de conexión: " + t.getMessage());
                tvRespuestaApi.setVisibility(View.VISIBLE);
                rvClientes.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

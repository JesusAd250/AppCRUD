package com.upiiz.mn_jatc_08;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.upiiz.mn_jatc_08.models.RespuestaEliminarCliente;
import com.upiiz.mn_jatc_08.network.ApiErrores;
import com.upiiz.mn_jatc_08.network.RetrofitClient;
import com.upiiz.mn_jatc_08.network.api.ApiCliente;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detalles_cliente extends AppCompatActivity {

    private long id;
    private boolean registrado;
    private String nombre, correo, telefono, direccion;
    private ApiCliente apiCliente;

    // Si se editó con éxito, se cierra el detalle y se vuelve a la lista (que recarga en onResume)
    private final ActivityResultLauncher<Intent> editarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) finish();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detalle), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(s.left, s.top, s.right, s.bottom);
            return insets;
        });

        apiCliente = RetrofitClient.obtenerApiCliente();

        Intent in = getIntent();
        id = in.getLongExtra("id", -1L);
        nombre = in.getStringExtra("nombre");
        correo = in.getStringExtra("correo");
        telefono = in.getStringExtra("telefono");
        direccion = in.getStringExtra("direccion");
        registrado = in.getBooleanExtra("registrado", false);

        ((TextView) findViewById(R.id.tvDetalleId)).setText(String.valueOf(id));
        ((TextView) findViewById(R.id.tvDetalleNombre)).setText(nombre);
        ((TextView) findViewById(R.id.tvDetalleCorreo)).setText(correo);
        ((TextView) findViewById(R.id.tvDetalleTelefono)).setText(telefono);
        ((TextView) findViewById(R.id.tvDetalleDireccion)).setText(direccion);
        ((TextView) findViewById(R.id.tvDetalleRegistrado)).setText(registrado ? "Sí" : "No");
        ((TextView) findViewById(R.id.tvDetalleLinks)).setText(in.getStringExtra("links"));

        findViewById(R.id.btnEditar).setOnClickListener(v -> {
            Intent i = new Intent(this, FormularioCliente.class);
            i.putExtra("id", id);
            i.putExtra("nombre", nombre);
            i.putExtra("correo", correo);
            i.putExtra("telefono", telefono);
            i.putExtra("direccion", direccion);
            i.putExtra("registrado", registrado);
            editarLauncher.launch(i);
        });

        findViewById(R.id.btnEliminar).setOnClickListener(v -> confirmarEliminar());
    }

    private void confirmarEliminar() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar cliente")
                .setMessage("¿Seguro que deseas eliminar a " + nombre + "? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> eliminar())
                .show();
    }

    private void eliminar() {
        apiCliente.eliminarCliente(id).enqueue(new Callback<RespuestaEliminarCliente>() {
            @Override
            public void onResponse(Call<RespuestaEliminarCliente> call, Response<RespuestaEliminarCliente> response) {
                if (response.isSuccessful()) {
                    String msg = "Cliente eliminado correctamente";
                    if (response.body() != null && response.body().getMensaje() != null) {
                        msg = response.body().getMensaje();
                    }
                    Toast.makeText(Detalles_cliente.this, msg, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(Detalles_cliente.this,
                            ApiErrores.mensajeHttp(response.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaEliminarCliente> call, Throwable t) {
                Toast.makeText(Detalles_cliente.this,
                        ApiErrores.mensajeFallo(t), Toast.LENGTH_LONG).show();
            }
        });
    }
}

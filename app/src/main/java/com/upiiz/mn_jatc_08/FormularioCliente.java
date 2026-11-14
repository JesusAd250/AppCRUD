package com.upiiz.mn_jatc_08;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.upiiz.mn_jatc_08.models.PeticionCliente;
import com.upiiz.mn_jatc_08.models.RespuestaCliente;
import com.upiiz.mn_jatc_08.network.ApiErrores;
import com.upiiz.mn_jatc_08.network.RetrofitClient;
import com.upiiz.mn_jatc_08.network.api.ApiCliente;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioCliente extends AppCompatActivity {

    private TextInputLayout tilNombre, tilCorreo, tilTelefono, tilDireccion;
    private TextInputEditText etNombre, etCorreo, etTelefono, etDireccion;
    private MaterialButton btnGuardar;
    private ProgressBar progress;
    private ApiCliente apiCliente;

    private long idEditar = -1L;
    private boolean registrado = true; // valor por defecto al crear (verificar en Swagger)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.formulario), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(s.left, s.top, s.right, s.bottom);
            return insets;
        });

        apiCliente = RetrofitClient.obtenerApiCliente();

        tilNombre = findViewById(R.id.tilNombre);
        tilCorreo = findViewById(R.id.tilCorreo);
        tilTelefono = findViewById(R.id.tilTelefono);
        tilDireccion = findViewById(R.id.tilDireccion);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btnGuardar = findViewById(R.id.btnGuardar);
        progress = findViewById(R.id.progressFormulario);

        // Si llega un id, es modo edición: se precargan los datos
        idEditar = getIntent().getLongExtra("id", -1L);
        if (idEditar != -1L) {
            ((TextView) findViewById(R.id.tvTituloFormulario)).setText("Editar cliente");
            etNombre.setText(getIntent().getStringExtra("nombre"));
            etCorreo.setText(getIntent().getStringExtra("correo"));
            etTelefono.setText(getIntent().getStringExtra("telefono"));
            etDireccion.setText(getIntent().getStringExtra("direccion"));
            registrado = getIntent().getBooleanExtra("registrado", true);
        }

        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());

        configurarLimpiadoresDeError();
    }

    private void configurarLimpiadoresDeError() {
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { tilNombre.setError(null); }
            @Override public void afterTextChanged(Editable s) {}
        });
        etCorreo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { tilCorreo.setError(null); }
            @Override public void afterTextChanged(Editable s) {}
        });
        etTelefono.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { tilTelefono.setError(null); }
            @Override public void afterTextChanged(Editable s) {}
        });
        etDireccion.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { tilDireccion.setError(null); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private String texto(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean validar() {
        boolean ok = true;
        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilTelefono.setError(null);
        tilDireccion.setError(null);

        if (texto(etNombre).isEmpty()) {
            tilNombre.setError("El nombre es obligatorio");
            ok = false;
        }

        String correo = texto(etCorreo);
        if (correo.isEmpty()) {
            tilCorreo.setError("El correo es obligatorio");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError("Formato de correo inválido (ej. usuario@dominio.com)");
            ok = false;
        }

        String tel = texto(etTelefono);
        if (tel.isEmpty()) {
            tilTelefono.setError("El teléfono es obligatorio");
            ok = false;
        } else if (!tel.matches("^[0-9]{7,15}$")) {
            tilTelefono.setError("El teléfono debe contener solo números (de 7 a 15 dígitos)");
            ok = false;
        }

        if (texto(etDireccion).isEmpty()) {
            tilDireccion.setError("La dirección es obligatoria");
            ok = false;
        }

        if (!ok) {
            Toast.makeText(this, "Por favor completa los campos obligatorios marcados en rojo", Toast.LENGTH_SHORT).show();
        }

        return ok;
    }

    private void guardar() {
        if (!validar()) return;

        PeticionCliente peticion = new PeticionCliente();
        // En edición se manda el id; al crear queda null y Gson lo omite
        if (idEditar != -1L) peticion.setId(idEditar);
        peticion.setNombre(texto(etNombre));
        peticion.setCorreo(texto(etCorreo));
        peticion.setTelefono(texto(etTelefono));
        peticion.setDireccion(texto(etDireccion));
        peticion.setRegistrado(registrado);

        setCargando(true);

        Call<RespuestaCliente> call = (idEditar == -1L)
                ? apiCliente.crearCliente(peticion)
                : apiCliente.actualizarCliente(idEditar, peticion);

        call.enqueue(new Callback<RespuestaCliente>() {
            @Override
            public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                setCargando(false);
                if (response.isSuccessful()) {
                    String msg = (idEditar == -1L)
                            ? "Cliente registrado correctamente"
                            : "Cliente actualizado correctamente";
                    Toast.makeText(FormularioCliente.this, msg, Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(FormularioCliente.this,
                            ApiErrores.mensajeHttp(response.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                setCargando(false);
                Toast.makeText(FormularioCliente.this,
                        ApiErrores.mensajeFallo(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCargando(boolean cargando) {
        progress.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!cargando);
    }
}

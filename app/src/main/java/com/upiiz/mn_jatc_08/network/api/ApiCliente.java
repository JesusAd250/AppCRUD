package com.upiiz.mn_jatc_08.network.api;

import com.upiiz.mn_jatc_08.models.PeticionCliente;
import com.upiiz.mn_jatc_08.models.RespuestaCliente;
import com.upiiz.mn_jatc_08.models.RespuestaClientes;
import com.upiiz.mn_jatc_08.models.RespuestaEliminarCliente;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCliente {
    //Solicita todos los clientes
    @GET("api/v1/clientes")
    Call<RespuestaClientes> obtenerClientes();

    //Solicita un cliente
    @GET("api/v1/clientes/{id}")
    Call<RespuestaCliente> obtenerClientePorId(@Path("id") Long id);

    //Crea un nuevo recurso (Cliente)
    @POST("api/v1/clientes")
    Call<RespuestaCliente> crearCliente(@Body PeticionCliente cliente);

    //Actualiza al cliente
    @PUT("api/v1/clientes/{id}")
    Call<RespuestaCliente> actualizarCliente(@Path("id") Long id, @Body PeticionCliente clienteActualizar);

    //Eliminar cliente por id
    @DELETE("api/v1/clientes/{id}")
    Call<RespuestaEliminarCliente> eliminarCliente(@Path("id") Long id);
}

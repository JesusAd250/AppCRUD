package com.upiiz.mn_jatc_08.network;

import com.upiiz.mn_jatc_08.network.api.ApiCliente;
import com.upiiz.mn_jatc_08.network.api.ApiProductos;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //Se comunica con la API - CLientes
    private static final String BASE_URL = "https://fw-ejercicio-08.onrender.com/";
    private static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static ApiCliente obtenerApiCliente(){
        return retrofit.create(ApiCliente.class);
    }

    public static ApiProductos obtenerApiProductos(){
        return retrofit.create(ApiProductos.class);
    }
}

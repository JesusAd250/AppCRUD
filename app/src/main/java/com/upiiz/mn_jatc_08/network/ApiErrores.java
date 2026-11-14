package com.upiiz.mn_jatc_08.network;

import java.io.IOException;

public class ApiErrores {

    public static String mensajeHttp(int code) {
        switch (code) {
            case 400:
                return "Error 400: Solicitud incorrecta o datos inválidos";
            case 401:
                return "Error 401: No autorizado";
            case 403:
                return "Error 403: Acceso prohibido";
            case 404:
                return "Error 404: Recurso no encontrado";
            case 500:
                return "Error 500: Error interno del servidor";
            default:
                return "Error en la petición (Código: " + code + ")";
        }
    }

    public static String mensajeFallo(Throwable t) {
        if (t instanceof IOException) {
            return "Error de red: Verifica tu conexión a internet";
        }
        return "Error inesperado: " + (t != null ? t.getMessage() : "Desconocido");
    }
}

package com.example.projetobiomedicina;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.0.40:8080/";

    private OkHttpClient client;

    public ApiClient() {
        client = new OkHttpClient();
    }

    public void getData(String endpoint, Callback callback) {
        String url = BASE_URL + endpoint;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void postData(String endpoint, String jsonBody, Callback callback) {
        String url = BASE_URL + endpoint;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void login(String jsonBody, Callback callback) {
        String url = BASE_URL + "auth/login";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getUser(String login, Callback callback) {
        String url = BASE_URL + "user/login/" + login;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getPacientePorCpf(Long cpf, Callback callback) {
        String url = BASE_URL + "paciente/" + cpf;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }
    public void updateVisita(long idAgenda, String visita, Callback callback) {
        String url = BASE_URL + "agenda/" + idAgenda + "/visita?visita=" + visita;
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(null, new byte[0])) // Corpo vazio para a requisição PUT
                .build();

        client.newCall(request).enqueue(callback);
    }
    public void getAgendamentosPorLoginData(String login, Callback callback) {
        String url = BASE_URL + "agenda/byLoginVisitaData?login=" + login;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }
}

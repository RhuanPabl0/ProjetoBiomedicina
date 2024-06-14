package com.example.projetobiomedicina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    performLogin(username, password);
                } else {
                    Log.e(TAG, "Login failed");
                }
            }
        });
    }

    private void performLogin(String username, String password) {

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("login", username);
        jsonBody.addProperty("password", password);

        String requestBody = new Gson().toJson(jsonBody);

        ApiClient apiClient = new ApiClient();
        apiClient.login(requestBody, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Login failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String token = jsonResponse.getString("token");

                        saveUserCredentials(username, token);

                        // Buscar o nome do usuário
                        apiClient.getUser(username, new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e(TAG, "Erro ao buscar o usuário", e);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String userResponseBody = response.body().string();
                                    try {
                                        JSONObject userJsonResponse = new JSONObject(userResponseBody);
                                        String nome = userJsonResponse.getString("nome");

                                        // Iniciar a Activity Dashboard
                                        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                                        startActivity(intent);
                                        finish(); // Finalizar a LoginActivity
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Erro ao analisar a resposta do servidor", e);
                                    }
                                } else {
                                    Log.e(TAG, "Erro ao buscar o usuário: " + response.message());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "Erro ao analisar a resposta do servidor", e);
                    }
                } else {
                    Log.e(TAG, "Erro no login: " + response.message());
                }
            }
        });
    }

    private void saveUserCredentials(String login, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LOGIN", login);
        editor.putString("TOKEN", token);
        editor.apply();
    }
}

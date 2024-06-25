package com.example.projetobiomedicina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashBoardActivity extends AppCompatActivity {



    private static final String TAG = "DashBoardActivity";
    Button btnVisita, btnRelatorio,btnColeta;
    Button btnSair, btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        btnSair = findViewById(R.id.txtBtnSair);
        btnVoltar = findViewById(R.id.txtBtnVoltar);
        btnVisita = findViewById(R.id.visitaAgendada); // Botão para navegar para VisitaActivity
        btnRelatorio = findViewById(R.id.relatorio);
        btnColeta = findViewById(R.id.listaColetada);

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUserCredentials();
                Intent logoutIntent = new Intent(DashBoardActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voltarIntent = new Intent(DashBoardActivity.this, LoginActivity.class);
                startActivity(voltarIntent);
            }
        });

        btnRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent relatorioIntent = new Intent(DashBoardActivity.this, RelatorioActivity.class);
                startActivity(relatorioIntent);
            }
        });

        // Configurar o botão de visita agendada para abrir a VisitaActivity
        btnVisita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent visitaIntent = new Intent(DashBoardActivity.this, VisitaActivity.class);
                startActivity(visitaIntent);
            }
        });

        btnColeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coletaIntent = new Intent(DashBoardActivity.this, ColetaActivity.class);
                startActivity(coletaIntent);
            }
        });


        // Recuperar as credenciais do usuário
        String login = getUserLogin();

        if (login != null) {
            fetchUserDetails(login);
        }
    }

    private String getUserLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LOGIN", null);
    }

    private void clearUserCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("LOGIN");
        editor.apply();
    }

    private void fetchUserDetails(String login) {
        ApiClient apiClient = new ApiClient();
        apiClient.getUser(login, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Erro ao buscar detalhes do usuário", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String nome = jsonResponse.getString("nome");
                        String cons = jsonResponse.getString("cons");

                        runOnUiThread(() -> {
                            TextView txtViewNome = findViewById(R.id.txtViewNome);
                            TextView txtViewCons = findViewById(R.id.consTextView);

                            txtViewNome.setText(nome);
                            txtViewCons.setText( "CRM: " +cons);

                            // Salvar detalhes do usuário no SharedPreferences
                            saveUserDetails(nome, cons);
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "Erro ao analisar a resposta do servidor", e);
                    }
                } else {
                    Log.e(TAG, "Erro ao buscar detalhes do usuário: " + response.message());
                }
            }
        });
    }

    private void saveUserDetails(String nome, String cons) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NOME", nome);
        editor.putString("CONS", cons);
        editor.apply();
    }
}

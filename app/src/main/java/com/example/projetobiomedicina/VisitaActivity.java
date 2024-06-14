package com.example.projetobiomedicina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VisitaActivity extends AppCompatActivity {
    private static final String TAG = "VisitaActivity";
    Button btnSair, btnVoltar;
    TextView agendamentosTextView;
    String login, nome, cons;
    RecyclerView recyclerViewAgendamentos;
    AgendamentoAdapter agendamentoAdapter;
    List<JSONObject> agendamentosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visita);

        // Recuperar as credenciais do usuário
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        login = sharedPreferences.getString("LOGIN", null);
        nome = sharedPreferences.getString("NOME", null);
        cons = sharedPreferences.getString("CONS", null);

        TextView nomeTextView = findViewById(R.id.nomeTextView);
        TextView loginTextView = findViewById(R.id.loginTextView);
        TextView consTextView = findViewById(R.id.consTextView);

        nomeTextView.setText("Nome: " + nome);
        loginTextView.setText("Codigo: " + login);
        consTextView.setText("CRM: " + cons);

        btnSair = findViewById(R.id.txtBtnSair);
        btnVoltar = findViewById(R.id.txtBtnVoltar);

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(VisitaActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voltarIntent = new Intent(VisitaActivity.this, DashBoardActivity.class);
                startActivity(voltarIntent);
            }
        });

        recyclerViewAgendamentos = findViewById(R.id.recyclerViewAgendamentos);
        recyclerViewAgendamentos.setLayoutManager(new LinearLayoutManager(this));
        agendamentoAdapter = new AgendamentoAdapter(this, agendamentosList);
        recyclerViewAgendamentos.setAdapter(agendamentoAdapter);

        buscarAgendamentos(login);
    }

    private void buscarAgendamentos(String login) {
        ApiClient apiClient = new ApiClient();
        apiClient.getData("agenda/byLoginAndVisita?login=" + login + "&visita=sim", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> agendamentosTextView.setText("Erro ao buscar os agendamentos."));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseData);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject pacienteObject = jsonObject.getJSONObject("paciente");
                                JSONObject procedimentoObject = jsonObject.getJSONObject("procedimentos");
                                JSONObject agendamento = new JSONObject();
                                agendamento.put("cpfpac", pacienteObject.getLong("cpfpac"));
                                agendamento.put("nomepac", pacienteObject.getString("nomepac"));
                                agendamento.put("dataConsulta", jsonObject.getString("dataConsulta"));
                                agendamento.put("telpac", pacienteObject.getLong("telpac"));
                                agendamento.put("ceppac", pacienteObject.getLong("ceppac"));
                                agendamento.put("lograpac", pacienteObject.getString("lograpac"));
                                agendamento.put("numlograpac", pacienteObject.getInt("numlograpac"));
                                agendamento.put("complpac", pacienteObject.getString("complpac"));
                                agendamento.put("bairropac", pacienteObject.getString("bairropac"));
                                agendamento.put("descProced", procedimentoObject.getString("descProced"));
                                agendamentosList.add(agendamento);
                            }
                            agendamentoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        agendamentosTextView.setText("Erro ao buscar os agendamentos.");
                    });
                }
            }
        });
    }
}

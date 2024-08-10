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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        // Inicialmente limpar a lista para evitar duplicações

    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarAgendamentos();
    }

    private void atualizarAgendamentos() {
        agendamentosList.clear();
        buscarAgendamentos(login);
    }

    private void buscarAgendamentos(String login) {
        ApiClient apiClient = new ApiClient();
        apiClient.getAgendamentosPorLoginData(login, new Callback() {
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
                            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            JSONArray jsonArray = new JSONArray(responseData);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject pacienteObject = jsonObject.getJSONObject("paciente");
                                JSONObject procedimentoObject = jsonObject.optJSONObject("procedimentos");
                                JSONObject userObject = jsonObject.getJSONObject("user");
                                JSONObject agendamento = new JSONObject();

                                String dataConsultaOriginal = jsonObject.getString("dataConsulta");
                                String idAgenda = jsonObject.getString("id");

                                Date date = inputDateFormat.parse(dataConsultaOriginal);
                                String dataConsultaFormatada = outputDateFormat.format(date);

                                agendamento.put("idAgenda", idAgenda);
                                agendamento.put("cpfpac", pacienteObject.getLong("cpfpac"));
                                agendamento.put("nomepac", pacienteObject.getString("nomepac"));
                                agendamento.put("dataConsulta", dataConsultaFormatada);
                                agendamento.put("telpac", pacienteObject.getLong("telpac"));
                                agendamento.put("ceppac", pacienteObject.getLong("ceppac"));
                                agendamento.put("lograpac", pacienteObject.getString("lograpac"));
                                agendamento.put("numlograpac", pacienteObject.getInt("numlograpac"));
                                agendamento.put("complpac", pacienteObject.getString("complpac"));
                                agendamento.put("bairropac", pacienteObject.getString("bairropac"));

                                // Adiciona informações do procedimento, se existir
                                if (procedimentoObject != null) {
                                    agendamento.put("descProced", procedimentoObject.getString("descProced"));
                                    agendamento.put("codProced", procedimentoObject.getString("codProced"));
                                } else {
                                    agendamento.put("descProced", "");
                                    agendamento.put("codProced", "");
                                }

                                agendamento.put("nome", userObject.getString("nome"));
                                agendamentosList.add(agendamento);
                            }
                            agendamentoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
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

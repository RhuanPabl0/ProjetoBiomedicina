package com.example.projetobiomedicina;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ColetaActivity extends AppCompatActivity {

    private ApiClient apiClient;
    private TextView textViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta);

        apiClient = new ApiClient();
        textViewData = findViewById(R.id.textViewData);
        Button buttonGetData = findViewById(R.id.buttonGetData);

        buttonGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
    }
    private void fetchData() {
        apiClient.getData("exameseamostras", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewData.setText("Failed to load data: " + e.getMessage());
                    }
                });
                Log.e("ColetaActivity", "Error fetching data", e);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ExameAmostra>>(){}.getType();
                    List<ExameAmostra> dataList = gson.fromJson(responseData, listType);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewData.setText(formatData(dataList));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewData.setText("Failed to load data: " + response.message());
                        }
                    });
                    Log.e("ColetaActivity", "Response error: " + response.message());
                }
            }
        });
    }
    private String formatData(List<ExameAmostra> dataList) {
        StringBuilder formattedData = new StringBuilder();
        for (ExameAmostra item : dataList) {
            formattedData.append("ID: ").append(item.getId()).append("\n")
                    .append("Nome do Exame: ").append(item.getNomeexame()).append("\n")
                    .append("Tipo: ").append(item.isTipo() ? "Tipo 1" : "Tipo 2").append("\n")
                    .append("Data Realizado: ").append(item.getDtrealizado()).append("\n")
                    .append("ID Cliente: ").append(item.getIdcliente()).append("\n")
                    .append("ID Informação Referência: ").append(item.getIdinforeferencia()).append("\n")
                    .append("Data e Hora da Coleta: ").append(item.getDataHoraColeta()).append("\n")
                    .append("Nome do Profissional: ").append(item.getNomeProfissional()).append("\n")
                    .append("Número de Amostras: ").append(item.getNumAmostras()).append("\n")
                    .append("Condições de Coleta: ").append(item.getCondicoesColeta()).append("\n")
                    .append("Identificação dos Tubos: ").append(item.getIdentificacaoTubos()).append("\n")
                    .append("Tempo de Armazenamento: ").append(item.getTempoArmazenamento()).append("\n")
                    .append("Condições de Transporte: ").append(item.getCondicoesTransporte()).append("\n")
                    .append("Observações: ").append(item.getObservacoes()).append("\n")
                    .append("Reações Adversas: ").append(item.getReacoesAdversas()).append("\n")
                    .append("Acompanhamento Adicional: ").append(item.getAcompanhamentoAdicional()).append("\n")
                    .append("--------------------------------------------------------------------------\n\n");
        }
        return formattedData.toString();
    }
}

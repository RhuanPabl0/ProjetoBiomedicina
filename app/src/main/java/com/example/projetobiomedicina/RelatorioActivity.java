package com.example.projetobiomedicina;




import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RelatorioActivity extends AppCompatActivity {

    Button getCpf;
    EditText campoBusca;

    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_relatorio);

        getCpf = findViewById(R.id.btnBuscar);
        campoBusca = findViewById(R.id.campoBusca);

        apiClient = new ApiClient();

        getCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpfText = campoBusca.getText().toString().trim();
                if (!cpfText.isEmpty()) {
                    Long cpf = Long.parseLong(cpfText);
                    buscarPaciente(cpf);
                } else {
                    Toast.makeText(RelatorioActivity.this, "Por favor, insira um CPF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void buscarPaciente(Long cpf){
        apiClient.getPacientePorCpf(cpf, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RelatorioActivity.this, "Erro ao buscar paciente", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("ApiError", "Erro ao buscar paciente: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RelatorioActivity.this, "Paciente não encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("ApiError", "Erro na resposta: " + response.message());
            }
        });
    }
}
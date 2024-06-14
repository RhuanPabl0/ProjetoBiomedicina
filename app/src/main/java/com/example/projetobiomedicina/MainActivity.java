package com.example.projetobiomedicina;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button buttonGetData;
    private TextView textViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonGetData = findViewById(R.id.buttonGetData);
        textViewData = findViewById(R.id.textViewData);

        buttonGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
    }
    private void fetchData() {
        ApiClient apiClient = new ApiClient();
        apiClient.getData("paciente", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erro na requisição: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Paciente>>() {}.getType();
                            List<Paciente> pacientes = gson.fromJson(responseData, listType);
                            displayData(pacientes);
                        }
                    });
                } else {
                    Log.e(TAG, "Erro na resposta: " + response.message());
                }
            }
        });
    }
    private void displayData(List<Paciente> pacientes) {
        StringBuilder sb = new StringBuilder();
        for (Paciente paciente : pacientes) {
            sb.append(paciente.toString()).append("\n\n");
        }
        textViewData.setText(sb.toString());
    }
}
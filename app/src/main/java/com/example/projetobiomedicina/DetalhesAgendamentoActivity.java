package com.example.projetobiomedicina;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DetalhesAgendamentoActivity extends AppCompatActivity {
    TextView detalhesTextView;
    EditText editTextNomeExame, editTextTipo, editTextIdCliente, editTextIdInfoReferencia;
    EditText editTextNomeProfissional, editTextNumAmostras, editTextCondicoesColeta;
    EditText editTextIdentificacaoTubos, editTextTempoArmazenamento, editTextCondicoesTransporte, editTextObservacoes;
    EditText editTextReacoesAdversas, editTextAcompanhamentoAdicional;
    Button buttonEnviar;

    TimeZone timeZone = TimeZone.getTimeZone("America/Cuiaba");
    Calendar calendar = Calendar.getInstance(timeZone);
    Date dataHoraAtual = calendar.getTime();

    SimpleDateFormat saveDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    SimpleDateFormat exibirDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    String dataHoraFormatada = saveDateFormat.format(dataHoraAtual);
    String dataFormatada = exibirDateFormat.format(dataHoraAtual);

    private long idAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_agendamento);

        detalhesTextView = findViewById(R.id.detalhesTextView);
        editTextNomeExame = findViewById(R.id.et_nome_exame);
        editTextTipo = findViewById(R.id.editTextTipo);
        editTextIdCliente = findViewById(R.id.et_id_cliente);
        editTextIdInfoReferencia = findViewById(R.id.et_id_info_referencia);
        editTextNomeProfissional = findViewById(R.id.et_nome_profissional);
        editTextNumAmostras = findViewById(R.id.et_num_amostras);
        editTextCondicoesColeta = findViewById(R.id.et_condicoes_coleta);
        editTextIdentificacaoTubos = findViewById(R.id.et_identificacao_tubos);
        editTextTempoArmazenamento = findViewById(R.id.et_tempo_armazenamento);
        editTextCondicoesTransporte = findViewById(R.id.et_condicoes_transporte);
        editTextObservacoes = findViewById(R.id.et_observacoes);
        editTextReacoesAdversas = findViewById(R.id.et_reacoes_adversas);
        editTextAcompanhamentoAdicional = findViewById(R.id.et_acompanhamento_adicional);
        buttonEnviar = findViewById(R.id.btn_salvar);

        String agendamentoStr = getIntent().getStringExtra("agendamento");
        try {
            JSONObject agendamento = new JSONObject(agendamentoStr);
            String detalhes = "CPF: " + agendamento.optString("cpfpac") + "\n"
                    + "Nome: " + agendamento.optString("nomepac") + "\n"
                    + "Data: " + agendamento.optString("dataConsulta") + "\n"
                    + "Telefone: " + agendamento.optString("telpac") + "\n"
                    + "CEP: " + agendamento.optString("ceppac") + "\n"
                    + "Logradouro: " + agendamento.optString("lograpac") + "\n"
                    + "Número: " + agendamento.optString("numlograpac") + "\n"
                    + "Complemento: " + agendamento.optString("complpac") + "\n"
                    + "Bairro: " + agendamento.optString("bairropac") + "\n"
                    + "Profissional: " + agendamento.optString("nome") + "\n"
//                    + "codProced: " + agendamento.optString("codProced") + "\n"
                    + "Procedimento: " + agendamento.optString("descProced");
            detalhesTextView.setText(detalhes);

            // Preencher campos de inserção com dados iniciais (se necessário)
            editTextIdCliente.setText(agendamento.optString("cpfpac"));
            editTextNomeExame.setText(agendamento.optString("descProced"));
            editTextNomeProfissional.setText(agendamento.optString("nome"));
            editTextIdInfoReferencia.setText(agendamento.optString("codProced"));

            // Armazenar idAgenda para a atualização de visita
            idAgenda = agendamento.optLong("idAgenda");

        } catch (JSONException e) {
            e.printStackTrace();
            detalhesTextView.setText("Erro ao carregar os detalhes.");
        }
        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDados();
            }
        });
    }

    private void enviarDados() {
        String mensagemRevisao = montarMensagemRevisao();
        new AlertDialog.Builder(DetalhesAgendamentoActivity.this)
                .setTitle("Revisar Dados")
                .setMessage(mensagemRevisao)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Chamar o método para realmente enviar os dados
                        realmenteEnviarDados();
                    }
                })
                .setNegativeButton("Voltar", null)
                .show();
    }

    private void realmenteEnviarDados() {
        String nomeExame = editTextNomeExame.getText().toString();
        long idCliente = Long.parseLong(editTextIdCliente.getText().toString());
        long idInfoReferencia = Long.parseLong(editTextIdInfoReferencia.getText().toString());
        String nomeProfissional = editTextNomeProfissional.getText().toString();
        boolean tipo = Boolean.parseBoolean(editTextTipo.getText().toString());
        String dataHoraColeta = dataHoraFormatada;
        int numAmostras = Integer.parseInt(editTextNumAmostras.getText().toString());
        String condicoesColeta = editTextCondicoesColeta.getText().toString();
        String identificacaoTubos = editTextIdentificacaoTubos.getText().toString();
        String tempoArmazenamento = editTextTempoArmazenamento.getText().toString();
        String condicoesTransporte = editTextCondicoesTransporte.getText().toString();
        String observacoes = editTextObservacoes.getText().toString();
        String reacoesAdversas = editTextReacoesAdversas.getText().toString();
        String acompanhamentoAdicional = editTextAcompanhamentoAdicional.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nomeexame", nomeExame);
            jsonObject.put("tipo", tipo);
            jsonObject.put("idcliente", idCliente);
            jsonObject.put("idinforeferencia", idInfoReferencia);
            jsonObject.put("dataHoraColeta", dataHoraFormatada);
            jsonObject.put("nomeProfissional", nomeProfissional);
            jsonObject.put("numAmostras", numAmostras);
            jsonObject.put("condicoesColeta", condicoesColeta);
            jsonObject.put("identificacaoTubos", identificacaoTubos);
            jsonObject.put("tempoArmazenamento", tempoArmazenamento);
            jsonObject.put("condicoesTransporte", condicoesTransporte);
            jsonObject.put("observacoes", observacoes);
            jsonObject.put("reacoesAdversas", reacoesAdversas);
            jsonObject.put("acompanhamentoAdicional", acompanhamentoAdicional);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonBody = jsonObject.toString();
        ApiClient apiClient = new ApiClient();

        apiClient.postData("exameseamostras", jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetalhesAgendamentoActivity.this, "Erro ao enviar dados", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVisitaStatus();
                        Toast.makeText(DetalhesAgendamentoActivity.this, "Dados enviados com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateVisitaStatus() {
        ApiClient apiClient = new ApiClient();
        apiClient.updateVisita(idAgenda, "nao", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetalhesAgendamentoActivity.this, "Erro ao atualizar visita", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toast.makeText(DetalhesAgendamentoActivity.this, "Visita atualizada com sucesso!", Toast.LENGTH_SHORT).show();

                            Intent VisitaIntent = new Intent(DetalhesAgendamentoActivity.this, VisitaActivity.class);
                            startActivity(VisitaIntent);
                        } else {
                            Toast.makeText(DetalhesAgendamentoActivity.this, "Erro ao atualizar visita", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private String montarMensagemRevisao() {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Nome do Exame: ").append(editTextNomeExame.getText().toString()).append("\n");
        mensagem.append("Tipo: ").append(editTextTipo.getText().toString()).append("\n");
        mensagem.append("ID Cliente: ").append(editTextIdCliente.getText().toString()).append("\n");
//        mensagem.append("ID Info Referência: ").append(editTextIdInfoReferencia.getText().toString()).append("\n");
        mensagem.append("Nome do Profissional: ").append(editTextNomeProfissional.getText().toString()).append("\n");
        mensagem.append("Data e Hora da Coleta: ").append(dataFormatada).append("\n");
        mensagem.append("Número de Amostras: ").append(editTextNumAmostras.getText().toString()).append("\n");
        mensagem.append("Condições de Coleta: ").append(editTextCondicoesColeta.getText().toString()).append("\n");
        mensagem.append("Identificação dos Tubos: ").append(editTextIdentificacaoTubos.getText().toString()).append("\n");
        mensagem.append("Tempo de Armazenamento: ").append(editTextTempoArmazenamento.getText().toString()).append("\n");
        mensagem.append("Condições de Transporte: ").append(editTextCondicoesTransporte.getText().toString()).append("\n");
        mensagem.append("Observações: ").append(editTextObservacoes.getText().toString()).append("\n");
        mensagem.append("Reações Adversas: ").append(editTextReacoesAdversas.getText().toString()).append("\n");
        mensagem.append("Acompanhamento Adicional: ").append(editTextAcompanhamentoAdicional.getText().toString());
        return mensagem.toString();
    }
}

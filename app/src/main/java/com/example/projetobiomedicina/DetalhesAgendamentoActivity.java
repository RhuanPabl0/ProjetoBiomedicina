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


public class DetalhesAgendamentoActivity extends AppCompatActivity {
    TextView detalhesTextView;
    EditText editTextNomeExame, editTextTipo,editTextIdCliente, editTextIdInfoReferencia;
    EditText editTextNomeProfissional, editTextNumAmostras, editTextCondicoesColeta;
    EditText editTextIdentificacaoTubos, editTextTempoArmazenamento, editTextCondicoesTransporte, editTextObservacoes;
    EditText editTextReacoesAdversas, editTextAcompanhamentoAdicional;
    Button buttonEnviar;

    Calendar calendar = Calendar.getInstance();
    Date dataHoraAtual = calendar.getTime();

    SimpleDateFormat saveDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    SimpleDateFormat exibirDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    String dataHoraFormatada = saveDateFormat.format(dataHoraAtual);
    String dataFormatada = exibirDateFormat.format(dataHoraAtual);


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
                    + "codProced: " + agendamento.optString("codProced") + "\n"
                    + "Procedimento: " + agendamento.optString("descProced");
            detalhesTextView.setText(detalhes);

            // Preencher campos de inserção com dados iniciais (se necessário)
            editTextIdCliente.setText(agendamento.optString("cpfpac"));
            editTextNomeExame.setText(agendamento.optString("descProced"));
            editTextNomeProfissional.setText(agendamento.optString("nome"));
            editTextIdInfoReferencia.setText(agendamento.optString("codProced"));

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
                runOnUiThread(() -> Toast.makeText(DetalhesAgendamentoActivity.this, "Falha ao enviar dados", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        mostrarPopup();
                    } else {
                        Toast.makeText(DetalhesAgendamentoActivity.this, "Erro ao enviar dados", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String montarMensagemRevisao() {
        String nomeExame = editTextNomeExame.getText().toString();
        boolean tipo = Boolean.parseBoolean(editTextTipo.getText().toString());
        long idCliente = Long.parseLong(editTextIdCliente.getText().toString());
        long idInfoReferencia = Long.parseLong(editTextIdInfoReferencia.getText().toString());
        String dataHoraColeta = dataFormatada;
        String nomeProfissional = editTextNomeProfissional.getText().toString();
        int numAmostras = Integer.parseInt(editTextNumAmostras.getText().toString());
        String condicoesColeta = editTextCondicoesColeta.getText().toString();
        String identificacaoTubos = editTextIdentificacaoTubos.getText().toString();
        String tempoArmazenamento = editTextTempoArmazenamento.getText().toString();
        String condicoesTransporte = editTextCondicoesTransporte.getText().toString();
        String observacoes = editTextObservacoes.getText().toString();
        String reacoesAdversas = editTextReacoesAdversas.getText().toString();
        String acompanhamentoAdicional = editTextAcompanhamentoAdicional.getText().toString();

        return  "---------Dados Importantes----------\n\n"+
                "Nome do Profissional: " + nomeProfissional + "\n" +
                "ID Cliente: " + idCliente + "\n" +
//                "ID Informação de Referência: " + idInfoReferencia + "\n" +
                "Nome do Exame: " + nomeExame + "\n\n" +

                "---------Dados Coletados----------\n\n"+
                "Tipo: " + tipo + "\n" +
                "Data e Hora da Coleta: " + dataHoraColeta + "\n" +
                "Número de Amostras: " + numAmostras + "\n" +
                "Condições da Coleta: " + condicoesColeta + "\n" +
                "Identificação dos Tubos: " + identificacaoTubos + "\n" +
                "Tempo de Armazenamento: " + tempoArmazenamento + "\n" +
                "Condições de Transporte: " + condicoesTransporte + "\n" +
                "Observações: " + observacoes + "\n" +
                "Reações Adversas: " + reacoesAdversas + "\n" +
                "Acompanhamento Adicional: " + acompanhamentoAdicional;
    }

    private void mostrarPopup() {
        new AlertDialog.Builder(DetalhesAgendamentoActivity.this)
                .setTitle("Dados Enviados")
                .setMessage("Os dados foram enviados com sucesso!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DetalhesAgendamentoActivity.this, VisitaActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }
}

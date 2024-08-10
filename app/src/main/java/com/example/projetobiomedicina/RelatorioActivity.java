package com.example.projetobiomedicina;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RelatorioActivity extends AppCompatActivity {

    private EditText campoLogin;
    private Button btnBuscar;
    private Button btnLimpar;
    private Button btnPrint;
    private TextView textInfo;
    private List<JSONObject> dadosExames;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio); // Certifique-se de que o layout correto está sendo carregado

        campoLogin = findViewById(R.id.campoLogin);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpar = findViewById(R.id.btnLimpar);
        textInfo = findViewById(R.id.textInfo);
        btnPrint = findViewById(R.id.btnPrint);

        campoLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                String login = campoLogin.getText().toString();
                new BuscarExameTask().execute(login);
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                campoLogin.setText("");
                textInfo.setText("");
                textInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                btnPrint.setEnabled(false);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dadosExames == null || dadosExames.isEmpty()) {
                    Toast.makeText(RelatorioActivity.this, "Nenhum exame buscado para gerar o PDF", Toast.LENGTH_SHORT).show();
                } else {
                    createPdf();
                }
            }
        });

        btnPrint.setEnabled(false);
    }

    private class BuscarExameTask extends AsyncTask<String, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(String... strings) {
            String login = strings[0];
            String apiUrl = "http://192.168.0.40:8080/resultadoexame/byUserLogin/" + login;
            List<JSONObject> dadosExames = new ArrayList<>();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dadosExames.add(jsonObject);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dadosExames;
        }

        @Override
        protected void onPostExecute(List<JSONObject> result) {
            if (result != null && !result.isEmpty()) {
                dadosExames = result;

                StringBuilder displayText = new StringBuilder();
                for (JSONObject jsonObject : result) {
                    try {
                        JSONObject paciente = jsonObject.getJSONObject("paciente");
                        String nome = paciente.getString("nomepac");
                        String cpf = paciente.getString("cpfpac");

                        displayText.append("Nome: ").append(nome).append("\n")
                                .append("CPF: ").append(cpf).append("\n\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                textInfo.setText(displayText.toString());

                Drawable icon = getResources().getDrawable(R.drawable.ic_check_green);
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                textInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                textInfo.setCompoundDrawablePadding(8);

                btnPrint.setEnabled(true);
                Toast.makeText(RelatorioActivity.this, "Exame(s) encontrado(s) com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RelatorioActivity.this, "Exame(s) não encontrado(s)!", Toast.LENGTH_SHORT).show();
                btnPrint.setEnabled(false);
            }
        }
    }

    private void createPdf() {
        if (dadosExames == null || dadosExames.isEmpty()) {
            Toast.makeText(this, "Nenhum exame buscado para gerar o PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Report";

        printManager.print(jobName, new MyPrintDocumentAdapter(this, pdfDocument, dadosExames), null);
    }

    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        private Context context;
        private List<JSONObject> dadosExames;
        private PdfDocument pdfDocument;
        private int pageWidth;
        private int pageHeight;
        private int totalPages;

        public MyPrintDocumentAdapter(Context context, PdfDocument pdfDocument, List<JSONObject> dadosExames) {
            this.context = context;
            this.pdfDocument = pdfDocument;
            this.dadosExames = dadosExames;
            this.totalPages = dadosExames.size();
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            pdfDocument = new PrintedPdfDocument(context, newAttributes);
            pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("Resultado")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                return;
            }

            for (int i = 0; i < totalPages; i++) {
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    return;
                }

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                if (page == null) {
                    callback.onWriteFailed("Failed to start page " + i);
                    return;
                }

                drawPage(page, i);
                pdfDocument.finishPage(page);
            }

            try {
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                callback.onWriteFinished(pages);
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
            } finally {
                pdfDocument.close();
            }
        }

        private void drawPage(PdfDocument.Page page, int pageNumber) {
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(14); // Aumentar o tamanho da fonte para melhor legibilidade

            int x = 10;
            int y = 25;

            // Desenha o logotipo no topo
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fasipe);
            int imageWidth = pageWidth / 3;
            int imageHeight = (bitmap.getHeight() * imageWidth) / bitmap.getWidth();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);

            int imageX = (pageWidth - imageWidth) / 2;
            canvas.drawBitmap(scaledBitmap, imageX, y, null);
            y += imageHeight + 40; // Aumentar o espaçamento após o logotipo

            // Desenha o conteúdo do exame atual
            try {
                JSONObject exame = dadosExames.get(pageNumber);

                JSONObject paciente = exame.getJSONObject("paciente");
                JSONObject procedimentos = exame.getJSONObject("procedimentos");
                JSONObject user = exame.getJSONObject("user");
                JSONObject infoReferencia = exame.getJSONObject("infoReferencia");

                String dataResultadoFormatada = formatarData(exame.getString("dtresultado"));


                String[] lines = {
                        "Nome: " + paciente.getString("nomepac"),
                        "CPF: " + paciente.getString("cpfpac"),
                        "Código do Paciente: " + paciente.getInt("codpac"),
                        "Telefone: " + paciente.getString("telpac"),
                        "CEP: " + paciente.getString("ceppac"),
                        "Endereço: " + paciente.getString("lograpac") + ", " + paciente.getString("numlograpac") + ", " + paciente.getString("complpac") + ", " + paciente.getString("bairropac") + ", " + paciente.getString("cidadepac") + " - " + paciente.getString("ufpac"),
                        "RG: " + paciente.getString("rgpac"),
                        "Estado do RG: " + paciente.getString("estrgpac"),
                        "Nome da Mãe: " + paciente.getString("nomemaepac"),
                        "Data de Nascimento: " + formatarData(paciente.getString("dtnascpac")), // Formata a data de nascimento
                        "Código do Procedimento: " + procedimentos.getString("codProced"),
                        "Descrição do Procedimento: " + procedimentos.getString("descProced"),
                        "Valor do Procedimento: " + procedimentos.getDouble("valProced"),
                        "Médico: " + user.getString("nome"),
                        "Login do Médico: " + user.getString("login"),
                        "Referência Mínima: " + infoReferencia.getDouble("referenciamin"),
                        "Referência Máxima: " + infoReferencia.getDouble("referenciamax"),
                        "Medida: " + infoReferencia.getString("medida"),
                        "Resultado: " + exame.getInt("valor"),
                        "Unidade de Medida: " + exame.getString("medida"),
                        "Observação: " + exame.getString("observacao"),
                        "Link do Resultado: " + exame.getString("linkresultado"),
                        "Data do Resultado: " + formatardatahora(exame.getString("dtresultado"))
                };

                for (String line : lines) {
                    canvas.drawText(line, x, y, paint);
                    y += paint.descent() - paint.ascent() + 10; // Aumentar o espaçamento entre as linhas
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static String formatarData(String data) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            java.util.Date date = inputFormat.parse(data);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            String formattedDate = outputDateFormat.format(date);

            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return data; // Retorna a data original em caso de erro
        }

    }

    private static String formatardatahora(String data) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            java.util.Date date = inputFormat.parse(data);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            String formattedDate = outputDateFormat.format(date);

            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return data; // Retorna a data original em caso de erro
        }
    }
}
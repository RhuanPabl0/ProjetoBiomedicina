package com.example.projetobiomedicina;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RelatorioActivity extends AppCompatActivity {

    private EditText campoCpf;
    private Button btnBuscar;
    private Button btnLimpar;
    private Button btnPrint;
    private TextView textInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_relatorio);

        campoCpf = findViewById(R.id.campoCpf);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpar = findViewById(R.id.btnLimpar);
        textInfo = findViewById(R.id.textInfo);
        btnPrint = findViewById(R.id.btnPrint);


        // Adiciona um TextWatcher para limitar o campo de CPF a 11 dígitos
        campoCpf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cpf = s.toString();
                if (cpf.length() > 11) {
                    campoCpf.setText(cpf.substring(0, 11));
                    campoCpf.setSelection(11);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                String cpf = campoCpf.getText().toString();
                if (!isValidCpf(cpf)) {
                    Toast.makeText(RelatorioActivity.this, "CPF inválido", Toast.LENGTH_SHORT).show();
                } else {
                    new BuscarCpfTask().execute(cpf);
                }
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                campoCpf.setText("");
                textInfo.setText("");
                textInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                btnPrint.setEnabled(false); // Desabilita o botão de impressão
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textInfo.getText().toString().isEmpty()) {
                    Toast.makeText(RelatorioActivity.this, "Nenhum paciente buscado para gerar o PDF", Toast.LENGTH_SHORT).show();
                } else {
                    createPdf();
                }
            }
        });

        // Desabilita o botão de impressão inicialmente
        btnPrint.setEnabled(false);
    }

    private boolean isValidCpf(String cpf) {
        return cpf.length() == 11;
    }

    private class BuscarCpfTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String cpf = strings[0];
            String apiUrl = "http://192.168.0.40:8080/paciente/" + cpf;

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

                    JSONObject jsonObject = new JSONObject(response.toString());
                    return jsonObject.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String nome = jsonObject.getString("nomepac");
                    String cpf = jsonObject.getString("cpfpac");
                    String telefone = jsonObject.getString("telpac");
                    String cep = jsonObject.getString("ceppac");
                    String endereco = jsonObject.getString("lograpac") + ", " + jsonObject.getString("numlograpac") + " - " + jsonObject.getString("complpac");
                    String bairro = jsonObject.getString("bairropac");
                    String cidade = jsonObject.getString("cidadepac");
                    String uf = jsonObject.getString("ufpac");
                    String rg = jsonObject.getString("rgpac");
                    String estadoRg = jsonObject.getString("estrgpac");
                    String nomeMae = jsonObject.getString("nomemaepac");
                    String dtNasc = jsonObject.getString("dtnascpac");

                    String info = "Nome: " + nome + "\n" +
                            "CPF: " + cpf + "\n" +
                            "Telefone: " + telefone + "\n" +
                            "CEP: " + cep + "\n" +
                            "Endereço: " + endereco + "\n" +
                            "Bairro: " + bairro + "\n" +
                            "Cidade: " + cidade + "\n" +
                            "UF: " + uf + "\n" +
                            "RG: " + rg + "\n" +
                            "Estado do RG: " + estadoRg + "\n" +
                            "Nome da Mãe: " + nomeMae + "\n" +
                            "Data de Nascimento: " + dtNasc;
                    textInfo.setText(info);

                    Drawable icon = getResources().getDrawable(R.drawable.ic_check_green);
                    icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                    textInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                    textInfo.setCompoundDrawablePadding(8);

                    // Habilita o botão de impressão quando os dados do paciente são buscados
                    btnPrint.setEnabled(true);

                    // Exibe mensagem de sucesso
                    Toast.makeText(RelatorioActivity.this, "Paciente encontrado com sucesso!", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Exibe mensagem de erro
                Toast.makeText(RelatorioActivity.this, "Paciente não encontrado!", Toast.LENGTH_SHORT).show();
                // Desabilita o botão de impressão quando nenhum paciente é encontrado
                btnPrint.setEnabled(false);
            }
        }
    }

    private void createPdf() {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Document";
        printManager.print(jobName, new MyPrintDocumentAdapter(this, textInfo.getText().toString()), null);
    }

    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        private Context context;
        private String content;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument pdfDocument;

        public MyPrintDocumentAdapter(Context context, String content) {
            this.context = context;
            this.content = content;
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

            if (pdfDocument.getPages().size() > 0) {
                callback.onLayoutFinished(new PrintDocumentInfo.Builder("document")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(pdfDocument.getPages().size())
                        .build(), true);
            } else {
                callback.onLayoutFinished(new PrintDocumentInfo.Builder("document")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build(), true);
            }
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                pdfDocument.close();
                pdfDocument = null;
                return;
            }

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);

            int x = 10, y = 25;

            // Carrega a imagem dos recursos
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fasipe);
            // Redimensiona a imagem
            int newWidth = 100;  // Defina a nova largura
            int newHeight = (bitmap.getHeight() * newWidth) / bitmap.getWidth();  // Mantém a proporção
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Desenha a imagem no canto superior esquerdo
            canvas.drawBitmap(scaledBitmap, x, y, null);

            // Ajusta a posição inicial do texto
            y += newHeight + 20;

            // Desenha borda ao redor do texto
            int borderPadding = 10;
            Rect border = new Rect(x - borderPadding, y - newHeight - 20 - borderPadding, pageWidth - x + borderPadding, pageHeight - y + borderPadding);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRect(border, paint);
            paint.setStyle(Paint.Style.FILL);

            // Desenha o texto da ficha
            for (String line : content.split("\n")) {
                canvas.drawText(line, x, y, paint);
                y += paint.descent() - paint.ascent();
            }

            pdfDocument.finishPage(page);

            try {
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                pdfDocument.close();
                pdfDocument = null;
            }
            callback.onWriteFinished(new PageRange[]{new PageRange(0, 0)});
        }
    }
}
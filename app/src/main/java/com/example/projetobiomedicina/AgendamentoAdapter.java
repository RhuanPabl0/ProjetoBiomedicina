package com.example.projetobiomedicina;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class AgendamentoAdapter extends RecyclerView.Adapter<AgendamentoAdapter.ViewHolder> {
    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private Context mContext;

    AgendamentoAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_agendamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject jsonObject = mData.get(position);
        String cpf = jsonObject.optString("cpfpac");
        String nome = jsonObject.optString("nomepac");
        String dataConsulta = jsonObject.optString("dataConsulta");
        String telefone = jsonObject.optString("telpac");
        String endereco = jsonObject.optString("lograpac") + ", " + jsonObject.optString("numlograpac")
                + ", " + jsonObject.optString("complpac") + ", " + jsonObject.optString("bairropac")
                + ", " + jsonObject.optString("ceppac");
        String procedimento = jsonObject.optString("descProced");
        String login = jsonObject.optString("login");

//        holder.textViewLogin.setText("Login: " + login);
        holder.textViewCpf.setText("CPF: " + cpf);
        holder.textViewNome.setText("Nome: " + nome);
        holder.textViewDataConsulta.setText("Data: " + dataConsulta);
        holder.textViewTelefone.setText("Telefone: " + telefone);
        holder.textViewEndereco.setText("Endereço: " + endereco);
        holder.textViewProcedimento.setText("Procedimento: " + procedimento);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DethalhesAgendamentoActivity.class);
            intent.putExtra("agendamento", jsonObject.toString());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCpf, textViewNome, textViewDataConsulta, textViewTelefone, textViewEndereco, textViewProcedimento;

        ViewHolder(View itemView) {
            super(itemView);
            textViewCpf = itemView.findViewById(R.id.textViewCpf);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDataConsulta = itemView.findViewById(R.id.textViewDataConsulta);
            textViewTelefone = itemView.findViewById(R.id.textViewTelefone);
            textViewEndereco = itemView.findViewById(R.id.textViewEndereco);
            textViewProcedimento = itemView.findViewById(R.id.textViewProcedimento);
        }
    }
}

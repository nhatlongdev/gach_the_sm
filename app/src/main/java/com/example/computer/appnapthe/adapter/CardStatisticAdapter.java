package com.example.computer.appnapthe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.listener.IOnItemClickedListener;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by along on 5/24/2017.
 */

public class CardStatisticAdapter extends RecyclerView.Adapter<CardStatisticAdapter.DishViewHolder>{

    private Context context;
    private List<String> listIdCard;
    private JSONObject listJsonCard;
    private IOnItemClickedListener listener;

    public CardStatisticAdapter(Context context, List<String> listIdCard, JSONObject listJsonCard, IOnItemClickedListener listener){
        this.context = context;
        this.listIdCard = listIdCard;
        this.listJsonCard = listJsonCard;
        this.listener = listener;
    }

    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_statistic,parent,false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DishViewHolder holder, final int position) {
        JSONObject jsonObjectItem = listJsonCard.optJSONObject(listIdCard.get(position));
        if(jsonObjectItem != null){
            holder.tvNameCard.setText(jsonObjectItem.optString("name_card"));
            holder.btnGuiLai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.btnTraCuu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(position,view);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return listIdCard.size();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNameCard;
        private Button btnGuiLai, btnTraCuu;
        public DishViewHolder(View itemView) {
            super(itemView);
            tvNameCard = itemView.findViewById(R.id.tv_name_card);
            btnGuiLai = itemView.findViewById(R.id.btn_gui_lai);
            btnTraCuu = itemView.findViewById(R.id.btn_tra_cuu);
        }
    }
}

package com.example.computer.appnapthe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.computer.appnapthe.R;
import com.example.computer.appnapthe.listener.IOnItemClickedListener;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by along on 5/24/2017.
 */

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.DishViewHolder>{

    private Context context;
    private List<String> listIdProvider;
    private JSONObject listJsonProvider;
    private IOnItemClickedListener listener;

    public ProviderAdapter(Context context, List<String> listIdProvider, JSONObject listJsonProvider, IOnItemClickedListener listener){
        this.context = context;
        this.listIdProvider = listIdProvider;
        this.listJsonProvider = listJsonProvider;
        this.listener = listener;
    }

    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nha_mang,parent,false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DishViewHolder holder, final int position) {
        JSONObject jsonObject = listJsonProvider.optJSONObject(listIdProvider.get(position));
        if(jsonObject != null){
            holder.tvNameProvider.setText(jsonObject.optString("name"));
            if(jsonObject.optBoolean("click") == true){
                holder.imgCircleRed.setVisibility(View.VISIBLE);
            }else {
                holder.imgCircleRed.setVisibility(View.INVISIBLE);
            }
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
        return listIdProvider.size();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgCircleRed;
        private TextView tvNameProvider;
        public DishViewHolder(View itemView) {
            super(itemView);
            imgCircleRed = itemView.findViewById(R.id.icon_img_red);
            tvNameProvider = itemView.findViewById(R.id.tv_name_provider);
        }
    }
}

package com.example.sah.myweather;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public static final String ICON_URL = "http://openweathermap.org/img/w/";

    private OnItemClickListener onItemClickListener;
    private ArrayList<String> data;
    private ArrayList<String> dataIcon;
    private Context context;


    public MyAdapter (ArrayList<String> strings, ArrayList<String> icons){
        data = strings;
        dataIcon = icons;

    }


    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items_details, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
        context = holder.imageView.getContext();
        Picasso.with(context).load(ICON_URL + dataIcon.get(position) + ".png").into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_item);
            imageView = (ImageView) itemView.findViewById(R.id.iv_temp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnItemClick(getAdapterPosition());
                }
            });
        }
    }
    public void setOnItemClickListener (OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;

    }

    public interface OnItemClickListener{
        void OnItemClick (int position);

    }
}
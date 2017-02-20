package com.example.sah.myweather;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder> {

    public static final String ICON_URL = "http://openweathermap.org/img/w/";

    private OnItemClickListener onItemClickListener;
    private String[] data;
    private Context context;
    private String iconName;



    public DetailsAdapter (String[] strings, String icon){
        data = strings;
        iconName = icon;


    }


    @Override
    public DetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items_details, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DetailsAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(data[position]);

        switch (position){
            case 0:
                context = holder.iv_temp.getContext();
                Picasso.with(context).load(R.drawable.dat).into(holder.iv_temp);
                holder.tvDate.setText(data[position].substring(0,2));
                break;
            case 1:
                context = holder.iv_temp.getContext();
                Picasso.with(context).load(ICON_URL + iconName + ".png").into(holder.iv_temp);
                break;
            case 2:
                holder.iv_temp.setImageResource(R.drawable.therm);
                break;
            case 3:
                holder.iv_temp.setImageResource(R.drawable.humidity);
                break;
            case 4:
                holder.iv_temp.setImageResource(R.drawable.pressure);
                break;
            case 5:
                holder.iv_temp.setImageResource(R.drawable.wind);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView, tvDate;
        ImageView iv_temp;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_temp = (ImageView) itemView.findViewById(R.id.iv_temp);
            textView = (TextView) itemView.findViewById(R.id.tv_item);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
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


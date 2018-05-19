package com.example.abis.lost__find.ForRecycleView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.squareup.picasso.Picasso;


import com.example.abis.lost__find.ForRecycleView.CustomPojo;
import com.example.abis.lost__find.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    //Creating an arraylist of POJO objects
    private List<CustomPojo> list_members=new ArrayList<>();
    View view;
    private Context context;


    public CustomAdapter(List<CustomPojo> customPojos, Context context){
        this.list_members=customPojos;
        this.context=context;
    }


    @Override
    public int getItemCount() {
        return list_members.size();
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(list_members.get(position).getMessage());
        String imageUrl=list_members.get(position).getImgpath();
        if(imageUrl!=null)
        Picasso.with(context).load(imageUrl).resize(120, 60).into(holder.imageView);
        else
            holder.imageView.setImageResource(0);

    }
    //This method inflates view present in the RecyclerView
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row, parent, false);
        return new MyViewHolder(v);
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        View v;

        public MyViewHolder(View itemView) {
            super(itemView);
            v=itemView;
            this.textView = (TextView) itemView.findViewById(R.id.showMessage);
            this.imageView=(ImageView)itemView.findViewById(R.id.showImage);
        }
    }

}
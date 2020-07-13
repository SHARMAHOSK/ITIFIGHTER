package com.example.itifighter.ui.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itifighter.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private  String data1[],data2[];
    //private int images[];
    private Context context;
    public MyAdapter(Context context, String[] data1, String[] data2){
         this.context = context;
       //  this.images = images;
         this.data1 = data1;
         this.data2 = data2;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_menu_group_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text1.setText(data1[position]);
        holder.text2.setText(data2[position]);
      //  holder.myImage.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text1,text2;
       // ImageView myImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.group_name_text);
            text2 = itemView.findViewById(R.id.group_desc_text);
         //   myImage = itemView.findViewById(R.id.group_image_view);
        }
    }
}

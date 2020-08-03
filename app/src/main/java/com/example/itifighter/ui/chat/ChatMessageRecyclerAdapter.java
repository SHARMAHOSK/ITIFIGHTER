package com.example.itifighter.ui.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itifighter.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>{

    private ArrayList<ChatMessage> mMessages;
    private Context mContext;
    public ChatMessageRecyclerAdapter(ArrayList<ChatMessage> messages,
                                      Context context) {
        this.mMessages = messages;
        this.mContext = context;
    }


    public int getItemViewType(int position){
        if(Objects.equals(FirebaseAuth.getInstance().getUid(), mMessages.get(position).getUser().getUser_id()))return 0;
        else return 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.their_message, parent, false);
        if (viewType == 0) return new ViewHolder(view0);
        else return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(!Objects.equals(FirebaseAuth.getInstance().getUid(), mMessages.get(position).getUser().getUser_id())){
            holder.username.setTextColor(ContextCompat.getColor(mContext, R.color.blue2));
            holder.username.setText(mMessages.get(position).getUser().getUsername());
        }

        holder.message.setText(mMessages.get(position).getMessage());
            holder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view){ try{
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        String x = date.format(new Date(mMessages.get(position).getTimestamp().toString()));
                    if (holder.timestamp.getText() == "") holder.timestamp.setText(x);
                    else holder.timestamp.setText("");
                }catch(Exception e){System.out.print(e.getMessage());}}
            });
        }



    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message, username,timestamp;
        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_message);
            username = itemView.findViewById(R.id.chat_message_username);
            timestamp = itemView.findViewById(R.id.messsage_timestamp);
        }
    }


}

















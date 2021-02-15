package com.example.itifighter.ui.notification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itifighter.R;
import com.example.itifighter.ui.groups.ListItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore; 
import com.google.firebase.firestore.Query;

public class notificationFragment extends Fragment {
    private FirestoreRecyclerAdapter<ListItem, ItemViewHolder> adapter;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_notification, container, false);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = root.findViewById(R.id.notification_recycle);
        Query query = firebaseFirestore.collection("notification");
        FirestoreRecyclerOptions<ListItem> options = new FirestoreRecyclerOptions.Builder<ListItem>()
                .setQuery(query,ListItem.class).build();
        adapter = new FirestoreRecyclerAdapter<ListItem,notificationFragment.ItemViewHolder>(options) {
            @NonNull
            @Override
            public notificationFragment.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.fragment_menu_notification_list, parent, false);
                return new notificationFragment.ItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final notificationFragment.ItemViewHolder holder, int i, @NonNull final ListItem listItem) {
                holder.text1.setText(listItem.getName());
                holder.selectId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.getUrl())));
                        }
                        catch(Exception e){
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return root;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1;
        private final LinearLayout selectId;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.notification_name_text);
            selectId = itemView.findViewById(R.id.id_notification);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
package com.example.itifighter.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.itifighter.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class GroupsFragment extends Fragment {
    private FirestoreRecyclerAdapter<ListItem, ItemViewHolder> adapter;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_menu_groups, container, false);
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            RecyclerView recyclerView = root.findViewById(R.id.group_recycle);
            Query query = firebaseFirestore.collection("groups");
            FirestoreRecyclerOptions<ListItem> options = new FirestoreRecyclerOptions.Builder<ListItem>()
                    .setQuery(query,ListItem.class).build();
            adapter = new FirestoreRecyclerAdapter<ListItem, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.fragment_menu_group_list, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int i, @NonNull ListItem listItem) {
                holder.text1.setText(listItem.getName());
                holder.text2.setText(listItem.getDesc());
            }

        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return root;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView text1,text2;
        private ImageView myImage;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.group_name_text);
            text2 = itemView.findViewById(R.id.group_desc_text);
            myImage = itemView.findViewById(R.id.group_image_view);
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
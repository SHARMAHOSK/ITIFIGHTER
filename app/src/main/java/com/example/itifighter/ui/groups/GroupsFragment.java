package com.example.itifighter.ui.groups;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itifighter.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
            protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int i, @NonNull final ListItem listItem) {
                Glide.with(requireContext()).load(R.drawable.load_image) .into(holder.myImage);
                holder.text1.setText(listItem.getName());
                holder.text2.setText(listItem.getDesc());
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference ref = firebaseStorage.getReferenceFromUrl("gs://itifighter.appspot.com").child("menu/groups/"+listItem.getName()+".png");
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = uri.toString();
                        Glide.with(requireContext()).load(imageURL).into(holder.myImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(),"Image load failed",Toast.LENGTH_SHORT).show();
                    }
                });
                holder.selectId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.getUrl())));}
                        catch(Exception e){ Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();}
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
        private TextView text1,text2;
        private ImageView myImage;
        private androidx.cardview.widget.CardView selectId;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.group_name_text);
            text2 = itemView.findViewById(R.id.group_desc_text);
            myImage = itemView.findViewById(R.id.group_image_view);
            selectId = itemView.findViewById(R.id.selectIdGroup);
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
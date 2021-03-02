package com.example.itifighter.ui.notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itifighter.R;
import com.example.itifighter.ui.groups.ListItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class notificationFragment extends Fragment {
    private FirestoreRecyclerAdapter<ListItem, ItemViewHolder> adapter;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_notification, container, false);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = root.findViewById(R.id.notification_recycle);
        Query query = firebaseFirestore.collection("notification").orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ListItem> options = new FirestoreRecyclerOptions.Builder<ListItem>()
                .setQuery(query,ListItem.class).build();
        adapter = new FirestoreRecyclerAdapter<ListItem,ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.fragment_menu_notification_list, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int i, @NonNull final ListItem listItem) {

                try{
                    Glide.with(requireContext()).load(R.drawable.new_jobs) .into(holder.image);
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
                    holder.image.setVisibility(isDateExistInPastSevenDays(listItem.getDate())?View.VISIBLE:View.GONE);
                }catch (Exception e){

                }
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return root;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1;
        private final ImageView image;
        private final LinearLayout selectId;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.notification_name_text);
            selectId = itemView.findViewById(R.id.id_notification);
            image = itemView.findViewById(R.id.notification_counter);
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


    public boolean isDateExistInPastSevenDays(String date) throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date date1 = cal.getTime(), date2 = new Date();
        Date date3 = formatter.parse(date);
        assert date3 != null;
        return date3.compareTo(date1) >= 0 && date3.compareTo(date2) <= 0;
    }
}
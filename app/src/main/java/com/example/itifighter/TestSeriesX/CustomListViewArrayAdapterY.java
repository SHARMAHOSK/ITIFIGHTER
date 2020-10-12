package com.example.itifighter.TestSeriesX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CustomListViewArrayAdapterY extends ArrayAdapter<CustomListItemY> {
    private Context context;
    private LayoutInflater inflater;
    private FirebaseStorage mFirebaseStorage= FirebaseStorage.getInstance();
    private Handler handler;
    private ArrayList<CustomListItemY> ProductData;
    private String counter;

    @Override
    public int getCount() { return ProductData.size(); }

    @Nullable
    @Override
    public CustomListItemY getItem(int position) { return  ProductData.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CustomListViewArrayAdapterY(Context context, int resource,ArrayList<CustomListItemY> ProductData) {
        super(context, resource , ProductData);
        this.context = context;
        this.ProductData = ProductData;
    }
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final CustomListItemY property = ProductData.get(position);
        final String currentSubject = property.getCurrentSubject(),
                currentChapter= property.getCurrentChapter(),
                expiryDate = property.getExpiryDate();
        if (inflater == null) inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.test_series_chapter, null);
        final ImageView thumbNail = view.findViewById(R.id.testxy_image_view);
        final TextView test = view.findViewById(R.id.testxy_desc_text);
        final TextView topicHeader = view.findViewById(R.id.testxy_chapter_title);
        final TextView duration = view.findViewById(R.id.counterTime);
        DocumentReference reference = FirebaseFirestore.getInstance().collection("section").document("ts").collection("branch")
                .document(currentSubject).collection("chapter").document(currentChapter);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot!=null){
                        topicHeader.setText(documentSnapshot.getString("name"));
                        StorageReference mmFirebaseStorageRef = mFirebaseStorage.getReference().child("menu/section/ts/chapter/");
                        if(documentSnapshot.getString("name") != null){
                            if(Objects.requireNonNull(documentSnapshot.getString("name")).trim().length() > 0){
                                Glide.with(context)
                                        .load(mmFirebaseStorageRef.child(Objects.requireNonNull(documentSnapshot.getString("name"))))
                                        .into(thumbNail);
                            }
                        }
                        if(documentSnapshot.getString("test") != null) test.setText(documentSnapshot.getString("test")+" Test");
                        else test.setText("0 Test");
                       /* b.setText("Go");
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, TestListSeries.class);
                                intent.putExtra("currentSubject",currentSubject);   
                                intent.putExtra("currentChapter",currentChapter);
                                intent.putExtra("ExpiryDate",counter);
                                intent.putExtra("SeriesName",documentSnapshot.getString("name"));
                                intent.putExtra("SeriesCount",documentSnapshot.getString("test"));
                                context.startActivity(intent);
                            }
                        });*/
                        startCounter(expiryDate,duration);
                    }
                }
            }
        });
        return view;
    }
    private void startCounter(final String expiryDate, final TextView counterTime) {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date futureDate = dateFormat.parse(expiryDate);
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        assert futureDate != null;
                        long diff = futureDate.getTime() - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        /*diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;*/
                        counter = "Expire in " + String.format("%02d", days) + " Days " /*+ String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)*/;
                        counterTime.setText(counter);
                    } else {
                        textViewGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void textViewGone() {
    }
}

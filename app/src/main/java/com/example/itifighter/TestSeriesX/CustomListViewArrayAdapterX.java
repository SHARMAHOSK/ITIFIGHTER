package com.example.itifighter.TestSeriesX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomListViewArrayAdapterX extends ArrayAdapter<CustomListItemX> {
    private Context context;
    private List<CustomListItemX> Chapters;
    private ArrayList<String> ChapterId;
    private LayoutInflater inflater;
    private Handler handler;
    private FirebaseStorage mFirebaseStorage= FirebaseStorage.getInstance();
    private String Uid = FirebaseAuth.getInstance().getUid(),currentSubject;

    @Override
    public int getCount() {
        return Chapters.size();
    }

    @Nullable
    @Override
    public CustomListItemX getItem(int position) {
        return Chapters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CustomListViewArrayAdapterX(Context context, int resource, ArrayList<CustomListItemX> objects,String currentSubject,ArrayList<String> ChapterId) {
        super(context, resource, objects);
        this.context = context;
        this.Chapters = objects;
        this.currentSubject = currentSubject;
        this.ChapterId = ChapterId;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final CustomListItemX property = Chapters.get(position);
        if (inflater == null) inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.test_series_chapter, null);

        ImageView thumbNail = view.findViewById(R.id.testxy_image_view);
        TextView test = view.findViewById(R.id.testxy_desc_text);
        TextView topicHeader = view.findViewById(R.id.testxy_chapter_title);
        TextView price = view.findViewById(R.id.Test_Price);
        TextView Fprice = view.findViewById(R.id.Test_FPrice);
        final TextView counterTime = view.findViewById(R.id.counterTime);


        StorageReference mmFirebaseStorageRef = mFirebaseStorage.getReference().child("menu/section/" + property.getImagex() + "/chapter/");
        if(property.getTopicHeader() != null){
            if(property.getTopicHeader().trim().length() > 0){
                Glide.with(context)
                        .load(mmFirebaseStorageRef.child(property.getTopicHeader()))
                        .into(thumbNail);
            }
        }
        topicHeader.setText(property.getTopicHeader());
        if(property.getTest() != null) test.setText(property.getTest()+ "Test");
        else test.setText("0 Test");
        price.setText(property.getPrice1()+" \u20B9");
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Fprice.setText(""+((Integer.parseInt(property.getPrice1())*Integer.parseInt(property.getDiscount1()))/100)+" \u20B9");

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid).collection("Products")
                .document(property.getImagex()).collection("ProductId").document(ChapterId.get(position));
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot!=null && documentSnapshot.exists()){
                       startCounter(documentSnapshot.getString("ExpiryDate"),counterTime);
                    }
                }
            }
        });
        //duration.setText(property.getMonth1()+" months");
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
                        String counter = "Expire in " + String.format("%02d", days) + " Days " /*+ String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)*/;
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

package com.example.itifighter.TestSeriesX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.itifighter.PaytmPayment;
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
        TextView duration = view.findViewById(R.id.testxytbatch);
        TextView price = view.findViewById(R.id.testxytprice);
        final Button b = view.findViewById(R.id.buttonxy);
        final TextView counterTime = view.findViewById(R.id.counterTime);
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Uid).collection("Products")
                .document(property.getImagex()).collection("ProductId").document(ChapterId.get(position));
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot!=null && documentSnapshot.exists()){
                       b.setText("Go");
                       b.animate().scaleX(1);
                       b.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Toast.makeText(getContext(),"button clicked",Toast.LENGTH_SHORT).show();
                           }
                       });
                       startCounter(documentSnapshot.getString("ExpiryDate"),counterTime);
                    }
                    else{
                        b.setText("Buy");
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), PaytmPayment.class);
                                intent.putExtra("ProductId",property.getId());
                                intent.putExtra("month1",property.getMonth1());
                                intent.putExtra("month2",property.getMonth2());
                                intent.putExtra("month3",property.getMonth3());
                                intent.putExtra("price1",property.getPrice1());
                                intent.putExtra("price2",property.getPrice2());
                                intent.putExtra("price3",property.getPrice3());
                                intent.putExtra("discount1",property.getDiscount1());
                                intent.putExtra("discount2",property.getDiscount2());
                                intent.putExtra("discount3",property.getDiscount3());
                                intent.putExtra("currentSection",property.getImagex());
                                intent.putExtra("titleName",property.getTopicHeader());
                                intent.putExtra("countTest",property.getTest());
                                intent.putExtra("currentSubject",currentSubject);
                                intent.putExtra("currentChapter",ChapterId.get(position));
                                context.startActivity(intent);
                            }
                        });
                    }
                }
            }
        });
        topicHeader.setText(property.getTopicHeader());
        StorageReference mmFirebaseStorageRef = mFirebaseStorage.getReference().child("menu/section/" + property.getImagex() + "/chapter/");
        if(property.getTopicHeader() != null){
            if(property.getTopicHeader().trim().length() > 0){
                Glide.with(context)
                        .load(mmFirebaseStorageRef.child(property.getTopicHeader()+".png"))
                        .into(thumbNail);
            }
        }
        if(property.getTest() != null) test.setText(property.getTest()+ " Test");
        else test.setText("0 Test");
        duration.setText(property.getMonth1()+" months");
        price.setText(property.getPrice1()+" \u20B9");
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

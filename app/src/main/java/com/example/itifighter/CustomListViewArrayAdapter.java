package com.example.itifighter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

class CustomListViewArrayAdapter extends ArrayAdapter<CustomListItem> {
    private Context context;
    private List<CustomListItem> Subjects;
    private LayoutInflater inflater;
    FirebaseStorage mFirebaseStorage= FirebaseStorage.getInstance();
    StorageReference mmFirebaseStorageRef;
    @Override
    public int getCount() { return Subjects.size(); }

    @Nullable
    @Override
    public CustomListItem getItem(int position) {
        return Subjects.get(position);
    }

    @Override
    public long getItemId(int position) {return position;}
    public CustomListViewArrayAdapter(Context context, int resource, ArrayList<CustomListItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.Subjects = objects;
    }

    // called when rendering the list
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //get the property we are displaying
        CustomListItem property = Subjects.get(position);
        return property.getType() == 1 ? getType1(property) : getType0(property);
    }

    private View getType0(CustomListItem property) {
        //get the inflater and inflate the XML layout for each item
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_custom_list_view_row, null);

        ImageView thumbNail = view
                .findViewById(R.id.thumbnail);
        TextView description = view.findViewById(R.id.genre);
        TextView topicHeader = view.findViewById(R.id.title);

        topicHeader.setText(property.getTopicHeader());
        mmFirebaseStorageRef=mFirebaseStorage.getReference().child("menu/section/"+property.getImagex()+"/");

        Glide.with(context)
                .load(mmFirebaseStorageRef.child(""+property.getTopicHeader()))
                .into(thumbNail);
        //display trimmed excerpt for description

        if(property.getDescription() != null){
            int descriptionLength = property.getDescription().length();
            if(descriptionLength >= 100){
                String descriptionTrim = property.getDescription().substring(0,100) + "...";
                description.setText(descriptionTrim);
            }else{
                description.setText(property.getDescription());
            }
        }else{ description.setText("--"); }

        return view;
    }

    private View getType1(CustomListItem property) {
        //get the inflater and inflate the XML layout for each item
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_mock_chapter_xyz, null);

        ImageView thumbNail = view
                .findViewById(R.id.thumbnail);
        TextView topicHeader = view.findViewById(R.id.title);

        topicHeader.setText(property.getTopicHeader());
        mmFirebaseStorageRef=mFirebaseStorage.getReference().child("menu/section/"+property.getImagex()+"/");

        Glide.with(context)
                .load(mmFirebaseStorageRef.child(""+property.getTopicHeader()))
                .circleCrop()
                .into(thumbNail);
        //display trimmed excerpt for description
        String _NOQ_DUR_MRX = ""+property.getQuesCount()+" Qs  |  "+property.getDuration()+" Mins  |  "+(property.getQuesCount()*property.getMPQ())+" Marks";
        ((TextView)view.findViewById(R.id.MockChap_NOQ_DUR_MRX)).setText(_NOQ_DUR_MRX);
        TextView originalPrice = (TextView)view.findViewById(R.id.MockChap_Price);
        originalPrice.setText(""+property.getPrice());
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ((TextView)view.findViewById(R.id.MockChap_FPrice)).setText(""+(property.getPrice() - property.getDiscount()));

        return view;
    }
}

package com.example.itifighter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    public int getCount() {
        return Subjects.size();
    }

    @Nullable
    @Override
    public CustomListItem getItem(int position) {
        return Subjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CustomListViewArrayAdapter(Context context, int resource, ArrayList<CustomListItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.Subjects = objects;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        CustomListItem property = Subjects.get(position);

        //get the inflater and inflate the XML layout for each item
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_custom_list_view_row, null);

        ImageView thumbNail = view
                .findViewById(R.id.thumbnail);

        TextView description = (TextView) view.findViewById(R.id.genre);
        TextView topicHeader = (TextView) view.findViewById(R.id.title);

        topicHeader.setText(property.getTopicHeader());

        mmFirebaseStorageRef=mFirebaseStorage.getReference().child("uploads");

        // thumbnail image
        Glide.with(context)
                .load(mmFirebaseStorageRef.child("cccc.png"))
                .into(thumbNail);
        //display trimmed excerpt for description
        int descriptionLength = property.getDescription().length();
        if(descriptionLength >= 100){
            String descriptionTrim = property.getDescription().substring(0, 100) + "...";
            description.setText(descriptionTrim);
        }else{
            description.setText(property.getDescription());
        }

        //get the image associated with this property
        /*int imageID = context.getResources().getIdentifier(property.getImageUrl(), "drawable", context.getPackageName());
        thumbNail.setImageResource(imageID);*/

        return view;
    }
}

package com.example.itifighter.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends  Fragment {


    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView UserImage;
    private String Uid;
    private ProgressDialog dialog;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu_profile, container, false);

        UserImage = root.findViewById(R.id.UserImageX);                         // User Profile image
        final ImageButton imageButton = root.findViewById(R.id.UserImageY);     // User image Button
        final TextView UserName = root.findViewById(R.id.UserNameX);
        final TextView UserAddress = root.findViewById(R.id.UserName_address);
        final TextView UserScore = root.findViewById(R.id.ScoreX);
        final TextView UserRank = root.findViewById(R.id.RankX);
        final TextView UserEmail = root.findViewById(R.id.UserEmailX);
        final TextView UserMobile = root.findViewById(R.id.UserMobileX);
        final TextView UserState = root.findViewById(R.id.UserStateX);
        final TextView UserTrade = root.findViewById(R.id.UserTradeX);
        setDialogMessage();
        Uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance().collection("users").document(Uid)
        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               assert documentSnapshot != null;
               UserEmail.setText(documentSnapshot.getString("Email"));
               UserMobile.setText(documentSnapshot.getString("Mobile"));
               UserName.setText(documentSnapshot.getString("Name"));
               UserAddress.setText(documentSnapshot.getString("State")+", INDIA");
               UserState.setText(documentSnapshot.getString("State"));
               UserTrade.setText(documentSnapshot.getString("Trade"));
               UserScore.setText("0");
               UserRank.setText("0");
               uploadImage();
               imageButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                        chooseImage();
                   }
               });
           }
       });
        return root;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null  && data.getData() != null) {
            dialog.show();
            Uri filePath = data.getData();
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(String.format("UserImage/%s",Uid));
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    uploadImage();
                }
            });
        }
    }

    void uploadImage(){
        Glide.with(this).load(R.drawable.load_image).into(UserImage);
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(String.format("UserImage/%s",Uid));
        Glide.with(ProfileFragment.this)
                .load(ref)
                .placeholder(R.drawable.user)
                .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))))
                .into(UserImage);
    }




    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setDialogMessage() {
        try {
            dialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            setDialogMessage();
        }
    }
}
package com.example.itifighter.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
    private View progressOver;
    private ProgressBar progressBar;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu_profile, container, false);

        UserImage = root.findViewById(R.id.UserImageX);                         // User Profile image
        final ImageButton imageButton = root.findViewById(R.id.UserImageY);     // User image Button
        final TextView UserName = root.findViewById(R.id.UserNameX);
        final TextView UserScore = root.findViewById(R.id.ScoreX);
        final TextView UserRank = root.findViewById(R.id.RankX);
        final TextView UserEmail = root.findViewById(R.id.UserEmailX);
        final TextView UserMobile = root.findViewById(R.id.UserMobileX);
        final TextView UserState = root.findViewById(R.id.UserStateX);
        final TextView UserTrade = root.findViewById(R.id.UserTradeX);

        progressOver = root.findViewById(R.id.progress_overlay);
        progressBar = root.findViewById(R.id.ProfileImageProgress);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));

        Uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance().collection("users").document(Uid)
        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               assert documentSnapshot != null;
               UserEmail.setText(documentSnapshot.getString("Email"));
               UserMobile.setText(documentSnapshot.getString("Mobile"));
               UserName.setText(documentSnapshot.getString("Name"));
               UserState.setText(documentSnapshot.getString("State"));
               UserTrade.setText(documentSnapshot.getString("Trade"));
               UserScore.setText("100");
               UserRank.setText("540");
               imageButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                        chooseImage();
                   }
               });
               StorageReference storageReference = FirebaseStorage.getInstance().getReference();
               uploadImage();
               /* try {
                   final File file = File.createTempFile("image","jpg");
                   ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                           Bitmap bit = BitmapFactory.decodeFile(file.getAbsolutePath());
                           UserImage.setImageBitmap(bit);
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"image loading failed",Toast.LENGTH_SHORT).show();
                       }
                   });
               } catch (IOException ex) {
                   ex.printStackTrace();
                   Toast.makeText(getContext(),"something wrong in loading..",Toast.LENGTH_SHORT).show();
               }*/
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
            progressOver.setVisibility(View.VISIBLE);
            Uri filePath = data.getData();
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(String.format("UserImage/%s",Uid));
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(),ref.toString(),Toast.LENGTH_LONG).show();
                    progressOver.setVisibility(View.GONE);
                        uploadImage();
                    /* Glide.with(ProfileFragment.this)
                            .load(storage.getReference().child("UserImage/%s"+Uid))
                            .into(UserImage);*/
                        /*final File file = File.createTempFile("image","jpg");
                        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bit = BitmapFactory.decodeFile(file.getAbsolutePath());
                                UserImage.setImageBitmap(bit);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"failed to upload",Toast.LENGTH_SHORT).show();
                            }
                        });*/
                }
            });
        }
    }

    void uploadImage(){

        progressBar.setVisibility(View.VISIBLE);
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(String.format("UserImage/%s",Uid));
        Glide.with(ProfileFragment.this)
                .load(ref)
                .placeholder(R.drawable.user)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getContext(),"not set",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Toast.makeText(getContext(),"set",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
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
}
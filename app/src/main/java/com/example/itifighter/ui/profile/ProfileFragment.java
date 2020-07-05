package com.example.itifighter.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.itifighter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends  Fragment {
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView UserImage;
    private FirebaseStorage storage;
    private StorageReference ref;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_profile, container, false);
        UserImage = root.findViewById(R.id.UserImageX);
        final TextView UserName = root.findViewById(R.id.UserNameX);
        final TextView Live = root.findViewById(R.id.TestX);
        final TextView UserSeries = root.findViewById(R.id.SeriesX);
        final TextView UserScore = root.findViewById(R.id.ScoreX);
        final TextView UserEmail = root.findViewById(R.id.UserEmailX);
        final TextView UserMobile = root.findViewById(R.id.UserMobileX);
        final TextView UserIp = root.findViewById(R.id.UserIpX);
        final TextView UserState = root.findViewById(R.id.UserStateX);
        final TextView UserTrade = root.findViewById(R.id.UserTradeX);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final String Uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference reference = fStore.collection("users").document(Uid);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               assert documentSnapshot != null;
               UserEmail.setText(documentSnapshot.getString("Email"));
               UserMobile.setText(documentSnapshot.getString("Mobile"));
               UserIp.setText("6951.0.0.1");
               UserName.setText(documentSnapshot.getString("Name"));
               UserState.setText(documentSnapshot.getString("State"));
               UserTrade.setText(documentSnapshot.getString("Trade"));
               Live.setText("122");
               UserScore.setText("100");
               UserSeries.setText("540");
               UserImage.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                        chooseImage();
                   }
               });
               storage = FirebaseStorage.getInstance();
               ref = storage.getReferenceFromUrl("gs://itifighter.appspot.com").child(String.format("UserImage/%s",Uid));
               try {
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
               }
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
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            Uri filePath = data.getData();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
            String FileName = "UserImage/"+uid;
            StorageReference folder = storage.getReference().child(FileName);
            folder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(),"uploded",Toast.LENGTH_SHORT).show();
                    try {
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
                                Toast.makeText(getContext(),"failed to upload",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Toast.makeText(getContext(),"something wrong to upload",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
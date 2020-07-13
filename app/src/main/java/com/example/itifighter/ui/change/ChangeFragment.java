package com.example.itifighter.ui.change;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu_change, container, false);
        Button reset = root.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText oldPassword = root.findViewById(R.id.OldId);
                final EditText newPassword = root.findViewById(R.id.NewId);
                final EditText conPassword = root.findViewById(R.id.ConId);
                final String o = oldPassword.getText().toString();
                final String n = newPassword.getText().toString();
                final String c = conPassword.getText().toString();
                final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                if(o.isEmpty())oldPassword.setError("must old password");
                else if(n.isEmpty())newPassword.setError("must new password");
                else if(c.isEmpty())conPassword.setError("must Confirm password");
                else if(!(n.equals(c))) conPassword.setError("password must be match");
                else if (!(auth != null && auth.getEmail() != null)) Toast.makeText(getContext(),"ReLogin Required",Toast.LENGTH_SHORT).show();
                else { AuthCredential credential = EmailAuthProvider.getCredential(auth.getEmail(),o);
                       auth.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                auth.updatePassword(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        newPassword.setText("");
                                        oldPassword.setText("");
                                        conPassword.setText("");
                                        Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "something error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            oldPassword.setError("wrong password");
                        }
                    });
                }
            }
        });
        return root;
    }
}
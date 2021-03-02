package com.example.itifighter.ui.change;

import android.app.ProgressDialog;
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

    private ProgressDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_menu_change, container, false);
        final EditText oldPassword = root.findViewById(R.id.OldId);
        final EditText newPassword = root.findViewById(R.id.NewId);
        final EditText conPassword = root.findViewById(R.id.ConId);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        setDialogMessage();
        Button reset = root.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final String old = oldPassword.getText().toString();
                final String newP = newPassword.getText().toString();
                final String confirm = conPassword.getText().toString();
                dialog.dismiss();
                if(old.isEmpty()){
                    oldPassword.setError("must old password");
                }
                else if(newP.isEmpty()){
                    newPassword.setError("must new password");
                }
                else if(confirm.isEmpty()){
                    conPassword.setError("must Confirm password");
                }
                else if(!(newP.equals(confirm))) {
                    conPassword.setError("password must be match");
                }
                else if (!(auth != null && auth.getEmail() != null)) {
                    Toast.makeText(getContext(),"ReLogin Required",Toast.LENGTH_SHORT).show();
                }
                else { AuthCredential credential = EmailAuthProvider.getCredential(auth.getEmail(),old);
                dialog.show();
                       auth.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                auth.updatePassword(newP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        newPassword.setText("");
                                        oldPassword.setText("");
                                        conPassword.setText("");
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "something error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            oldPassword.setError("wrong password");
                        }
                    });
                }
            }
        });
        return root;
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
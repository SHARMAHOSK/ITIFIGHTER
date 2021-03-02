package com.example.itifighter.ui.chat;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itifighter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatFragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = "ChatroomActivity";

    //widgets
    private final Chatroom mChatroom = new Chatroom("bhosk","123456");
    private EditText mMessage;

    //vars
    private ListenerRegistration mChatMessageEventListener, mUserListEventListener;
    private RecyclerView mChatMessageRecyclerView;
    private ChatMessageRecyclerAdapter mChatMessageRecyclerAdapter;
    private FirebaseFirestore mDb;
    private final ArrayList<ChatMessage> mMessages = new ArrayList<>();
    private final Set<String> mMessageIds = new HashSet<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private String email,name,id;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_chat, container, false);
        mMessage = root.findViewById(R.id.input_message);
        mChatMessageRecyclerView = root.findViewById(R.id.chatmessage_recycler_view);
        root.findViewById(R.id.checkmark).setOnClickListener(this);
        mDb = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DocumentReference reference = mDb.collection("users").document(user.getUid());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                email = documentSnapshot.getString("Email");
                name = (documentSnapshot.getString("Name"));
                id = user.getUid();
                }
                });
        initChatroomRecyclerView();
        getChatroomUsers();
        return root;
    }

    private void getChatMessages(){

        CollectionReference messagesRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom.getChatroom_id())
                .collection(getString(R.string.collection_chat_messages));
        mChatMessageEventListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }
                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ChatMessage message = doc.toObject(ChatMessage.class);
                                if(!mMessageIds.contains(message.getMessage_id())){
                                    mMessageIds.add(message.getMessage_id());
                                    mMessages.add(message);
                                    mChatMessageRecyclerView.scrollToPosition(mMessages.size() - 1);
                                }

                            }
                            mChatMessageRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getChatroomUsers(){

        CollectionReference usersRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom.getChatroom_id())
                .collection(getString(R.string.collection_chatroom_user_list));

        mUserListEventListener = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){

                            // Clear the list and add all the users again
                            mUserList.clear();
                            mUserList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                User user = doc.toObject(User.class);
                                mUserList.add(user);
                            }

                            Log.d(TAG, "onEvent: user list size: " + mUserList.size());
                        }
                    }
                });
    }

    private void initChatroomRecyclerView(){
        mChatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(mMessages, getContext());
        mChatMessageRecyclerView.setAdapter(mChatMessageRecyclerAdapter);
        mChatMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatMessageRecyclerView.setVerticalScrollBarEnabled(true);

        mChatMessageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, final int top, int right, final int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mChatMessageRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mMessages.size() > 0){
                              mChatMessageRecyclerView.scrollToPosition(
                                        Objects.requireNonNull(mChatMessageRecyclerView.getAdapter()).getItemCount() - 1);
                            }

                        }
                    }, 100);
                }
            }
        });

    }
    
    private void insertNewMessage(){
        String message = mMessage.getText().toString();
        if(!message.equals("")){
            message = message.replaceAll(Objects.requireNonNull(System.getProperty("line.separator")), "");

            DocumentReference newMessageDoc = mDb
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom.getChatroom_id())
                    .collection(getString(R.string.collection_chat_messages))
                    .document();

            ChatMessage newChatMessage = new ChatMessage();
            newChatMessage.setMessage(message);
            newChatMessage.setMessage_id(newMessageDoc.getId());

            User user = new User(email,id,name);
            Log.d(TAG, "insertNewMessage: retrieved user client: " + user.toString());
            newChatMessage.setUser(user);
            newMessageDoc.set(newChatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        clearMessage();
                    }
                }
            });
        }
    }

    private void clearMessage(){
        mMessage.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        getChatMessages();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChatMessageEventListener != null){
            mChatMessageEventListener.remove();
        }
        if(mUserListEventListener != null){
            mUserListEventListener.remove();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkmark) {
            insertNewMessage();
        }
    }

}


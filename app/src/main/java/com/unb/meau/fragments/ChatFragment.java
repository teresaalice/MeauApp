package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomChatFirestoreRecyclerAdapter;
import com.unb.meau.objects.Message;

import java.util.Date;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    String chatId;
    String userId;

    EditText text_message;

    FloatingActionButton button_send;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = rootView.findViewById(R.id.messagesListRecyclerView);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        text_message = rootView.findViewById(R.id.text_message);
        button_send = rootView.findViewById(R.id.button_send);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            chatId = bundle.getString("chat");
            userId = bundle.getString("user");
        }

        getMessages();

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: enviar mensagem");
                sendMessage();
            }
        });

        return rootView;
    }

    private void getMessages() {
        Query query = db.collection("chats").document(chatId).collection("messages").orderBy("date");

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        adapter = new CustomChatFirestoreRecyclerAdapter(options, currentUser.getUid());
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    private void sendMessage() {
        MainActivity.hideKeyboard(getActivity());

        Date date = new Date();

        String messageText = text_message.getText().toString();

        if (messageText.isEmpty())
            return;

        text_message.setText("");

        Message message = new Message(currentUser.getDisplayName(), userId, messageText, date);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + task.getResult().getId());
                        } else {
                            Log.w(TAG, "onComplete: Error adding message", task.getException());
                            Toast.makeText(getActivity(), "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("chats")
                .document(chatId)
                .update("lastMessageDate", date);

        db.collection("chats")
                .document(chatId)
                .update("lastMessage", messageText);

        HashMap<String, Boolean> visualized = new HashMap<>();
        visualized.put(userId, false);
        visualized.put(currentUser.getUid(), true);

        db.collection("chats")
                .document(chatId)
                .update("visualized", visualized);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

        ((MainActivity) getActivity()).setActionBarTitle("Chat");

        ((MainActivity) getActivity()).setActionBarTheme("Verde");

        ((MainActivity) getActivity()).menuItemName = "more";
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

        db.collection("chats")
                .document(chatId)
                .update("visualized." + currentUser.getUid(), true);

        ((MainActivity) getActivity()).menuItemName = "";
        getActivity().invalidateOptionsMenu();


    }
}
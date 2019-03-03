package com.unb.meau.fragments;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    Boolean blocked;

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
            blocked = bundle.getBoolean("blocked");
        }

        if (blocked)
            disableChat();

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

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

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

    private void removeChat() {
        Log.d(TAG, "removeChat: Removendo chat");
        db.collection("chats").document(chatId).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Chat removed");
                            Toast.makeText(getActivity(), "Chat removido com sucesso", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        } else {
                            Log.w(TAG, "onComplete: Error removing chat", task.getException());
                            Toast.makeText(getActivity(), "Erro ao remover chat", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void blockUser() {
        Log.d(TAG, "removeChat: Bloqueando user");
        db.collection("chats").document(chatId).update("blocked", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: User blocked");
                            Toast.makeText(getActivity(), "Usuário bloqueado com sucesso", Toast.LENGTH_SHORT).show();
                            disableChat();
                        } else {
                            Log.w(TAG, "onComplete: Error blocking user", task.getException());
                            Toast.makeText(getActivity(), "Erro ao bloquear usuário", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openUserProfile() {
        Log.d(TAG, "removeChat: Opening user profile");
    }

    private void disableChat() {
        button_send.setEnabled(false);
        text_message.setEnabled(false);
        text_message.setText("Chat bloqueado");
        text_message.setTypeface(null, Typeface.ITALIC);
        text_message.setGravity(Gravity.CENTER);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

        ((MainActivity) getActivity()).setActionBarTitle("Chat");

        ((MainActivity) getActivity()).setActionBarTheme("Verde");

        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

        db.collection("chats")
                .document(chatId)
                .update("visualized." + currentUser.getUid(), true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_more).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: more");

        int id = item.getItemId();

        if (id == R.id.action_more) {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            View mView = getLayoutInflater().inflate(R.layout.fragment_chat_dialog, null);
            TextView remover = mView.findViewById(R.id.remover_contato);
            TextView bloquear = mView.findViewById(R.id.bloquear_contato);
            TextView perfil = mView.findViewById(R.id.ver_perfil);
            Button cancelar = mView.findViewById(R.id.button_cancelar);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);

            dialog.show();

            remover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: remover");
                    removeChat();
                }
            });

            bloquear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: bloquear");
                    blockUser();
                }
            });

            perfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: perfil");
                    ((MainActivity) getActivity()).showPerfilUsuarioFragment(userId);
                }
            });

            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
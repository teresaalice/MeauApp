package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomChatListFirestoreRecyclerAdapter;
import com.unb.meau.objects.Chat;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListChatFragment extends Fragment implements CustomChatListFirestoreRecyclerAdapter.ListChatClickListener {

    private static final String TAG = "ListChatFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    Button finalizar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = rootView.findViewById(R.id.chatListRecyclerView);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        finalizar = rootView.findViewById(R.id.button_finalizar);
        getChatList();

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: finalizar um processo");

                FinalizarProcessoFragment finalizarProcessoFragment= new FinalizarProcessoFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, finalizarProcessoFragment)
                        .addToBackStack("FINALIZAR_PROCESSO_TAG")
                        .commit();
            }
        });

        return rootView;
    }

    private void getChatList() {

        Query query = db.collection("chats").whereEqualTo("users." + currentUser.getUid(), true);

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        adapter = new CustomChatListFirestoreRecyclerAdapter(this, options, currentUser.getUid(), this);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onListChatClick(Chat chat) {
        Log.d(TAG, "onListChatClick: " + chat.getUsersNames());

        String userUid = "";

        for (String user : chat.getUsers().keySet()) {
            if (!user.equals(currentUser.getUid())) {
                userUid = user;
                break;
            }
        }

        final String finalUserUid = userUid;
        db.collection("chats")
                .whereEqualTo("users." + currentUser.getUid(), true)
                .whereEqualTo("users." + userUid, true)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Success");
                            String chatId = task.getResult().getDocuments().get(0).getId();
                            Log.d(TAG, "onComplete: Chat ID: " + chatId);

                            ChatFragment chatFragment = new ChatFragment();

                            Bundle args = new Bundle();
                            args.putString("chat", chatId);
                            args.putString("user", finalUserUid);
                            chatFragment.setArguments(args);

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, chatFragment)
                                    .addToBackStack(null)
                                    .commit();

                        } else {
                            Log.w(TAG, "onComplete: Error", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle("Chat");

        ((MainActivity) getActivity()).setActionBarTheme("Verde");

        ((MainActivity) getActivity()).menuItemName = "search";
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

        ((MainActivity) getActivity()).menuItemName = "";
        getActivity().invalidateOptionsMenu();
    }
}
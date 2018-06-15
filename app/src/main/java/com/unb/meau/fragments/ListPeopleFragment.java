package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomPeopleFirestoreRecyclerAdapter;
import com.unb.meau.objects.Chat;
import com.unb.meau.objects.User;

import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListPeopleFragment extends Fragment implements CustomPeopleFirestoreRecyclerAdapter.ListUserClickListener {

    private static final String TAG = "ListPeopleFragment";

    RecyclerView mRecyclerView;
    GridLayoutManager gridLayoutManager;
    String animalId;
    FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    Button button_chat;
    private String chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_users_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        mRecyclerView = rootView.findViewById(R.id.usersListRecyclerView);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        button_chat = rootView.findViewById(R.id.button_chat);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("animalId") != null) {
            animalId = bundle.getString("animalId");
        } else {
            Log.d(TAG, "onCreateView: bundle null");
            return rootView;
        }

        getInterestedList();

        button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showListChatFragment();
            }
        });

        return rootView;
    }

    private void getInterestedList() {
        Query query;

        query = db.collection("users").whereEqualTo("interesses." + animalId, true);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new CustomPeopleFirestoreRecyclerAdapter(this, options, this);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onListUserClick(User user) {
        Log.d(TAG, "onClick: " + user.getNome());

//        PerfilUserFragment perfilUserFragment = new PerfilUserFragment();
//
//        Bundle args = new Bundle();
//        args.putString("email", user.getEmail());
//        perfilUserFragment.setArguments(args);
//
//        getFragmentManager().beginTransaction()
//                .replace(R.id.content_frame, perfilUserFragment)
//                .addToBackStack("LIST_PERFIL_USER_TAG")
//                .commit();

        Chat chat = new Chat();

        HashMap<String, Boolean> users = new HashMap<>();
        users.put(currentUser.getUid(), true);
        users.put(user.getUid(), true);
        chat.setUsers(users);

        HashMap<String, String> usersNames = new HashMap<>();
        usersNames.put(currentUser.getUid(), currentUser.getDisplayName());
        usersNames.put(user.getUid(), user.getNome());
        chat.setUsersNames(usersNames);

        HashMap<String, String> photos = new HashMap<>();
        photos.put(currentUser.getUid(), currentUser.getPhotoUrl().toString());
        photos.put(user.getUid(), user.getFoto());
        chat.setPhotos(photos);

        HashMap<String, Boolean> visualized = new HashMap<>();
        visualized.put(currentUser.getUid(), false);
        visualized.put(user.getUid(), false);
        chat.setVisualized(visualized);

        chatId = currentUser.getDisplayName() + "_" + user.getNome();

        db.collection("chats").document(chatId)
                .set(chat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + chatId);
                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                            Toast.makeText(getActivity(), "Erro ao criar o chat", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle("Interessados");

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
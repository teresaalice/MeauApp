package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomPeopleFirestoreRecyclerAdapter;
import com.unb.meau.objects.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListPeopleFragment extends Fragment implements CustomPeopleFirestoreRecyclerAdapter.ListUserClickListener {

    private static final String TAG = "ListFragment";

    RecyclerView mRecyclerView;
    GridLayoutManager gridLayoutManager;
    String animalId;
    FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(gridLayoutManager);
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
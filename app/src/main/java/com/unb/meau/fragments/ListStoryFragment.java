package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomStoryFirestoreRecyclerAdapter;
import com.unb.meau.objects.Story;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListStoryFragment extends Fragment implements CustomStoryFirestoreRecyclerAdapter.ListStoryClickListener {

    private static final String TAG = "ListStoryFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = rootView.findViewById(R.id.listRecyclerView);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("uid") != null) {
            uid = bundle.getString("uid");
        }

        getStoryList();

        return rootView;
    }

    private void getStoryList() {
        Query query;

        if (uid == null || uid.isEmpty())
            query = db.collection("story");
        else
            query = db.collection("story").whereEqualTo("userId", uid);

        FirestoreRecyclerOptions<Story> options = new FirestoreRecyclerOptions.Builder<Story>()
                .setQuery(query, Story.class)
                .build();

        adapter = new CustomStoryFirestoreRecyclerAdapter(this, options, this);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onListStoryClick(Story story) {
        Log.d(TAG, "onClick: " + story.getNome());
        ((MainActivity) getActivity()).showHistoriaFragment(story.getStoryId(), story.getNome());
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle("Histórias de adoção");
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
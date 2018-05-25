package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomFirestoreRecyclerAdapter;
import com.unb.meau.objects.Animal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListFragment extends Fragment implements CustomFirestoreRecyclerAdapter.ListAnimalClickListener {

    private static final String TAG = "ListFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    String acao;
    String uid;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("acao") != null) {
            Log.d(TAG, "onCreateView: Listar pets: " + bundle.getString("acao"));
            acao = bundle.getString("acao");
            if (acao.equals("Meus Pets")) {
                uid = bundle.getString("uid");
                Log.d(TAG, "onCreateView: uid: " + uid);
            }
        } else {
            Log.d(TAG, "onCreateView: bundle null");
            acao = "Animais";
        }

        getAnimalList();

        return rootView;
    }

    private void getAnimalList() {
        Query query;

        switch (acao) {
            case "Adotar":
                query = db.collection("animals").whereEqualTo("cadastro_adocao", true);
                break;
            case "Apadrinhar":
                query = db.collection("animals").whereEqualTo("cadastro_apadrinhar", true);
                break;
            case "Ajudar":
                query = db.collection("animals").whereEqualTo("cadastro_ajuda", true);
                break;
            case "Meus Pets":
                query = db.collection("animals").whereEqualTo("dono", uid);
                break;
            default:
                query = db.collection("animals");
                break;
        }

        FirestoreRecyclerOptions<Animal> options = new FirestoreRecyclerOptions.Builder<Animal>()
                .setQuery(query, Animal.class)
                .build();

        adapter = new CustomFirestoreRecyclerAdapter(this, options, acao, this);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onListAnimalClick(Animal animal) {
        Log.d(TAG, "onClick: " + animal.getNome());

        PerfilAnimalFragment perfilAnimalFragment = new PerfilAnimalFragment();

        Bundle args = new Bundle();
        args.putString("nome", animal.getNome());
        args.putString("dono", animal.getDono());
        args.putString("acao", acao);
        perfilAnimalFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, perfilAnimalFragment)
                .addToBackStack("LIST_PERFIL_ANIMAL_TAG")
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle(acao);

        if (acao.equals("Meus Pets")) {
            ((MainActivity) getActivity()).setActionBarTheme("Verde");
        } else {
            ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomAnimalsFirestoreRecyclerAdapter;
import com.unb.meau.objects.Animal;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListFragment extends Fragment implements CustomAnimalsFirestoreRecyclerAdapter.ListAnimalClickListener {

    private static final String TAG = "ListFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    String acao;
    String uid;
    FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = rootView.findViewById(R.id.listRecyclerView);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("acao") != null) {
            Log.d(TAG, "onCreateView: Listar pets: " + bundle.getString("acao"));
            acao = bundle.getString("acao");
            if (acao.equals("Meus Pets") || acao.equals("Favoritos") || acao.equals("Filtro")) {
                uid = bundle.getString("userID");
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
            case "Favoritos":
                query = db.collection("animals").whereEqualTo("favoritos." + currentUser.getUid(), true);
                break;
            case "Filtro":
                query = db.collection("animals").whereEqualTo("filteredBy." + currentUser.getUid(), true);
                break;
            default:
                query = db.collection("animals");
                break;
        }

        FirestoreRecyclerOptions<Animal> options = new FirestoreRecyclerOptions.Builder<Animal>()
                .setQuery(query, Animal.class)
                .build();

        adapter = new CustomAnimalsFirestoreRecyclerAdapter(this, options, acao, currentUser.getUid(), this);
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
    public void onListAnimalFavClick(Animal animal, final Boolean favoritar) {
        Log.d(TAG, "onClick: " + animal.getNome() + " favoritado");

        db.collection("animals")
                .whereEqualTo("nome", animal.getNome())
                .whereEqualTo("dono", animal.getDono())
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                String animalId = task.getResult().getDocuments().get(0).getId();

                                db.collection("animals").document(animalId)
                                        .update("favoritos." + currentUser.getUid(), (favoritar) ? true : FieldValue.delete())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Document updated");
                                                    if (favoritar)
                                                        Toast.makeText(getActivity(), "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(getActivity(), "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.w(TAG, "Error updating document", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "onComplete: Animal not found", task.getException());
                            Toast.makeText(getActivity(), "Animal não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle(acao);

        if (acao.equals("Meus Pets") || acao.equals("Favoritos")) {
            ((MainActivity) getActivity()).setActionBarTheme("Verde");
        } else {
            ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_search).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: share");

        int id = item.getItemId();

        if (id == R.id.action_search)
            ((MainActivity) getActivity()).showFilterFragment();

        return super.onOptionsItemSelected(item);
    }
}
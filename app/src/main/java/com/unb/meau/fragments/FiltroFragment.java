package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Animal;

import java.util.List;

public class FiltroFragment extends Fragment {

    private static final String TAG = "CadastroAnimalFragment";
    ProgressBar mProgressBar;

    ToggleButton button_adotar;
    ToggleButton button_ajudar;
    ToggleButton button_apadrinhar;
    ToggleButton button_cachorro;
    ToggleButton button_gato;
    ToggleButton button_macho;
    ToggleButton button_femea;
    ToggleButton button_filhote;
    ToggleButton button_adulto;
    ToggleButton button_idoso;
    ToggleButton button_pequeno;
    ToggleButton button_medio;
    ToggleButton button_grande;

    EditText estado;
    EditText cidade;
    EditText nome;

    Button pesquisar;

    FirebaseFirestore db;
    FirebaseUser currentUser;

    Integer results;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.filtro, container, false);

        mProgressBar = view.findViewById(R.id.progress_bar);

        button_adotar = view.findViewById(R.id.button_adotar);
        button_ajudar = view.findViewById(R.id.button_ajudar);
        button_apadrinhar = view.findViewById(R.id.button_apadrinhar);
        button_cachorro = view.findViewById(R.id.button_cachorro);
        button_gato = view.findViewById(R.id.button_gato);
        button_macho = view.findViewById(R.id.button_macho);
        button_femea = view.findViewById(R.id.button_femea);
        button_filhote = view.findViewById(R.id.button_filhote);
        button_adulto = view.findViewById(R.id.button_adulto);
        button_idoso = view.findViewById(R.id.button_idoso);
        button_pequeno = view.findViewById(R.id.button_pequeno);
        button_medio = view.findViewById(R.id.button_medio);
        button_grande = view.findViewById(R.id.button_grande);
        estado = view.findViewById(R.id.estado);
        cidade = view.findViewById(R.id.cidade);
        nome = view.findViewById(R.id.nome);

        pesquisar = view.findViewById(R.id.button_pesquisar);

        db = FirebaseFirestore.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        pesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: pesquisar");
                makeQuery();
//                showProgressDialog();
            }
        });

        return view;
    }

    private void makeQuery() {

        CollectionReference animalsRef = db.collection("animals");

        showProgressDialog();

        results = 0;

        db.collection("animals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Queried " + task.getResult().getDocuments().size() + " documents");

                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        Animal animal = documentSnapshot.toObject(Animal.class);

                        if (queryCadastro(animal) && queryEspecie(animal) && querySexo(animal) && queryIdade(animal) && queryPorte(animal) && queryLocalizacao(animal) && queryNome(animal)) {
                            batch.update(documentSnapshot.getReference(), "filteredBy." + currentUser.getUid(), true);
                            results++;
                        }
                    }

                    if (results > 0) {

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: filter created");
                                    hideProgressDialog();
                                    ((MainActivity) getActivity()).showListarAnimaisFragment ("Filtro");
                                } else {
                                    hideProgressDialog();
                                    Log.w(TAG, "onComplete: Error updating animals", task.getException());
                                }
                            }
                        });
                    } else {
                        hideProgressDialog();
                        Log.d(TAG, "onComplete: No results");

                        FiltroErroFragment filtroErroFragment = new FiltroErroFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, filtroErroFragment, MainActivity.FRAGMENT_FILTRO_ERRO_TAG)
                                .addToBackStack("FILTRO_TAG")
                                .commit();
                    }
                } else {
                    hideProgressDialog();
                    Log.w(TAG, "onComplete: Error getting animals", task.getException());
                }
            }
        });
    }

    private Boolean queryLocalizacao(Animal animal) {

        if (estado.getText().toString().isEmpty() && cidade.getText().toString().isEmpty())
            return true;

        if (!estado.getText().toString().isEmpty() && animal.getLocalizacao().toLowerCase().contains(estado.getText().toString().toLowerCase()))
            return true;

        if (!cidade.getText().toString().isEmpty() && animal.getLocalizacao().toLowerCase().contains(cidade.getText().toString().toLowerCase()))
            return true;

        return false;
    }

    private Boolean queryNome(Animal animal) {

        if (nome.getText().toString().isEmpty())
            return true;

        if (animal.getNome().toLowerCase().contains(nome.getText().toString().toLowerCase()))
            return true;

        // TODO: 5/31/18 Remover animal.getDono_nome() != null após refazer cadastros
        if (animal.getDono_nome() != null && animal.getDono_nome().toLowerCase().contains(nome.getText().toString().toLowerCase()))
            return true;

        return false;
    }

    private Boolean queryCadastro(Animal animal) {

        if (!button_adotar.isChecked() && !button_ajudar.isChecked() && !button_apadrinhar.isChecked())
            return true;

        if (button_adotar.isChecked() && animal.getCadastro_adocao())
            return true;

        if (button_ajudar.isChecked() && animal.getCadastro_ajuda())
            return true;

        if (button_apadrinhar.isChecked() && animal.getCadastro_apadrinhar())
            return true;

        return false;

    }

    private Boolean queryEspecie(Animal animal) {

        if (!button_cachorro.isChecked() && !button_gato.isChecked())
            return true;

        if (button_cachorro.isChecked() && animal.getEspecie().equals("Cachorro"))
            return true;

        if (button_gato.isChecked() && animal.getEspecie().equals("Gato"))
            return true;

        return false;

    }

    private Boolean querySexo(Animal animal) {

        if (!button_macho.isChecked() && !button_femea.isChecked())
            return true;

        if (button_macho.isChecked() && animal.getSexo().equals("Macho"))
            return true;

        if (button_femea.isChecked() && animal.getSexo().equals("Fêmea"))
            return true;

        return false;
    }

    private Boolean queryIdade(Animal animal) {

        if (!button_filhote.isChecked() && !button_adulto.isChecked() && !button_idoso.isChecked())
            return true;

        if (button_filhote.isChecked() && animal.getIdade().equals("Filhote"))
            return true;

        if (button_adulto.isChecked() && animal.getIdade().equals("Adulto"))
            return true;

        if (button_idoso.isChecked() && animal.getIdade().equals("Idoso"))
            return true;

        return false;
    }

    private Boolean queryPorte(Animal animal) {

        if (!button_pequeno.isChecked() && !button_medio.isChecked() && !button_grande.isChecked())
            return true;

        if (button_pequeno.isChecked() && animal.getPorte().equals("Pequeno"))
            return true;

        if (button_medio.isChecked() && animal.getPorte().equals("Médio"))
            return true;

        if (button_grande.isChecked() && animal.getPorte().equals("Grande"))
            return true;

        return false;
    }

    private void clearFilters() {

        pesquisar.setEnabled(false);

        db.collection("animals")
                .whereEqualTo("filteredBy." + currentUser.getUid(), true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Queried " + task.getResult().getDocuments().size() + " documents");
                            List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                            WriteBatch batch = db.batch();

                            for (DocumentSnapshot documentSnapshot : documentSnapshotList)
                                batch.update(documentSnapshot.getReference(), "filteredBy." + currentUser.getUid(), FieldValue.delete());

                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: user filters cleared");
                                        pesquisar.setEnabled(true);
                                    } else {
                                        Log.w(TAG, "onComplete: Error clearing user filter", task.getException());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Filtrar pesquisa");
        ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
        clearFilters();
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}
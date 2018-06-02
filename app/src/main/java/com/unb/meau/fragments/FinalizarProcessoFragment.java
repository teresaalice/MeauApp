package com.unb.meau.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Process;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FinalizarProcessoFragment extends Fragment implements RadioButton.OnCheckedChangeListener {

    private static final String TAG = "FinalizarFragment";

    LinearLayoutManager linearLayoutManager;
    FirebaseUser currentUser;
    private FirebaseFirestore db;

    String animalId;
    String userId;
    String acao;

    List<String> animals;
    List<String> animalsId;
    List<String> users;
    List<String> usersId;
    List<String> categorias;
    List<Process> processes;

    RadioButton[] rb;
    RadioButton[] rbusers;
    RadioButton[] rbcategorias;

    RadioGroup radio_group_animals;
    RadioGroup radio_group_process;
    RadioGroup radio_group_user;
    Button button_finalizar_processo;
    ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_finalizar_processo, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        radio_group_animals = rootView.findViewById(R.id.radio_group_animals);
        radio_group_process = rootView.findViewById(R.id.radio_group_process);
        radio_group_user = rootView.findViewById(R.id.radio_group_user);
        button_finalizar_processo = rootView.findViewById(R.id.button_finalizar_processo);
        mProgressBar = rootView.findViewById(R.id.progress_bar);

        processes = new ArrayList<>();

        showProgressDialog();

        db.collection("processes")
                .whereEqualTo("dono", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: " + task.getResult().getDocuments().size() + " pets returned");
                            for (DocumentSnapshot process : task.getResult().getDocuments())
                                processes.add(process.toObject(Process.class));
                            createRadioButtons();
//                            createAnimalsRadioButtons();
                        } else {
                            Log.w(TAG, "onComplete: Error", task.getException());
                        }
                    }
                });

        button_finalizar_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: finalizar processo");

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.fragment_finalizar_processo_dialog, null);
                Button concordar = mView.findViewById(R.id.button_concordar);
                Button cancelar = mView.findViewById(R.id.button_cancelar);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                concordar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: concordar");
                        finalizarProcesso();
                        deletarPet();
                        removerPetReferences();
                    }
                });
            }
        });

        return rootView;
    }

    private void removerPetReferences() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            WriteBatch batch = db.batch();
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                batch.update(documentSnapshot.getReference(), "interesses", FieldValue.delete());
                            }
                            batch.commit();
                        } else {
                            Log.w(TAG, "onComplete: Error getting users", task.getException());
                        }
                    }
                });
    }

    private void deletarPet() {
        db.collection("animals").document(animalId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Animal removido");
                        } else {
                            Log.w(TAG, "onComplete: Erro ao remover animal", task.getException());
                        }
                    }
                });
    }

    private void finalizarProcesso() {
        db.collection("processes")
                .whereEqualTo("animal", animalId)
                .whereEqualTo("interessado", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getDocuments().get(0).getId();

                            db.collection("processes").document(id)
                                    .update("estagio", "finalizado");

                        } else {
                            Log.w(TAG, "onComplete: Error", task.getException());
                        }
                    }
                });
    }

    private void createRadioButtons() {
        animals = new ArrayList<>();
        animalsId = new ArrayList<>();

        users = new ArrayList<>();
        usersId = new ArrayList<>();

        categorias = new ArrayList<>();

        for (Process process : processes) {
            if (!animalsId.contains(process.getAnimal())) {
                animals.add(process.getAnimalNome());
                animalsId.add(process.getAnimal());
            }

            if (!usersId.contains(process.getInteressado())) {
                users.add(process.getInteressadoNome());
                usersId.add(process.getInteressado());
            }
        }

        rb = new RadioButton[animals.size()];
        for (int i = 0; i < animals.size(); i++) {
            rb[i] = new RadioButton(getActivity());
            rb[i].setText(animals.get(i));
            rb[i].setId(1000 + i);
            rb[i].setOnCheckedChangeListener(this);
            radio_group_animals.addView(rb[i]);
        }

        rbusers = new RadioButton[users.size()];
        for (int i = 0; i < users.size(); i++) {
            rbusers[i] = new RadioButton(getActivity());
            rbusers[i].setText(users.get(i));
            rbusers[i].setId(2000 + i);
            rbusers[i].setOnCheckedChangeListener(this);
            rbusers[i].setEnabled(false);
            radio_group_user.addView(rbusers[i]);
        }

        rbcategorias = new RadioButton[3];
        categorias.add("Adoção");
        categorias.add("Ajuda");
        categorias.add("Apadrinhamento");

        for (int i = 0; i < categorias.size(); i++) {
            rbcategorias[i] = new RadioButton(getActivity());
            rbcategorias[i].setText(categorias.get(i));
            rbcategorias[i].setId(i);
            rbcategorias[i].setOnCheckedChangeListener(this);
            rbcategorias[i].setEnabled(false);
            radio_group_process.addView(rbcategorias[i]);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!isChecked)
            return;

        Log.d(TAG, "onCheckedChanged: " + buttonView.getId());

        if (buttonView.getId() >= 1000 && buttonView.getId() < 2000) {
            int id = buttonView.getId() - 1000;

            Log.d(TAG, "onCheckedChanged: Animal ID: " + id);

            for (RadioButton rb : rbcategorias) {
                rb.setEnabled(false);
            }

            for (RadioButton rb : rbusers) {
                rb.setEnabled(false);
            }

            animalId = animalsId.get(id);

            for (Process process : processes) {
                if (process.getAnimal().equals(animalId)) {
                    switch (process.getAcao()) {
                        case "adoção":
                            rbcategorias[0].setEnabled(true);
                            break;
                        case "ajuda":
                            rbcategorias[1].setEnabled(true);
                            break;
                        case "apadrinhamento":
                            rbcategorias[2].setEnabled(true);
                            break;
                    }
                }
            }

        } else if (buttonView.getId() >= 0 && buttonView.getId() < 3) {
            int id = buttonView.getId();

            Log.d(TAG, "onCheckedChanged: interesse ID: " + id);

            for (RadioButton rb : rbusers) {
                rb.setEnabled(false);
            }

            switch (id) {
                case 0:
                    acao = "adoção";
                    break;
                case 1:
                    acao = "ajuda";
                    break;
                case 2:
                    acao = "apadrinhamento";
                    break;
            }

            for (Process process : processes) {
                if (process.getAnimal().equals(animalId) && process.getAcao().equals(acao)) {
                    for (int i = 0; i < usersId.size(); i++) {
                        if (usersId.get(i).equals(process.getInteressado()))
                            rbusers[i].setEnabled(true);
                    }
                }
            }

        } else if (buttonView.getId() >= 2000 && buttonView.getId() < 3000) {
            int id = buttonView.getId() - 2000;
            Log.d(TAG, "onCheckedChanged: user ID: " + id);
            userId = usersId.get(id);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Finalizar processo");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}
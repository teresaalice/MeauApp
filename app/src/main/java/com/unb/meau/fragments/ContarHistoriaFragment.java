package com.unb.meau.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ContarHistoriaFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ContarHistoriaFragment";
    ProgressBar mProgressBar;

    Button buttonAdicionarFoto;
    Button finalizar;
    TextView data_label;
    ToggleButton buttonAdotante;
    ToggleButton buttonPadrinho;
    ToggleButton buttonDoador;

    ArrayList<String> downloadUrl = new ArrayList<>();
    FirebaseFirestore db;
    Map<String, Object> storyObj;
    View view;
    private int PICK_IMAGE_REQUEST = 1;
    private String storyId;
    private String animalName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contar_historia, container, false);

        finalizar = view.findViewById(R.id.button_contar_historia);
        buttonAdotante = view.findViewById(R.id.button_adotante);
        buttonPadrinho = view.findViewById(R.id.button_padrinho);
        buttonDoador = view.findViewById(R.id.button_doador);
        buttonAdicionarFoto = view.findViewById(R.id.button_adicionar_fotos);
        data_label = view.findViewById(R.id.data_label);
        mProgressBar = view.findViewById(R.id.progress_bar);

        buttonAdotante.setOnCheckedChangeListener(this);
        buttonPadrinho.setOnCheckedChangeListener(this);
        buttonDoador.setOnCheckedChangeListener(this);

        buttonAdicionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Adicionar Foto");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), PICK_IMAGE_REQUEST);
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: finalizar");
                if (!isDataCorrect()) return;
                showProgressDialog();
                addToDatabase();
            }
        });

        return view;
    }

    private boolean isDataCorrect() {

        if (!buttonAdotante.isChecked() & !buttonDoador.isChecked() & !buttonPadrinho.isChecked()) {
            Log.d(TAG, "isDataCorrect: User role missing");
            Toast.makeText(getActivity(), "Escolha seu papel na história", Toast.LENGTH_SHORT).show();
            return false;
        }

        EditText nome_do_animal = view.findViewById(R.id.nome_do_animal);
        if (nome_do_animal.getText().toString().isEmpty()) {
            Log.d(TAG, "isDataCorrect: Animal name missing");
            Toast.makeText(getActivity(), "Escreva o nome do animal", Toast.LENGTH_SHORT).show();
            return false;
        }

        EditText historia = view.findViewById(R.id.historia);
        if (historia.getText().toString().isEmpty()) {
            Log.d(TAG, "isDataCorrect: Story missing");
            Toast.makeText(getActivity(), "Escreva uma história", Toast.LENGTH_SHORT).show();
            return false;
        }

        EditText data = view.findViewById(R.id.data);
        if (data.getText().toString().isEmpty()) {
            Log.d(TAG, "isDataCorrect: Date missing");
            Toast.makeText(getActivity(), "Insira uma data", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Contar História");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!buttonAdotante.isChecked() && !buttonDoador.isChecked() && !buttonPadrinho.isChecked()) {
            data_label.setText("Data");
            return;
        }

        switch (buttonView.getId()) {

            case R.id.button_adotante:
                if (isChecked) {
                    data_label.setText("Data da adoção");
                    buttonDoador.setChecked(false);
                    buttonPadrinho.setChecked(false);
                }
                break;

            case R.id.button_doador:
                if (isChecked) {
                    data_label.setText("Data da doação");
                    buttonAdotante.setChecked(false);
                    buttonPadrinho.setChecked(false);
                }
                break;

            case R.id.button_padrinho:
                if (isChecked) {
                    data_label.setText("Data do apadrinhamento");
                    buttonAdotante.setChecked(false);
                    buttonDoador.setChecked(false);
                }
                break;
        }
    }

    private void addToDatabase() {

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        storyObj = new HashMap<>();

        Story story = new Story();

        if (buttonAdotante.isChecked())
            story.setTipo("adocao");
        else if (buttonDoador.isChecked())
            story.setTipo("ajuda");
        else if (buttonPadrinho.isChecked())
            story.setTipo("apadrinhamento");

        final EditText nome_do_animal = view.findViewById(R.id.nome_do_animal);
        animalName = nome_do_animal.getText().toString();
        story.setNome(animalName);

        story.setUser(currentUser.getDisplayName());
        story.setUserId(currentUser.getUid());

        if (downloadUrl.size() > 0) {
            String fotos = TextUtils.join(",", downloadUrl);
            story.setFotos(fotos);
        } else {
            story.setFotos("");
        }

        EditText historia = view.findViewById(R.id.historia);
        story.setHistoria(historia.getText().toString());

        EditText data = view.findViewById(R.id.data);
        story.setData(data.getText().toString());

        storyId = Long.toString(System.currentTimeMillis()) + "_" + nome_do_animal.getText().toString();
        story.setStoryId(storyId);

        showProgressDialog();

        db.collection("story").document(storyId)
                .set(story)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + storyId);

                            ContarHistoriaSucessoFragment contarHistoriaSucessoFragment = new ContarHistoriaSucessoFragment();

                            Bundle args = new Bundle();
                            args.putString("story_id", storyId);
                            args.putString("animal_name", animalName);
                            contarHistoriaSucessoFragment.setArguments(args);

                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, contarHistoriaSucessoFragment, MainActivity.FRAGMENT_CONTAR_HISTORIA_SUCESSO_TAG)
                                    .addToBackStack(null)
                                    .commit();

                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                            Toast.makeText(getActivity(), "Erro ao contar história", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void uploadFile(Uri filePath) {
        finalizar.setEnabled(false);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference imageRef = mStorageRef.child("animals/" + System.currentTimeMillis() + ".jpg");

        Toast.makeText(getActivity(), "Fazendo upload da imagem", Toast.LENGTH_SHORT).show();

        imageRef.putFile(filePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadUrl.add(uri.toString());
                                            Log.d(TAG, "onComplete: Photo uploaded: " + downloadUrl.get(downloadUrl.size() - 1));
                                            Toast.makeText(getActivity(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "onComplete: Error uploading photo", e);
                                            Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.w(TAG, "onComplete: Error uploading photo", task.getException());
                            Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                        }
                        finalizar.setEnabled(true);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Log.d(TAG, "onActivityResult: " + filePath);
            uploadFile(filePath);
        }
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}
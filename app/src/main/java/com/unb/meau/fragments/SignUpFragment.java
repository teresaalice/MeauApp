package com.unb.meau.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.User;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    EditText mEmailEdit;
    EditText mPasswordEdit;
    EditText mPasswordConfirmationEdit;
    EditText mUsername;

    Button buttonAdicionarFoto;
    Button button_signup;

    ProgressBar mProgressBar;

    Boolean providerCompleteMode = false;
    Boolean editMode = false;
    Boolean completed = false;

    FirebaseUser currentUser;
    FirebaseFirestore db;

    Uri downloadUrl;

    String email;
    String password;

    User newUser;
    User user;

    String uid;

    View v;

    private int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_signup, container, false);

        mProgressBar = v.findViewById(R.id.progress_bar);

        button_signup = v.findViewById(R.id.button_signup);

        mEmailEdit = v.findViewById(R.id.email);
        mPasswordEdit = v.findViewById(R.id.senha);
        mPasswordConfirmationEdit = v.findViewById(R.id.senha2);
        mUsername = v.findViewById(R.id.username);
        buttonAdicionarFoto = v.findViewById(R.id.add_photo);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            if (bundle.getBoolean("providerComplete")) {
                providerCompleteMode = true;
                adaptLayout("complete");
            } else if (bundle.getBoolean("edit")) {
                editMode = true;
                getUserAndAdaptLayout();
            }
        }

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

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: button_signup");

                email = mEmailEdit.getText().toString();
                password = mPasswordEdit.getText().toString();
                String username = mUsername.getText().toString();

                if (email.isEmpty() || username.isEmpty()) {
                    Log.d(TAG, "onClick: Enter an email and username");
                    Toast.makeText(getActivity(), "Insira um email e um nome de usuário", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!providerCompleteMode && !editMode) {
                    if (password.isEmpty()) {
                        Log.d(TAG, "onClick: Enter a password");
                        Toast.makeText(getActivity(), "Escolha uma senha", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!password.equals(mPasswordConfirmationEdit.getText().toString())) {
                            Log.d(TAG, "onClick: Senhas diferentes");
                            Toast.makeText(getActivity(), "As senhas não são iguais", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                if (username.contains("@")) {
                    Log.d(TAG, "onClick: Invalid username");
                    return;
                }

                showProgressDialog();

                if (editMode) {
                    storeUserData();
                    return;
                }

                if (providerCompleteMode) {
                    db.collection("users")
                            .whereEqualTo("username", username)
                            .limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getDocuments().size() > 0) {
                                            Log.d(TAG, "onComplete: Username exists");
                                            Toast.makeText(getActivity(), "Nome de usuário em uso", Toast.LENGTH_SHORT).show();
                                            hideProgressDialog();
                                        } else {
                                            Log.d(TAG, "onComplete: valid username");
                                            storeUserData();
                                        }
                                    }
                                }
                            });
                    return;
                }

                db.collection("users")
                        .whereEqualTo("username", username)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        Log.d(TAG, "onComplete: Username exists");
                                        Toast.makeText(getActivity(), "Nome de usuário em uso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d(TAG, "onComplete: valid username");
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d(TAG, "createUserWithEmail:success");
                                                            currentUser = mAuth.getCurrentUser();
                                                            storeUserData();
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                            hideProgressDialog();

                                                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                                Log.d(TAG, "onComplete: User with this email already exist");
                                                                Toast.makeText(getActivity(), "Email já cadastrado", Toast.LENGTH_SHORT).show();
                                                            } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                                                Log.d(TAG, "onComplete: Senha inválida! Ela deve conter ao menos 6 caracteres");
                                                                Toast.makeText(getActivity(), "Senha inválida! Ela deve conter ao menos 6 caracteres", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.w(TAG, "onComplete: Error checking username", task.getException());
                                    Toast.makeText(getActivity(), "Erro ao verificar username", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return v;
    }

    private void getUserAndAdaptLayout() {

        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        user = task.getResult().toObject(User.class);
                        adaptLayout("edit");
                    } else {
                        Log.d(TAG, "onComplete: User not found");
                        Toast.makeText(getActivity(), "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        (getActivity()).onBackPressed();
                    }
                } else {
                    Log.w(TAG, "onComplete: Error getting user", task.getException());
                    Toast.makeText(getActivity(), "Erro ao buscar usuário", Toast.LENGTH_SHORT).show();
                    (getActivity()).onBackPressed();
                }
            }
        });
    }

    private void adaptLayout(String mode) {

        EditText nome;
        EditText telefone;

        switch (mode) {
            case "complete":
                nome = v.findViewById(R.id.nome);
                telefone = v.findViewById(R.id.telefone);

                mEmailEdit.setEnabled(false);
                mPasswordEdit.setVisibility(View.GONE);
                mPasswordConfirmationEdit.setVisibility(View.GONE);

                nome.setText(currentUser.getDisplayName());
                telefone.setText(currentUser.getPhoneNumber());
                mEmailEdit.setText(currentUser.getEmail());
                break;
            case "edit":
                button_signup.setText("Salvar Edições");

                nome = v.findViewById(R.id.nome);
                EditText idade = v.findViewById(R.id.idade);
                EditText email = v.findViewById(R.id.email);
                EditText estado = v.findViewById(R.id.estado);
                EditText cidade = v.findViewById(R.id.cidade);
                EditText endereco = v.findViewById(R.id.endereco);
                telefone = v.findViewById(R.id.telefone);
                TextView info_perfil = v.findViewById(R.id.info_perfil);

                mEmailEdit.setEnabled(false);
                mPasswordEdit.setVisibility(View.GONE);
                mPasswordConfirmationEdit.setVisibility(View.GONE);
                info_perfil.setVisibility(View.GONE);
                mUsername.setVisibility(View.GONE);

                nome.setText(user.getNome());
                idade.setText(user.getIdade().toString());
                email.setText(user.getEmail());
                estado.setText(user.getEstado());
                cidade.setText(user.getCidade());
                endereco.setText(user.getEndereco());
                telefone.setText(user.getTelefone());
                mUsername.setText(user.getUsername());

                mEmailEdit.setText(currentUser.getEmail());
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Cadastro");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }

    private void storeUserData() {

        newUser = new User();

        EditText nomeView = v.findViewById(R.id.nome);
        EditText idadeView = v.findViewById(R.id.idade);
        EditText emailView = v.findViewById(R.id.email);
        EditText estadoView = v.findViewById(R.id.estado);
        EditText cidadeView = v.findViewById(R.id.cidade);
        EditText enderecoView = v.findViewById(R.id.endereco);
        EditText telefoneView = v.findViewById(R.id.telefone);

        newUser.setEmail(emailView.getText().toString());
        newUser.setNome(nomeView.getText().toString());
        newUser.setUsername(mUsername.getText().toString());
        if (!idadeView.getText().toString().isEmpty()) {
            newUser.setIdade(Integer.parseInt(idadeView.getText().toString()));
        }
        if (!estadoView.getText().toString().isEmpty()) {
            newUser.setEstado(estadoView.getText().toString());
        }
        if (!cidadeView.getText().toString().isEmpty()) {
            newUser.setCidade(cidadeView.getText().toString());
        }
        if (!enderecoView.getText().toString().isEmpty()) {
            newUser.setEndereco(enderecoView.getText().toString());
        }
        if (!telefoneView.getText().toString().isEmpty()) {
            newUser.setTelefone(telefoneView.getText().toString());
        }

        newUser.setNotificacoes_chat(true);
        newUser.setNotificacoes_recordacao(true);
        newUser.setNotificacoes_eventos(true);

        newUser.setUid(currentUser.getUid());

        if (downloadUrl != null && !downloadUrl.toString().isEmpty())
            newUser.setFoto(downloadUrl.toString());
        else if (providerCompleteMode || editMode)
            newUser.setFoto(currentUser.getPhotoUrl().toString());

        if (providerCompleteMode || editMode) {

            db.collection("users").document(currentUser.getUid())
                    .set(newUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressDialog();

                            if (!currentUser.getDisplayName().equals(newUser.getNome())) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUser.getNome())
                                        .build();
                                currentUser.updateProfile(profileUpdates);
                            }

                            if (!newUser.getFoto().isEmpty() && downloadUrl != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUrl)
                                        .build();
                                currentUser.updateProfile(profileUpdates);
                            }

                            if (task.isSuccessful()) {
                                Log.d(TAG, "Document added successfully");
                                if (providerCompleteMode)
                                    Toast.makeText(getActivity(), "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                                else if (editMode)
                                    Toast.makeText(getActivity(), "Edição realizada com sucesso", Toast.LENGTH_SHORT).show();

                                ((MainActivity) getActivity()).setDrawerInfo();
                                completed = true;
                                getActivity().onBackPressed();
                            } else {
                                Log.w(TAG, "Error adding document", task.getException());
                                if (providerCompleteMode)
                                    Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(), "Erro ao editar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nomeView.getText().toString())
                .setPhotoUri(downloadUrl)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");

                            db.collection("users").document(currentUser.getUid())
                                    .set(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hideProgressDialog();

                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Document added successfully");
                                                Toast.makeText(getActivity(), "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                                                getActivity().onBackPressed();
                                            } else {
                                                Log.w(TAG, "Error adding document", task.getException());
                                                Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void uploadFile(Uri filePath) {

        button_signup.setEnabled(false);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageRef = mStorageRef.child("users/" + System.currentTimeMillis() + ".jpg");

        Toast.makeText(getActivity(), "Fazendo upload da imagem", Toast.LENGTH_SHORT).show();

        imageRef.putFile(filePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().getDownloadUrl();
                            Log.d(TAG, "onComplete: Photo uploaded: " + downloadUrl);
                            Toast.makeText(getActivity(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "onComplete: Error uploading photo", task.getException());
                            Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                        }
                        button_signup.setEnabled(true);
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

    @Override
    public void onStop() {
        super.onStop();

        if (providerCompleteMode && !completed) {
            Log.d(TAG, "onStop: deleting user");
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: user deleted");
                    } else {
                        Log.w(TAG, "onComplete: error deleting user", task.getException());
                    }
                }
            });
        }
    }
}
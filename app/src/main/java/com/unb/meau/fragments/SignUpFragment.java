package com.unb.meau.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    EditText mEmailEdit;
    EditText mPasswordEdit;
    EditText mPasswordConfirmationEdit;
    EditText mUsername;
    Button buttonAdicionarFoto;
    ProgressBar mProgressBar;
    FirebaseUser user;
    Uri downloadUrl;
    View v;
    private int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();

        mProgressBar = v.findViewById(R.id.progress_bar);

        Button button_signup = v.findViewById(R.id.button_signup);

        mEmailEdit = v.findViewById(R.id.email);
        mPasswordEdit = v.findViewById(R.id.senha);
        mPasswordConfirmationEdit = v.findViewById(R.id.senha2);
        mUsername = v.findViewById(R.id.username);
        buttonAdicionarFoto = v.findViewById(R.id.add_photo);

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

                String email = mEmailEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();
                String username = mUsername.getText().toString();

                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Log.d(TAG, "onClick: Enter an email, username and password");
                    Toast.makeText(getActivity(), "Insira um email, nome de usuário e senha", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "onClick: " + email + " : " + password);

                if (!password.equals(mPasswordConfirmationEdit.getText().toString())) {
                    Log.d(TAG, "onClick: Senhas diferentes");
                    Toast.makeText(getActivity(), "As senhas não são iguais", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.contains("@")) {
                    Log.d(TAG, "onClick: Invalid username");
                    return;
                }

                showProgressDialog();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    user = mAuth.getCurrentUser();
                                    storeUserData();
                                    returnToIntro();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

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
                                hideProgressDialog();
                            }
                        });
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Cadastro");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }

    private void storeUserData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userObj = new HashMap<>();

        EditText nomeView = v.findViewById(R.id.nome);
        EditText idadeView = v.findViewById(R.id.idade);
        EditText emailView = v.findViewById(R.id.email);
        EditText estadoView = v.findViewById(R.id.estado);
        EditText cidadeView = v.findViewById(R.id.cidade);
        EditText enderecoView = v.findViewById(R.id.endereco);
        EditText telefoneView = v.findViewById(R.id.telefone);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nomeView.getText().toString())
                .setPhotoUri(downloadUrl)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

        userObj.put("email", emailView.getText().toString());
        userObj.put("nome", nomeView.getText().toString());
        userObj.put("username", mUsername.getText().toString());
        userObj.put("idade", Integer.parseInt(idadeView.getText().toString()));
        userObj.put("estado", estadoView.getText().toString());
        userObj.put("cidade", cidadeView.getText().toString());
        userObj.put("endereco", enderecoView.getText().toString());
        userObj.put("telefone", telefoneView.getText().toString());

        db.collection("users").document(user.getUid())
                .set(userObj)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Document added successfully");
                            Toast.makeText(getActivity(), "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                            Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void returnToIntro() {
        if (getActivity() != null) {
            getActivity().getFragmentManager().popBackStack();
        }
    }

    private void uploadFile(Uri filePath) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageRef = mStorageRef.child("users/" + System.currentTimeMillis() + ".jpg");

        Toast.makeText(getActivity(), "Fazendo upload da imagem", Toast.LENGTH_SHORT).show();

        imageRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG, "onSuccess: Photo uploaded: " + downloadUrl);
                        Toast.makeText(getActivity(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "onFailure: Error uploading photo");
                        Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
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
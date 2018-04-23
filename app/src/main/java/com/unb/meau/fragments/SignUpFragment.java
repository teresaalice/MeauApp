package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.meau.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    private FirebaseAuth mAuth;

    EditText mEmailEdit;
    EditText mPasswordEdit;
    EditText mPasswordConfirmationEdit;
    EditText mUsername;
    FirebaseUser user;

    View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();

        Button button_signup = v.findViewById(R.id.button_signup);

        mEmailEdit = v.findViewById(R.id.email);
        mPasswordEdit = v.findViewById(R.id.senha);
        mPasswordConfirmationEdit = v.findViewById(R.id.senha2);
        mUsername = v.findViewById(R.id.username);

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: button_signup");

                String email = mEmailEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();
                String username = mUsername.getText().toString();

                if(email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Log.d(TAG, "onClick: Enter an email, username and password");
                    return;
                }

                Log.d(TAG, "onClick: " + email + " : " + password);

                if(!password.equals(mPasswordConfirmationEdit.getText().toString())) {
                    Log.d(TAG, "onClick: Senhas diferentes");
                    return;
                }

                if (username.contains("@")) {
                    Log.d(TAG, "onClick: Invalid username");
                    return;
                }

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
                                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                        Log.d(TAG, "onComplete: Senha inválida! Ela deve conter ao menos 6 caracteres");
                                    }
                                }
                            }
                        });
            }
        });

        return v;
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
//        EditText usernameView = v.findViewById(R.id.username);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nomeView.getText().toString())
//                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
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
                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                        }
                    }
                });
    }

    private void returnToIntro() {
//        IntroFragment introFragment = new IntroFragment();
//        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.content_frame, introFragment);

        if (getActivity() != null) {
            FragmentManager fm = getActivity().getFragmentManager();
            fm.popBackStack();
        }
    }
}

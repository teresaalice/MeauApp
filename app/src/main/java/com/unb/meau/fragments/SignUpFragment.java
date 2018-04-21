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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.unb.meau.R;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    private FirebaseAuth mAuth;

    EditText mEmailEdit;
    EditText mPasswordEdit;
    EditText mPasswordConfirmationEdit;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();

        Button button_signup = v.findViewById(R.id.button_signup);

        mEmailEdit = v.findViewById(R.id.email);
        mPasswordEdit = v.findViewById(R.id.senha);
        mPasswordConfirmationEdit = v.findViewById(R.id.senha2);

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: button_signup");

                String email = mEmailEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    Log.d(TAG, "onClick: Enter an email and password");
                    return;
                }

                Log.d(TAG, "onClick: " + email + " : " + password);

                if(!password.equals(mPasswordConfirmationEdit.getText().toString())) {
                    Log.d(TAG, "onClick: Senhas diferentes");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
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

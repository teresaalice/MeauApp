package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";
    private static final int RC_SIGN_IN = 9001;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText mUsernameEdit;
    EditText mPasswordEdit;
    ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signin, container, false);

        Button button_login = v.findViewById(R.id.button_signin);
        SignInButton button_google = v.findViewById(R.id.login_with_google);
        LoginButton button_facebook = v.findViewById(R.id.login_with_facebook);

        mProgressBar = v.findViewById(R.id.progress_bar);

        button_facebook.setFragment(this);

        mUsernameEdit = v.findViewById(R.id.editTextUsername);
        mPasswordEdit = v.findViewById(R.id.editTextPassword);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth = FirebaseAuth.getInstance();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_login");

                MainActivity.hideKeyboard(getActivity());

                String username = mUsernameEdit.getText().toString();
                final String password = mPasswordEdit.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Log.d(TAG, "onClick: Digite um nome de usuário e senha");
                    Toast.makeText(getActivity(), "Digite um nome de usuário e senha", Toast.LENGTH_SHORT).show();
                } else if (username.contains("@")) {
                    signIn(username, password);
                } else {
                    Query query = db.collection("users").whereEqualTo("username", username).limit(1);
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e == null) {
                                Log.d(TAG, "onEvent: Success");
                                String email;
                                if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                                    Log.d(TAG, "onEvent: queryDocumentSnapshots != null");
                                    email = queryDocumentSnapshots.getDocuments().get(0).getString("email");

                                    if (email != null) {
                                        signIn(email, password);
                                    } else {
                                        Log.d(TAG, "onEvent: Error retrieving email");
                                        Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Log.d(TAG, "onEvent: Email not found");
                                    Toast.makeText(getActivity(), "Nome de usuário não reconhecido", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "onEvent: Query error", e);
                                Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_facebook");
//                signIn();
            }
        });

        button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_google");
                signIn();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        button_facebook.setReadPermissions("email", "public_profile");
        button_facebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Login");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode);

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getActivity(), "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).setDrawerInfo();
                            getActivity().onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getActivity(), "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).setDrawerInfo();
                            getActivity().onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn: " + email + " : " + password);

        if (email.isEmpty() || password.isEmpty()) {
            Log.d(TAG, "signIn: Enter an username and password");
            Toast.makeText(getActivity(), "Digite seu nome de usuário e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText((getActivity()), "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).setDrawerInfo();
                            getActivity().onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Log.d(TAG, "onComplete: There is no user with this email");
                                Toast.makeText(getActivity(), "Usuário não cadastrado", Toast.LENGTH_SHORT).show();

                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.d(TAG, "onComplete: Invalid password");
                                Toast.makeText(getActivity(), "Senha inválida", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}

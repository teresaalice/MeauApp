package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class IntroFragment extends Fragment {

    private static final String TAG = "IntroFragment";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    TextView text_login;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro, container, false);

        ImageView drawer_icon = v.findViewById(R.id.drawer_icon);
        Button button_adotar = v.findViewById(R.id.button_adotar);
        Button button_ajudar = v.findViewById(R.id.button_ajudar);
        Button button_cadastrar_animal = v.findViewById(R.id.button_cadastrar_animal);
        text_login = v.findViewById(R.id.text_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        drawer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClick: drawer_icon");
                ((MainActivity) getActivity()).drawer.openDrawer(Gravity.START);
            }
        });

        button_adotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_adotar");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    signIn();
                } else {
                    Log.d(TAG, "onClick: Adotar Animal");
                    listarAnimais("adotar");
                }
            }
        });

        button_ajudar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_ajudar");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    signIn();
                } else {
                    Log.d(TAG, "onClick: Ajudar Animal");
                    listarAnimais("ajudar");
                }
            }
        });

        button_cadastrar_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_cadastrar_animal");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    signIn();
                } else {
                    Log.d(TAG, "onClick: Cadastrar Animal");
                    cadastrarAnimal();
                }
            }
        });

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: text_login");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    login();
                } else {
                    Log.d(TAG, "onClick: Logging out");
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                }
                updateLoginButton();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        ((MainActivity)getActivity()).enterFullScreen();
        updateLoginButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).exitFullScreen();
    }

    private void updateLoginButton() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "updateLoginButton: User not logged");
            text_login.setText(R.string.login);
        } else {
            text_login.setText(R.string.logout);
            Log.d(TAG, "updateLoginButton: User " + currentUser.getEmail() + " logged");
//            Log.d(TAG, "onCreateView: DisplayName: " + currentUser.getDisplayName() +
//                    " ProviderId: " + currentUser.getProviderId() +
//                    " Uid: " + currentUser.getUid());
        }
    }

    private void login() {
        LoginFragment fragment = new LoginFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void signIn() {
        SignInFragment fragment = new SignInFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void cadastrarAnimal() {
        CadastroAnimalFragment fragment = new CadastroAnimalFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void listarAnimais(String acao) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putString("acao", acao);
        fragment.setArguments(args);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
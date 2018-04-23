package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
                    adotarAnimal();
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
        Log.d(TAG, "onStart: entered");
        ((MainActivity)getActivity()).enterFullScreen();

        updateLoginButton();

        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: entered");
        ((MainActivity)getActivity()).exitFullScreen();
        super.onPause();
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
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, loginFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void signIn() {
        SignInFragment signInFragment = new SignInFragment();
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, signInFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void cadastrarAnimal() {
        CadastroAnimalFragment cadastroAnimalFragment = new CadastroAnimalFragment();
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, cadastroAnimalFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void adotarAnimal() {
        ListFragment listFragment = new ListFragment();
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, listFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class IntroFragment extends Fragment {

    private static final String TAG = "IntroFragment";
    TextView text_login;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro, container, false);

        toolbar = getActivity().findViewById(R.id.toolbar);

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
                ((MainActivity) getActivity()).drawer.openDrawer(Gravity.START);
            }
        });

        button_adotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_adotar");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    ((MainActivity) getActivity()).showNotLoggedFragment();
                } else {
                    ((MainActivity) getActivity()).showListarAnimaisFragment("Adotar");
                }
            }
        });

        button_ajudar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_ajudar");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    ((MainActivity) getActivity()).showNotLoggedFragment();
                } else {
                    ((MainActivity) getActivity()).showListarAnimaisFragment("Ajudar");
                }
            }
        });

        button_cadastrar_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_cadastrar_animal");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    ((MainActivity) getActivity()).showNotLoggedFragment();
                } else {
//                    ((MainActivity) getActivity()).showCadastrarAnimalFragment();

                    Fragment fragment = new CadastroAnimalFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment, "FRAGMENT_CADASTRO_ANIMAL_TAG")
                            .addToBackStack(null)
                            .commit();

                }
            }
        });

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: text_login");

                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    ((MainActivity) getActivity()).showSignInFragment();
                } else {
                    Log.d(TAG, "onClick: Logging out");
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getActivity(), "Saindo", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setDrawerInfo();
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
        enterFullScreen();
        updateLoginButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exitFullScreen();
    }

    private void updateLoginButton() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "updateLoginButton: User not logged");
            text_login.setText(R.string.login);
        } else {
            Log.d(TAG, "updateLoginButton: User " + currentUser.getEmail() + " logged");
            text_login.setText(R.string.logout);
        }
    }

    public void enterFullScreen() {
        toolbar.setVisibility(View.GONE);

        // hide status bar
//        View decorView = getActivity().getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void exitFullScreen() {
        toolbar.setVisibility(View.VISIBLE);

        // show status bar
//        View decorView = getActivity().getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class NotLoggedFragment extends Fragment {

    private static final String TAG = "NotLoggedFragment";

    private FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_not_logged, container, false);

        mAuth = FirebaseAuth.getInstance();

        Button button_signup = v.findViewById(R.id.button_signup);
        Button button_signin = v.findViewById(R.id.button_signin);

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_signup");
                ((MainActivity) getActivity()).showSignUpFragment();
            }
        });

        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_signin");
                ((MainActivity) getActivity()).showSignInFragment();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Cadastro");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");

        if (mAuth.getCurrentUser() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}

package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unb.meau.R;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signin, container, false);

        Button button_signup = v.findViewById(R.id.button_signup);
        Button button_signin = v.findViewById(R.id.button_signin);

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_signup");

                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, signUpFragment);
                fragmentTransaction.commit();
            }
        });

        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_signin");

                LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, loginFragment);
                fragmentTransaction.commit();
            }
        });

        return v;
    }
}

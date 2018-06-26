package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class SemHistoriaFragment extends Fragment {

    private static final String TAG = "SemHistoriaFragment";
    FirebaseAuth mAuth;

    View view;

    String nameUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sem_historia, container, false);

        Button button_listar_historias = view.findViewById(R.id.button_listar_historias);

        Bundle bundle = this.getArguments();

        nameUser = bundle.getString("nome");

        button_listar_historias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_signup");
                ((MainActivity) getActivity()).showListarHistoriasFragment();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle(nameUser);
        ((MainActivity) getActivity()).setActionBarTheme("Verde");

        if (mAuth.getCurrentUser() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }


}

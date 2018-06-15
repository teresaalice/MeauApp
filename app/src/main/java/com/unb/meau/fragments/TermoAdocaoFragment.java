package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

import java.util.HashMap;

public class TermoAdocaoFragment extends Fragment {

    private static final String TAG = "TermoAdocaoFragment";

    TextView texto_termos;

    Button buttonTermoAdocao;
    Button buttonTermoApadrinhamento;

    FirebaseUser currentUser;

    View view;

    private FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_termoadocao, container, false);

        mAuth = FirebaseAuth.getInstance();
        texto_termos = view.findViewById(R.id.termo_texto);
        buttonTermoAdocao = view.findViewById(R.id.button_termoadocao);
        buttonTermoApadrinhamento = view.findViewById(R.id.button_termoapadrinhamento);


        buttonTermoAdocao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: button termo_adocao.");
                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    ((MainActivity) getActivity()).showNotLoggedFragment();
                } else {
                    sendTerms();
                    ((MainActivity) getActivity()).showSentTermFragment();
                }
            }
        });

        buttonTermoApadrinhamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: button termo_apadrinhamento.");
                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    ((MainActivity) getActivity()).showNotLoggedFragment();
                } else {
                    sendTerms();
                    ((MainActivity) getActivity()).showSentTermFragment();
                }
            }
        });
        return view;
    }

    private void sendTerms() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        HashMap<String, String> email = new HashMap<>();
        email.put("email", currentUser.getEmail());
        db.collection("mailRequests").add(email);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Termo de Adoção");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }
}
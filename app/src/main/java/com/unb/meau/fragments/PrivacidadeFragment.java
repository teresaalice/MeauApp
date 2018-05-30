package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

import java.util.HashMap;

public class PrivacidadeFragment extends Fragment {

    private static final String TAG = "PrivacidadeFragment";

    Button buttonSalvarPrivacidade;

    TextView title;
    TextView texto_notificacoes;
    TextView texto_privacidade;

    CheckBox not_chat;
    CheckBox not_recordacoes;
    CheckBox not_eventos;

    FirebaseUser currentUser;
    FirebaseFirestore db;

    View view;

    HashMap<Object, Object> userObj = new HashMap<>();

    private FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_privacidade, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        title = view.findViewById(R.id.action_info);
        texto_notificacoes = view.findViewById(R.id.notificacoes);

        not_chat = view.findViewById(R.id.notificacoes_chat);
        not_recordacoes = view.findViewById(R.id.notificacoes_recordacao);
        not_eventos = view.findViewById(R.id.notificacoes_eventos);

        texto_privacidade = view.findViewById(R.id.privacidade_texto);
        buttonSalvarPrivacidade = view.findViewById(R.id.button_salvarprivacidade);

        buttonSalvarPrivacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: User settings updated.");
                Toast.makeText(getActivity(), "Alterações salvas com sucesso.", Toast.LENGTH_SHORT).show();
                storeUserData();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Privacidade");
        ((MainActivity) getActivity()).setActionBarTheme("Cinza");
    }

    private void storeUserData() {
        db = FirebaseFirestore.getInstance();
        userObj = new HashMap<>();
        CheckBox not_chat = view.findViewById(R.id.notificacoes_chat);
        userObj.put("notificacoes_chat", not_chat.isChecked());
        CheckBox not_recordacoes = view.findViewById(R.id.notificacoes_recordacao);
        userObj.put("notificacoes_recordacao", not_recordacoes.isChecked());
        CheckBox not_eventos = view.findViewById(R.id.notificacoes_eventos);
        userObj.put("notificacoes_eventos", not_eventos.isChecked());
    }
}
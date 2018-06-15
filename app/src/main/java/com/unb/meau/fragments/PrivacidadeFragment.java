package com.unb.meau.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class PrivacidadeFragment extends Fragment {

    private static final String TAG = "PrivacidadeFragment";

    CheckBox notificacoes_chat;
    CheckBox notificacoes_recordacao;
    CheckBox notificacoes_eventos;

    Button buttonSalvarPrivacidade;

    SharedPreferences sharedPref;

    FirebaseUser currentUser;
    FirebaseFirestore db;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_privacidade, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        notificacoes_chat = view.findViewById(R.id.notificacoes_chat);
        notificacoes_recordacao = view.findViewById(R.id.notificacoes_recordacao);
        notificacoes_eventos = view.findViewById(R.id.notificacoes_eventos);
        buttonSalvarPrivacidade = view.findViewById(R.id.button_salvarprivacidade);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        notificacoes_chat.setChecked(sharedPref.getBoolean("notificacoes_chat", true));
        notificacoes_recordacao.setChecked(sharedPref.getBoolean("notificacoes_recordacao", true));
        notificacoes_eventos.setChecked(sharedPref.getBoolean("notificacoes_eventos", true));

        buttonSalvarPrivacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: User settings updated.");
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

        CheckBox notificacoes_chat = view.findViewById(R.id.notificacoes_chat);
        CheckBox notificacoes_recordacao = view.findViewById(R.id.notificacoes_recordacao);
        CheckBox notificacoes_eventos = view.findViewById(R.id.notificacoes_eventos);

        WriteBatch batch = db.batch();
        batch.update(db.collection("users").document(currentUser.getUid()), "notificacoes_chat", notificacoes_chat.isChecked());
        batch.update(db.collection("users").document(currentUser.getUid()), "notificacoes_recordacao", notificacoes_recordacao.isChecked());
        batch.update(db.collection("users").document(currentUser.getUid()), "notificacoes_eventos", notificacoes_eventos.isChecked());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: user setting updated");
                    Toast.makeText(getActivity(), "Alterações salvas com sucesso.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "onComplete: Error updating user setting", task.getException());
                    Toast.makeText(getActivity(), "Erro ao salvar alterações", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("notificacoes_chat", notificacoes_chat.isChecked());
        editor.putBoolean("notificacoes_recordacao", notificacoes_recordacao.isChecked());
        editor.putBoolean("notificacoes_eventos", notificacoes_eventos.isChecked());
        editor.apply();
    }
}
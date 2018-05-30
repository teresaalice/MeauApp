package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class SentTermFragment extends Fragment {

    private static final String TAG = "SentTermFragment";

    TextView title_sent;
    TextView text_sent;

    Button button_legislacao;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_not_logged, container, false);

        title_sent = view.findViewById(R.id.titulo);
        text_sent = view.findViewById(R.id.texto);
        button_legislacao = view.findViewById(R.id.button_legislacao);

        Toast.makeText(getActivity(), "Termo enviado para o e-mail cadastrado.", Toast.LENGTH_SHORT).show();

        button_legislacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: button_signup");
                ((MainActivity) getActivity()).showLegislacaoFragment();
            }
        });
        return view;
    }
}

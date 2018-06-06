package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class ContarHistoriaSucessoFragment extends Fragment {

    private static final String TAG = "ContarHistoriaSucesso";

    private String storyId;
    private String animalName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contar_historia_sucesso, container, false);

        Button minha_historia = v.findViewById(R.id.button_minha_historia);
        Button ver_historias = v.findViewById(R.id.button_ver_historias);

        Bundle bundle = this.getArguments();

        if (bundle == null || bundle.getString("story_id") == null ) {
            Log.d(TAG, "onCreate: bundle null");
            return v;
        }

        if (bundle.getString("story_id") == null || bundle.getString("animal_name") == null) {
            Log.d(TAG, "onCreate: missing arguments");
            return v;
        }

        storyId = bundle.getString("story_id");
        animalName = bundle.getString("animal_name");

        minha_historia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: minha_historia");
                ((MainActivity) getActivity()).showHistoriaFragment(storyId, animalName);
            }
        });

        ver_historias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ver_historias");
                ((MainActivity) getActivity()).showListarHistoriasFragment();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Contar história");
    }
}
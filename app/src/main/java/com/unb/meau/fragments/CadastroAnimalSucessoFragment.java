package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class CadastroAnimalSucessoFragment extends Fragment {

    private static final String TAG = "CadastroAnimalSucesso";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cadastro_animal_sucesso, container, false);

        Button meus_pets = v.findViewById(R.id.button_meus_pets);

        meus_pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: meus_pets");
                ((MainActivity) getActivity()).showListarAnimaisFragment("Meus Pets");
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Cadastro do Animal");
        ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
    }
}
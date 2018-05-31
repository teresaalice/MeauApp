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

public class FiltroErroFragment extends Fragment {

    private static final String TAG = "CadastroAnimalFragment";

    Button editar;
    Button desabilitar;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filtro_erro, container, false);

        editar = view.findViewById(R.id.button_editar);
        desabilitar = view.findViewById(R.id.button_desabilitar);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: editar");
                getFragmentManager().popBackStack();
            }
        });

        desabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: desabilitar");
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
    }

}
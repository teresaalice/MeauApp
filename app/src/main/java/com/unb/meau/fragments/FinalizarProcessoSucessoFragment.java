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

public class FinalizarProcessoSucessoFragment extends Fragment {

    private static final String TAG = "FinalizarProcessoSucess";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_finalizar_processo_sucesso, container, false);

        Button buttonCompartilharHistoria = v.findViewById(R.id.button_share_history);

        buttonCompartilharHistoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: compartilhar história");
//                ((MainActivity) getActivity()).showCompartilharHistoriaFragment();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Finalizar Processo");
    }
}
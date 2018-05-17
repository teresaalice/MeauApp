package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

import org.w3c.dom.Text;

public class RemocaoAnimalSucessoFragment extends Fragment {

    private static final String TAG = "RemocaoAnimalSucesso";

    String nome;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_remocao_animal_sucesso, container, false);

        Button meus_pets = v.findViewById(R.id.button_meus_pets);

        Bundle bundle = this.getArguments();

        if (bundle == null) {
            Log.d(TAG, "onCreate: bundle null");
            return v;
        }

        nome = bundle.getString("nome");

        TextView text = v.findViewById(R.id.text2);
        text.setText("O " + nome + " foi removido da nossa lista com sucesso!");

        meus_pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: meus_pets");
                getActivity().getSupportFragmentManager().popBackStack("LIST_PERFIL_ANIMAL_TAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Remover pet");
    }
}
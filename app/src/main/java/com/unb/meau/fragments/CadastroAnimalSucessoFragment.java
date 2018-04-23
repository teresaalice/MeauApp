package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.meau.R;

import java.util.HashMap;
import java.util.Map;

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

//                SignUpFragment signUpFragment = new SignUpFragment();
//                FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.content_frame, signUpFragment);
//                fragmentTransaction.commit();
            }
        });

        return v;
    }
}
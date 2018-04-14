package com.unb.meau.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class IntroFragment extends Fragment {

    private static final String TAG = "IntroFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro, container, false);

        ImageView drawer_icon = v.findViewById(R.id.drawer_icon);
        Button button_adotar = v.findViewById(R.id.button_adotar);
        Button button_ajudar = v.findViewById(R.id.button_ajudar);
        Button button_cadastrar_animal = v.findViewById(R.id.button_cadastrar_animal);
        TextView text_login = v.findViewById(R.id.text_login);

        drawer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClick: drawer_icon");
                ((MainActivity) getActivity()).drawer.openDrawer(Gravity.START);
            }
        });

        button_adotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_adotar");
            }
        });

        button_ajudar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_ajudar");
            }
        });

        button_cadastrar_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_cadastrar_animal");
            }
        });

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: text_login");
            }
        });

        return v;
    }
}

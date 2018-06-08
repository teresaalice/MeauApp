package com.unb.meau.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

import java.io.InputStream;


public class DicasSaude1Fragment extends Fragment  {

    private static final String TAG = "DicasSaude1Fragment";
    Button maisdicas;
    View v;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dicas_saude_1, container, false);

        ImageView imagem = v.findViewById(R.id.imagem);


        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/meau-unb.appspot.com/o/extras%2Fmascotas-perros-no-pintar-unas-848x477x80xX.jpg?alt=media&token=875454dd-6820-407a-9b45-aeeda68314dc")
                .into(imagem);

        
        TextView main = v.findViewById(R.id.texto_main);
        String string1 = "<font color=\"#589b9b\">"+getString(R.string.text_saude_2) +"</font>" +"<br/>"+ getString(R.string.text_saude_3) +
                "<font color=\"#589b9b\">"+"<br/>"+"<br/>"+getString(R.string.text_saude_4) +"</font>" +"<br/>"+ getString(R.string.text_saude_5) +
                "<font color=\"#589b9b\">"+"<br/>"+"<br/>"+getString(R.string.text_saude_6) +"</font>" +"<br/>"+ getString(R.string.text_saude_7)+
                "<font color=\"#589b9b\">"+"<br/>"+"<br/>"+getString(R.string.text_saude_8) +"</font>" +"<br/>"+ getString(R.string.text_saude_9)+
                "<font color=\"#589b9b\">"+"<br/>"+"<br/>"+getString(R.string.text_saude_10) +"</font>" +"<br/>"+ getString(R.string.text_saude_11_1)
                +"<br/>"+ getString(R.string.text_saude_11_2)+"<br/>"+ getString(R.string.text_saude_11_3)+"<br/>"+ getString(R.string.text_saude_11_4)
                +"<br/>"+ getString(R.string.text_saude_11_5)+"<br/>"+ getString(R.string.text_saude_11_6)+"<br/>"+ getString(R.string.text_saude_11_7)
                +"<br/>"+ getString(R.string.text_saude_11_8);

        main.setText(Html.fromHtml(string1));


        TextView texto = v.findViewById(R.id.conteudo_por);
        String string2 = "<font color=\"#589b9b\">"+"CONTEUDO POR: " +"</font>" +getString(R.string.text_saude_12);
        texto.setText(Html.fromHtml(string2));

        //Botão
        maisdicas = v.findViewById(R.id.button_ver_mais);
        maisdicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: editar");
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Dicas");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }
}
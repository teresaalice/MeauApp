package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class DicaFragment extends Fragment {

    private static final String TAG = "DicaFragment";
    Button maisDicas;
    View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dica, container, false);

        ImageView imagem = v.findViewById(R.id.imagem);

        Glide.with(this)
                .load(getString(R.string.unhas_url))
                .into(imagem);

        TextView titulo = v.findViewById(R.id.titulo);
        titulo.setText(R.string.saude_1);

        TextView intro = v.findViewById(R.id.intro);
        intro.setText(R.string.text_saude_1);

        TextView main = v.findViewById(R.id.texto_main);
        String dicas = "<font color=\"#589b9b\">" + getString(R.string.text_saude_2) + "</font>" + "<br/>" + getString(R.string.text_saude_3) +
                "<font color=\"#589b9b\">" + "<br/>" + "<br/>" + getString(R.string.text_saude_4) + "</font>" + "<br/>" + getString(R.string.text_saude_5) +
                "<font color=\"#589b9b\">" + "<br/>" + "<br/>" + getString(R.string.text_saude_6) + "</font>" + "<br/>" + getString(R.string.text_saude_7) +
                "<font color=\"#589b9b\">" + "<br/>" + "<br/>" + getString(R.string.text_saude_8) + "</font>" + "<br/>" + getString(R.string.text_saude_9) +
                "<font color=\"#589b9b\">" + "<br/>" + "<br/>" + getString(R.string.text_saude_10) + "</font>" + "<br/>" + getString(R.string.text_saude_11_1)
                + "<br/>" + getString(R.string.text_saude_11_2) + "<br/>" + getString(R.string.text_saude_11_3) + "<br/>" + getString(R.string.text_saude_11_4)
                + "<br/>" + getString(R.string.text_saude_11_5) + "<br/>" + getString(R.string.text_saude_11_6) + "<br/>" + getString(R.string.text_saude_11_7)
                + "<br/>" + getString(R.string.text_saude_11_8);

        main.setText(Html.fromHtml(dicas));

        TextView texto = v.findViewById(R.id.conteudo_por);
        String fonte = "<font color=\"#589b9b\">" + "CONTEÚDO POR: " + "</font>" + getString(R.string.text_saude_12);
        texto.setText(Html.fromHtml(fonte));

        // Botão
        maisDicas = v.findViewById(R.id.button_ver_mais);
        maisDicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

public class LegislacaoFragment extends Fragment {

    private static final String TAG = "Legislacao";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_legislacao, container, false);

        //Cores e paragrafos
        TextView texto = v.findViewById(R.id.text3);

        //<font color="#589b9b"> </font>
        String string = "<font color=\"#589b9b\">"+"Art. 4º" +"</font>" + " ";
        string = string + getString(R.string.artigo_4_1)+ "<br/>"+ getString(R.string.artigo_4_2)+ "<br/>" + getString(R.string.artigo_4_3)+ "<br/>"
                + getString(R.string.artigo_4_4)+ "<br/>"+ getString(R.string.artigo_4_5) +"<br/>"+"<br/>";
        string = string + "<font color=\"#589b9b\">"+"Art. 5º" +"</font>" + " " ;
        string = string + getString(R.string.artigo_5_1)+ "<br/>"+ getString(R.string.artigo_5_2)+ "<br/>"+"<br/>";
        string = string + "<font color=\"#589b9b\">"+"Art. 6º" +"</font>" + " ";
        string = string + getString(R.string.artigo_6)+"<br/>"+"<br/>";
        string = string + "<font color=\"#589b9b\">"+"Art. 7º" +"</font>" + " ";
        string = string + getString(R.string.artigo_7)+"<br/>"+"<br/>";

        texto.setText(Html.fromHtml(string));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Legislação");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }
}
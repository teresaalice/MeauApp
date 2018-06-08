package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.CustomDicasExpandableListAdapter;

import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


public class DicasFragment extends Fragment {

    private static final String TAG = "DicasFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dicas, container, false);
        setInfo(v);


        return v;
    }


    public void setInfo(View v){

        final List<String> groupNames = Arrays.asList("Comportamento", "Alimentação", "Saúde");

        final LinkedHashMap<String, List<String>> listItem = new LinkedHashMap<>();
        listItem.put("Comportamento", Arrays.asList(getString(R.string.comportamento_1),getString(R.string.comportamento_2), getString(R.string.comportamento_3)));
        listItem.put("Alimentação", Arrays.asList(getString(R.string.alimentacao_1), getString(R.string.alimentacao_2), getString(R.string.alimentacao_3), getString(R.string.alimentacao_4)));
        listItem.put("Saúde", Arrays.asList(getString(R.string.saude_1),getString(R.string.saude_2), getString(R.string.saude_3), getString(R.string.saude_4), getString(R.string.saude_5),getString(R.string.saude_6),
                getString(R.string.saude_7),getString(R.string.saude_8),getString(R.string.saude_9)));

        ExpandableListAdapter expandableListViewAdapter = new CustomDicasExpandableListAdapter(getActivity(), groupNames, listItem);
        ExpandableListView expandableListView = v.findViewById(R.id.expandable_list);
        expandableListView.setAdapter(expandableListViewAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (listItem.get(groupNames.get(groupPosition)))).get(childPosition).toString();

                switch (selectedItem) {
                    case "Como cortar as unhas do seu cachorro":
                        Log.d(TAG, "onClick: saude1");

                        DicasSaude1Fragment DicasSaude1Fragment = new DicasSaude1Fragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, DicasSaude1Fragment, MainActivity.FRAGMENT_CADASTRO_ANIMAL_SUCESSO_TAG)
                                .addToBackStack("FRAGMENT_DICAS_SAUDE1_TAG")
                                .commit();

                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Dicas");
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }
}
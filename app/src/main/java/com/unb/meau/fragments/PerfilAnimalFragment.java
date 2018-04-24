package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unb.meau.R;
import com.unb.meau.objects.Animal;

import java.util.ArrayList;
import java.util.List;

public class PerfilAnimalFragment extends Fragment {

    private static final String TAG = "PerfilAnimalFragment";

    private FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil_animal, container, false);

        Bundle bundle = this.getArguments();

        if (bundle == null) {
            Log.d(TAG, "onCreate: bundle null");
            return v;
        }

        String nome = bundle.getString("nome");
        String dono = bundle.getString("dono");

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("animals").whereEqualTo("dono", dono).whereEqualTo("nome", nome).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Animal animal = task.getResult().getDocuments().get(0).toObject(Animal.class);
                    bindData(animal);
                } else {
                    Log.w(TAG, "onComplete: Animal no found", task.getException());
                }
            }
        });

        return v;
    }

    private void bindData(Animal animal) {
        TextView nome = getView().findViewById(R.id.nome);
        TextView sexo = getView().findViewById(R.id.sexo);
        TextView porte = getView().findViewById(R.id.porte);
        TextView idade = getView().findViewById(R.id.idade);
        TextView localizacao = getView().findViewById(R.id.localizacao);
        TextView castrado = getView().findViewById(R.id.castrado);
        TextView vermifugado = getView().findViewById(R.id.vermifugado);
        TextView vacinado = getView().findViewById(R.id.vacinado);
        TextView doencas = getView().findViewById(R.id.doencas);
        TextView temperamento = getView().findViewById(R.id.temperamento);
        LinearLayout adocao = getView().findViewById(R.id.adocao);
        TextView exigencias_doacao = getView().findViewById(R.id.exigencias_doacao);
        LinearLayout apadrinhamento = getView().findViewById(R.id.apadrinhamento);
        TextView exigencias_apadrinhamento = getView().findViewById(R.id.exigencias_apadrinhamento);
        LinearLayout ajuda = getView().findViewById(R.id.ajuda);
        TextView exigencias_ajuda = getView().findViewById(R.id.exigencias_ajuda);
        TextView sobre = getView().findViewById(R.id.sobre);

        nome.setText(animal.getNome());
        sexo.setText(animal.getSexo());
        porte.setText(animal.getPorte());
        idade.setText(animal.getIdade());
        localizacao.setText(animal.getLocalizacao());
        sobre.setText(animal.getHistoria());
        doencas.setText(animal.getDoencas());

        castrado.setText(((animal.getCastrado()) ? "Sim" : "Não"));
        vermifugado.setText(((animal.getVermifugado()) ? "Sim" : "Não"));
        vacinado.setText(((animal.getVacinado()) ? "Sim" : "Não"));

        temperamento.setText(getTemperamentoString(animal));

        if (animal.getCadastro_adocao()) {
            adocao.setVisibility(View.VISIBLE);
            exigencias_doacao.setText(getAdocaoString(animal));
        }
        if (animal.getCadastro_apadrinhar()) {
            apadrinhamento.setVisibility(View.VISIBLE);
            exigencias_apadrinhamento.setText(getApadrinhamentoString(animal));
        }
        if (animal.getCadastro_ajuda()) {
            ajuda.setVisibility(View.VISIBLE);
            exigencias_ajuda.setText(getAjudaString(animal));
        }
    }

    private String getTemperamentoString(Animal animal) {
        List<String> temperamentoArray = new ArrayList<String>();

        if(animal.getBrincalhao()) temperamentoArray.add("Brincalhão");
        if(animal.getTimido()) temperamentoArray.add("Tímido");
        if(animal.getCalmo()) temperamentoArray.add("Calmo");
        if(animal.getGuarda()) temperamentoArray.add("Guarda");
        if(animal.getAmoroso()) temperamentoArray.add("Amoroso");
        if(animal.getPreguicoso()) temperamentoArray.add("Preguiçoso");

        String temperamentoString = "";
        if (temperamentoArray.size() > 0) {
            temperamentoString = temperamentoArray.get(0);

            for (int i = 1; i < temperamentoArray.size(); i++) {
                if (i == temperamentoArray.size() - 1) {
                    temperamentoString += " e " + temperamentoArray.get(i);
                } else {
                    temperamentoString += ", " + temperamentoArray.get(i);
                }
            }
        }
        return temperamentoString;
    }

    private String getAdocaoString(Animal animal) {
        List<String> adocaoArray = new ArrayList<String>();

        if(animal.getTermo_de_adocao()) adocaoArray.add("Termo de adoção");
        if(animal.getFotos_da_casa()) adocaoArray.add("Fotos da casa");
        if(animal.getVisita_previa_ao_animal()) adocaoArray.add("Visita prévia ao animal");
        if(animal.getAcompanhamento_pos_adocao() != null) adocaoArray.add("Acompanhamento durante " + animal.getAcompanhamento_pos_adocao());

        String adocaoString = "";
        if (adocaoArray.size() > 0) {
            adocaoString = adocaoArray.get(0);

            for (int i = 1; i < adocaoArray.size(); i++) {
                if (i == adocaoArray.size() - 1) {
                    adocaoString += " e " + adocaoArray.get(i);
                } else {
                    adocaoString += ", " + adocaoArray.get(i);
                }
            }
        }
        return adocaoString;
    }

    private String getApadrinhamentoString(Animal animal) {
        List<String> apadrinhamentoArray = new ArrayList<String>();

        if(animal.getTermo_de_apadrinhamento()) apadrinhamentoArray.add("Termo de apadrinhamento");
        if(animal.getVisitas_ao_animal()) apadrinhamentoArray.add("Visitas ao animal");
        if(animal.getAuxilio_financeiro()) apadrinhamentoArray.add("Auxílio financeiro");

        String apadrinhamentoString = "";
        if (apadrinhamentoArray.size() > 0) {
            apadrinhamentoString = apadrinhamentoArray.get(0);

            for (int i = 1; i < apadrinhamentoArray.size(); i++) {
                if (i == apadrinhamentoArray.size() - 1) {
                    apadrinhamentoString += " e " + apadrinhamentoArray.get(i);
                } else {
                    apadrinhamentoString += ", " + apadrinhamentoArray.get(i);
                }
            }
        }
        return apadrinhamentoString;
    }

    private String getAjudaString(Animal animal) {
        List<String> ajudaArray = new ArrayList<String>();

        if(animal.getAlimento()) ajudaArray.add("Alimento");
        if(animal.getAjuda_financeira()) ajudaArray.add("Ajuda financeira");
        if(animal.getAjuda_medicamento()) ajudaArray.add(animal.getAjuda_medicamento_nome());
        if(animal.getAjuda_objeto()) ajudaArray.add(animal.getAjuda_objetos_nome());

        String ajudaString = "";
        if (ajudaArray.size() > 0) {
            ajudaString = ajudaArray.get(0);

            for (int i = 1; i < ajudaArray.size(); i++) {
                if (i == ajudaArray.size() - 1) {
                    ajudaString += " e " + ajudaArray.get(i);
                } else {
                    ajudaString += ", " + ajudaArray.get(i);
                }
            }
        }
        return ajudaString;
    }
}

package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class CadastroAnimalFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "CadastroAnimalFragment";

    Button finalizar;

    ToggleButton buttonAdocao;
    ToggleButton buttonApadrinhar;
    ToggleButton buttonAjuda;

    LinearLayout adocaoLayout;
    LinearLayout apadrinharLayout;
    LinearLayout ajudarLayout;

    CheckBox acompanhamento;
    CheckBox auxilio_financeiro;
    CheckBox sub_checkbox_alimentacao;
    CheckBox sub_checkbox_saude;
    CheckBox sub_checkbox_objetos;

    RadioGroup acompanhamento_radio_group;

    ConstraintLayout second_layout_page;

    TextView title;

    FirebaseFirestore db;
    Map<String, Object> animalObj;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cadastro_animal, container, false);

        finalizar = view.findViewById(R.id.button_finalizar);
        buttonAdocao = view.findViewById(R.id.button_adocao);
        buttonApadrinhar = view.findViewById(R.id.button_apadrinhar);
        buttonAjuda = view.findViewById(R.id.button_ajuda);
        adocaoLayout = view.findViewById(R.id.adocao);
        apadrinharLayout = view.findViewById(R.id.apadrinhamento);
        ajudarLayout = view.findViewById(R.id.ajudar);
        acompanhamento = view.findViewById(R.id.acompanhamento);
        acompanhamento_radio_group = view.findViewById(R.id.acompanhamento_radio_group);
        title = view.findViewById(R.id.action_info);
        auxilio_financeiro = view.findViewById(R.id.auxilio_financeiro);
        sub_checkbox_alimentacao = view.findViewById(R.id.auxilio_alimentacao);
        sub_checkbox_saude = view.findViewById(R.id.auxilio_saude);
        sub_checkbox_objetos = view.findViewById(R.id.auxilio_objetos);
        second_layout_page = view.findViewById(R.id.cadastro_animal_second_layout);

        second_layout_page.setVisibility(View.GONE);

        buttonAdocao.setOnCheckedChangeListener(this);
        buttonApadrinhar.setOnCheckedChangeListener(this);
        buttonAjuda.setOnCheckedChangeListener(this);
        acompanhamento.setOnCheckedChangeListener(this);
        auxilio_financeiro.setOnCheckedChangeListener(this);

        for(int i = 0; i < acompanhamento_radio_group.getChildCount(); i++){
            (acompanhamento_radio_group.getChildAt(i)).setEnabled(false);
        }

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: finalizar");
                addToDatabase();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Cadastro do Animal");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.button_adocao:
            case R.id.button_apadrinhar:
            case R.id.button_ajuda:

                if (isChecked) {
                    second_layout_page.setVisibility(View.VISIBLE);
                } else {
                    if (!buttonAdocao.isChecked() && !buttonApadrinhar.isChecked() && !buttonAjuda.isChecked()) {
                        second_layout_page.setVisibility(View.GONE);
                    }
                }
                break;
        }

        switch (buttonView.getId()) {

            case R.id.button_adocao:
                if (isChecked) {
//                    buttonAdocao.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    title.setText("Adotar");
                    finalizar.setText("COLOCAR PARA ADOÇÃO");
                    adocaoLayout.setVisibility(View.VISIBLE);
                    buttonApadrinhar.setEnabled(false);
                } else {
//                    buttonAdocao.setBackgroundColor(getResources().getColor(R.color.cinza));
                    adocaoLayout.setVisibility(View.GONE);
                    buttonApadrinhar.setEnabled(true);
                }
                break;

            case R.id.button_apadrinhar:
                if (isChecked) {
                    title.setText("Apadrinhar");
                    finalizar.setText("PROCURAR PADRINHO");
                    apadrinharLayout.setVisibility(View.VISIBLE);
                    buttonAdocao.setEnabled(false);
                } else {
                    apadrinharLayout.setVisibility(View.GONE);
                    buttonAdocao.setEnabled(true);
                }
                break;

            case R.id.button_ajuda:
                if (isChecked) {
                    if (!buttonAdocao.isChecked() && !buttonApadrinhar.isChecked()) {
                        title.setText("Ajudar");
                        finalizar.setText("BUSCAR AJUDA");
                    }
                    ajudarLayout.setVisibility(View.VISIBLE);
                } else {
                    ajudarLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.acompanhamento:
                for(int i = 0; i < acompanhamento_radio_group.getChildCount(); i++){
                    (acompanhamento_radio_group.getChildAt(i)).setEnabled(isChecked);
                }
                break;

            case R.id.auxilio_financeiro:
                if (isChecked) {
                    sub_checkbox_alimentacao.setEnabled(true);
                    sub_checkbox_saude.setEnabled(true);
                    sub_checkbox_objetos.setEnabled(true);
                } else {
                    sub_checkbox_alimentacao.setEnabled(false);
                    sub_checkbox_saude.setEnabled(false);
                    sub_checkbox_objetos.setEnabled(false);
                }
                break;
        }
    }

    private void addToDatabase() {

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

       animalObj = new HashMap<>();

        animalObj.put("dono", currentUser.getUid());

        ToggleButton button_adocao = view.findViewById(R.id.button_adocao);
        animalObj.put("cadastro_adocao", button_adocao.isChecked());

        ToggleButton button_apadrinhar = view.findViewById(R.id.button_apadrinhar);
        animalObj.put("cadastro_apadrinhar", button_apadrinhar.isChecked());

        ToggleButton button_ajuda = view.findViewById(R.id.button_ajuda);
        animalObj.put("cadastro_ajuda", button_ajuda.isChecked());

        EditText nome_do_animal = view.findViewById(R.id.nome_do_animal);
        animalObj.put("nome", nome_do_animal.getText().toString());

        RadioGroup especie = view.findViewById(R.id.especie);
        switch (especie.getCheckedRadioButtonId()) {
            case R.id.cachorro:
                animalObj.put("especie", "Cachorro");
                break;
            case R.id.gato:
                animalObj.put("especie", "Gato");
                break;
        }

        RadioGroup sexo = view.findViewById(R.id.sexo);
        switch (sexo.getCheckedRadioButtonId()) {
            case R.id.macho:
                animalObj.put("sexo", "Macho");
                break;
            case R.id.femea:
                animalObj.put("sexo", "Fêmea");
                break;
        }

        RadioGroup porte = view.findViewById(R.id.porte);
        switch (porte.getCheckedRadioButtonId()) {
            case R.id.pequeno:
                animalObj.put("porte", "Pequeno");
                break;
            case R.id.medio:
                animalObj.put("porte", "Médio");
                break;
            case R.id.grande:
                animalObj.put("porte", "Grande");
                break;
        }


        RadioGroup idade = view.findViewById(R.id.idade);
        switch (idade.getCheckedRadioButtonId()) {
            case R.id.filhote:
                animalObj.put("idade", "Filhote");
                break;
            case R.id.adulto:
                animalObj.put("idade", "Adulto");
                break;
            case R.id.idoso:
                animalObj.put("idade", "Idoso");
                break;
        }

        // Temperamento
        CheckBox brincalhao = view.findViewById(R.id.brincalhao);
        animalObj.put("brincalhao", brincalhao.isChecked());

        CheckBox timido = view.findViewById(R.id.timido);
        animalObj.put("timido", timido.isChecked());

        CheckBox calmo = view.findViewById(R.id.calmo);
        animalObj.put("calmo", calmo.isChecked());

        CheckBox guarda = view.findViewById(R.id.guarda);
        animalObj.put("guarda", guarda.isChecked());

        CheckBox amoroso = view.findViewById(R.id.amoroso);
        animalObj.put("amoroso", amoroso.isChecked());

        CheckBox preguicoso = view.findViewById(R.id.preguicoso);
        animalObj.put("preguicoso", preguicoso.isChecked());

        // Saúde
        CheckBox vacinado = view.findViewById(R.id.vacinado);
        animalObj.put("vacinado", vacinado.isChecked());

        CheckBox vermifugado = view.findViewById(R.id.vermifugado);
        animalObj.put("vermifugado", vermifugado.isChecked());

        CheckBox castrado = view.findViewById(R.id.castrado);
        animalObj.put("castrado", castrado.isChecked());

        CheckBox doente = view.findViewById(R.id.doente);
        if (doente.isChecked()) {
            EditText doencas = view.findViewById(R.id.doencas);
            animalObj.put("doencas", doencas.getText().toString());
        }

        // Adoção
        CheckBox termo_de_adocao = view.findViewById(R.id.termo_de_adocao);
        animalObj.put("termo_de_adocao", termo_de_adocao.isChecked());

        CheckBox fotos_da_casa = view.findViewById(R.id.fotos_da_casa);
        animalObj.put("fotos_da_casa", fotos_da_casa.isChecked());

        CheckBox visita_previa_ao_animal = view.findViewById(R.id.visita_previa_ao_animal);
        animalObj.put("visita_previa_ao_animal", visita_previa_ao_animal.isChecked());

        CheckBox acompanhamento = view.findViewById(R.id.acompanhamento);
        if (acompanhamento.isChecked()) {
            RadioGroup acompanhamento_radio_group = view.findViewById(R.id.acompanhamento_radio_group);
            switch (acompanhamento_radio_group.getCheckedRadioButtonId()) {
                case R.id.um_mes:
                    animalObj.put("acompanhamento_pos_adocao", "1 mês");
                    break;
                case R.id.tres_meses:
                    animalObj.put("acompanhamento_pos_adocao", "3 meses");
                    break;
                case R.id.seis_meses:
                    animalObj.put("acompanhamento_pos_adocao", "6 meses");
                    break;
            }
        }

        // Apadrinhamento
        CheckBox termo_apadrinhamento = view.findViewById(R.id.termo_apadrinhamento);
        animalObj.put("termo_de_apadrinhamento", termo_apadrinhamento.isChecked());

        CheckBox auxilio_financeiro = view.findViewById(R.id.auxilio_financeiro);
        animalObj.put("auxilio_financeiro", auxilio_financeiro.isChecked());
        if (auxilio_financeiro.isChecked()) {

            CheckBox auxilio_alimentacao = view.findViewById(R.id.auxilio_alimentacao);
            if (auxilio_alimentacao.isChecked()) animalObj.put("auxilio_alimentacao", true);

            CheckBox auxilio_saude = view.findViewById(R.id.auxilio_saude);
            if (auxilio_saude.isChecked()) animalObj.put("auxilio_saude", true);

            CheckBox auxilio_objetos = view.findViewById(R.id.auxilio_objetos);
            if (auxilio_objetos.isChecked()) animalObj.put("auxilio_objetos", true);
        }

        CheckBox visitas_ao_animal = view.findViewById(R.id.visitas_ao_animal);
        animalObj.put("visitas_ao_animal", visitas_ao_animal.isChecked());

        // Ajuda
        CheckBox alimento = view.findViewById(R.id.alimento);
        animalObj.put("alimento", alimento.isChecked());

        CheckBox ajuda_financeira = view.findViewById(R.id.ajuda_financeira);
        animalObj.put("ajuda_financeira", ajuda_financeira.isChecked());


        CheckBox medicamento = view.findViewById(R.id.medicamento);
        animalObj.put("ajuda_medicamento", medicamento.isChecked());
        if (medicamento.isChecked()) {
            EditText medicamento_text = view.findViewById(R.id.medicamento_text);
            if (medicamento_text.getText() != null)
                animalObj.put("ajuda_medicamento_nome", medicamento_text.getText().toString());
        }

        CheckBox ajuda_objetos = view.findViewById(R.id.ajuda_objetos);
        animalObj.put("ajuda_objeto", ajuda_objetos.isChecked());
        if (ajuda_objetos.isChecked()) {
            EditText objetos_text = view.findViewById(R.id.objetos_text);
            if (objetos_text.getText() != null)
                animalObj.put("ajuda_objetos_nome", objetos_text.getText().toString());
        }

        EditText sobre_o_animal = view.findViewById(R.id.sobre_o_animal);
        if(!sobre_o_animal.getText().toString().isEmpty())
            animalObj.put("historia", sobre_o_animal.getText().toString());
        else
            animalObj.put("historia", "");

        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Localização: " + task.getResult().get("cidade") + " - " + task.getResult().get("estado"));

                    animalObj.put("localizacao", task.getResult().get("cidade") + " - " + task.getResult().get("estado"));

                    db.collection("animals")
                            .add(animalObj)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + task.getResult().getId());
                                        Toast.makeText(getActivity(), "Animal cadastrado com sucesso", Toast.LENGTH_SHORT).show();

                                        CadastroAnimalSucessoFragment fragment = new CadastroAnimalSucessoFragment();
                                        getActivity().getFragmentManager().beginTransaction()
                                                .replace(R.id.content_frame, fragment)
                                                .commit();
                                    } else {
                                        Log.w(TAG, "Error adding document", task.getException());
                                        Toast.makeText(getActivity(), "Erro ao cadastrar o animal", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    Log.w(TAG, "onComplete: Error getting user location", task.getException());
                    Toast.makeText(getActivity(), "Erro ao verificar localização do usuário", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

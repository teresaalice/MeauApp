package com.unb.meau.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Animal;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class CadastroAnimalFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "CadastroAnimalFragment";
    ProgressBar mProgressBar;
    Button finalizar;
    Button buttonAdicionarFoto;
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
    ArrayList<String> downloadUrl = new ArrayList<>();
    FirebaseFirestore db;
    Animal newAnimal;
    View view;

    private int PICK_IMAGE_REQUEST = 1;
    private Animal animal;
    private String animalId;
    private Boolean editarPerfil = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cadastro_animal, container, false);

        second_layout_page = view.findViewById(R.id.cadastro_animal_second_layout);
        second_layout_page.setVisibility(View.GONE);

        adocaoLayout = view.findViewById(R.id.adocao);
        apadrinharLayout = view.findViewById(R.id.apadrinhamento);
        ajudarLayout = view.findViewById(R.id.ajudar);

        adocaoLayout.setVisibility(View.GONE);
        apadrinharLayout.setVisibility(View.GONE);
        ajudarLayout.setVisibility(View.GONE);

        finalizar = view.findViewById(R.id.button_finalizar);
        buttonAdocao = view.findViewById(R.id.button_adocao);
        buttonApadrinhar = view.findViewById(R.id.button_apadrinhar);
        buttonAjuda = view.findViewById(R.id.button_ajuda);
        buttonAdicionarFoto = view.findViewById(R.id.button_adicionar_fotos);
        acompanhamento = view.findViewById(R.id.acompanhamento);
        acompanhamento_radio_group = view.findViewById(R.id.acompanhamento_radio_group);
        title = view.findViewById(R.id.action_info);
        auxilio_financeiro = view.findViewById(R.id.auxilio_financeiro);
        sub_checkbox_alimentacao = view.findViewById(R.id.auxilio_alimentacao);
        sub_checkbox_saude = view.findViewById(R.id.auxilio_saude);
        sub_checkbox_objetos = view.findViewById(R.id.auxilio_objetos);
        mProgressBar = view.findViewById(R.id.progress_bar);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("animalId") != null) {
            editarPerfil = true;
            animalId = bundle.getString("animalId");
            Log.d(TAG, "onCreateView: Editar animal " + animalId);
            finalizar.setText("Salvar alterações");
            getAnimalPerfil();
        }

        buttonAdocao.setOnCheckedChangeListener(this);
        buttonApadrinhar.setOnCheckedChangeListener(this);
        buttonAjuda.setOnCheckedChangeListener(this);
        acompanhamento.setOnCheckedChangeListener(this);
        auxilio_financeiro.setOnCheckedChangeListener(this);

        buttonAdicionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Adicionar Foto");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), PICK_IMAGE_REQUEST);
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: finalizar");
                if (!isDataCorrect()) return;
                showProgressDialog();
                addToDatabase();
            }
        });

        return view;
    }

    private boolean isDataCorrect() {
        EditText nome_do_animal = view.findViewById(R.id.nome_do_animal);
        if (nome_do_animal.getText().toString().isEmpty()) {
            Log.d(TAG, "isDataCorrect: Animal name missing");
            Toast.makeText(getActivity(), "Defina o nome do animal", Toast.LENGTH_SHORT).show();
            return false;
        }

        RadioGroup especie = view.findViewById(R.id.especie);
        if (especie.getCheckedRadioButtonId() == -1) {
            Log.d(TAG, "isDataCorrect: Animal species missing");
            Toast.makeText(getActivity(), "Defina a espécie do animal", Toast.LENGTH_SHORT).show();
            return false;
        }

        RadioGroup sexo = view.findViewById(R.id.sexo);
        if (sexo.getCheckedRadioButtonId() == -1) {
            Log.d(TAG, "isDataCorrect: Animal sex missing");
            Toast.makeText(getActivity(), "Defina o sexo do animal", Toast.LENGTH_SHORT).show();
            return false;
        }

        RadioGroup porte = view.findViewById(R.id.porte);
        if (porte.getCheckedRadioButtonId() == -1) {
            Log.d(TAG, "isDataCorrect: Animal size missing");
            Toast.makeText(getActivity(), "Defina o porte do animal", Toast.LENGTH_SHORT).show();
            return false;
        }

        RadioGroup idade = view.findViewById(R.id.idade);
        if (idade.getCheckedRadioButtonId() == -1) {
            Log.d(TAG, "isDataCorrect: Animal age missing");
            Toast.makeText(getActivity(), "Defina a idade do animal", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Cadastro do Animal");
        ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.button_adocao:
            case R.id.button_apadrinhar:
            case R.id.button_ajuda:

                // mostra ou esconde restante da página
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
                    title.setText("Adotar");
                    if (!editarPerfil) finalizar.setText("COLOCAR PARA ADOÇÃO");
                    adocaoLayout.setVisibility(View.VISIBLE);
                    buttonApadrinhar.setEnabled(false);
                } else {
                    adocaoLayout.setVisibility(View.GONE);
                    buttonApadrinhar.setEnabled(true);
                }
                break;

            case R.id.button_apadrinhar:
                if (isChecked) {
                    title.setText("Apadrinhar");
                    if (!editarPerfil) finalizar.setText("PROCURAR PADRINHO");
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
                        if (!editarPerfil) finalizar.setText("BUSCAR AJUDA");
                    }
                    ajudarLayout.setVisibility(View.VISIBLE);
                } else {
                    ajudarLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.acompanhamento:
                for (int i = 0; i < acompanhamento_radio_group.getChildCount(); i++) {
                    (acompanhamento_radio_group.getChildAt(i)).setEnabled(isChecked);
                }
                break;

            case R.id.auxilio_financeiro:
                sub_checkbox_alimentacao.setEnabled(isChecked);
                sub_checkbox_saude.setEnabled(isChecked);
                sub_checkbox_objetos.setEnabled(isChecked);
                break;
        }
    }

    private void getAnimalPerfil() {

        showProgressDialog();

        db.collection("animals").document(animalId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                animal = task.getResult().toObject(Animal.class);
                                fillData();
                            } else
                                Log.d(TAG, "onComplete: No animal found with ID" + animalId);
                        } else {
                            Log.w(TAG, "onComplete: Error getting animal", task.getException());
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void fillData() {

        ToggleButton button_adocao = view.findViewById(R.id.button_adocao);
        button_adocao.setChecked(animal.getCadastro_adocao());

        ToggleButton button_apadrinhar = view.findViewById(R.id.button_apadrinhar);
        button_apadrinhar.setChecked(animal.getCadastro_apadrinhar());

        ToggleButton button_ajuda = view.findViewById(R.id.button_ajuda);
        button_ajuda.setChecked(animal.getCadastro_ajuda());

        EditText nome_do_animal = view.findViewById(R.id.nome_do_animal);
        nome_do_animal.setText(animal.getNome());

        RadioGroup especie = view.findViewById(R.id.especie);
        switch (animal.getEspecie()) {
            case "Cachorro":
                especie.check(R.id.cachorro);
                break;
            case "Gato":
                especie.check(R.id.gato);
                break;
        }

        RadioGroup sexo = view.findViewById(R.id.sexo);
        switch (animal.getSexo()) {
            case "Macho":
                sexo.check(R.id.macho);
                break;
            case "Fêmea":
                sexo.check(R.id.femea);
                break;
        }

        RadioGroup porte = view.findViewById(R.id.porte);
        switch (animal.getPorte()) {
            case "Pequeno":
                porte.check(R.id.pequeno);
                break;
            case "Médio":
                porte.check(R.id.medio);
                break;
            case "Grande":
                porte.check(R.id.grande);
                break;
        }

        RadioGroup idade = view.findViewById(R.id.idade);
        switch (animal.getIdade()) {
            case "Filhote":
                idade.check(R.id.filhote);
                break;
            case "Adulto":
                idade.check(R.id.adulto);
                break;
            case "Idoso":
                idade.check(R.id.idoso);
                break;
        }

        // Temperamento
        CheckBox brincalhao = view.findViewById(R.id.brincalhao);
        brincalhao.setChecked(animal.getBrincalhao());

        CheckBox timido = view.findViewById(R.id.timido);
        timido.setChecked(animal.getTimido());

        CheckBox calmo = view.findViewById(R.id.calmo);
        calmo.setChecked(animal.getCalmo());

        CheckBox guarda = view.findViewById(R.id.guarda);
        guarda.setChecked(animal.getGuarda());

        CheckBox amoroso = view.findViewById(R.id.amoroso);
        amoroso.setChecked(animal.getAmoroso());

        CheckBox preguicoso = view.findViewById(R.id.preguicoso);
        preguicoso.setChecked(animal.getPreguicoso());

        // Saúde
        CheckBox vacinado = view.findViewById(R.id.vacinado);
        vacinado.setChecked(animal.getVacinado());

        CheckBox vermifugado = view.findViewById(R.id.vermifugado);
        vermifugado.setChecked(animal.getVermifugado());

        CheckBox castrado = view.findViewById(R.id.castrado);
        castrado.setChecked(animal.getCastrado());

        CheckBox doente = view.findViewById(R.id.doente);
        if (animal.getDoencas() != null && !animal.getDoencas().isEmpty()) {
            doente.setChecked(true);
            EditText doencas = view.findViewById(R.id.doencas);
            doencas.setText(animal.getDoencas());
        }

        // Adoção
        if (animal.getCadastro_adocao()) {

            CheckBox termo_de_adocao = view.findViewById(R.id.termo_de_adocao);
            termo_de_adocao.setChecked(animal.getTermo_de_adocao());

            CheckBox fotos_da_casa = view.findViewById(R.id.fotos_da_casa);
            fotos_da_casa.setChecked(animal.getFotos_da_casa());

            CheckBox visita_previa_ao_animal = view.findViewById(R.id.visita_previa_ao_animal);
            visita_previa_ao_animal.setChecked(animal.getVisita_previa_ao_animal());

            CheckBox acompanhamento = view.findViewById(R.id.acompanhamento);
            if (animal.getAcompanhamento_pos_adocao() != null) {
                acompanhamento.setChecked(true);
                RadioGroup acompanhamento_radio_group = view.findViewById(R.id.acompanhamento_radio_group);
                switch (animal.getAcompanhamento_pos_adocao()) {
                    case "1 mês":
                        acompanhamento_radio_group.check(R.id.um_mes);
                        break;
                    case "3 meses":
                        acompanhamento_radio_group.check(R.id.tres_meses);
                        break;
                    case "6 meses":
                        acompanhamento_radio_group.check(R.id.seis_meses);
                        break;
                }
            }
        }

        // Apadrinhamento
        if (animal.getCadastro_apadrinhar()) {

            CheckBox termo_apadrinhamento = view.findViewById(R.id.termo_apadrinhamento);
            termo_apadrinhamento.setChecked(animal.getTermo_de_apadrinhamento());

            CheckBox auxilio_financeiro = view.findViewById(R.id.auxilio_financeiro);
            newAnimal.setAuxilio_financeiro(auxilio_financeiro.isChecked());
            if (animal.getAuxilio_financeiro()) {

                CheckBox auxilio_alimentacao = view.findViewById(R.id.auxilio_alimentacao);
                auxilio_alimentacao.setChecked(animal.getAuxilio_alimentacao());

                CheckBox auxilio_saude = view.findViewById(R.id.auxilio_saude);
                auxilio_saude.setChecked(animal.getAuxilio_saude());

                CheckBox auxilio_objetos = view.findViewById(R.id.auxilio_objetos);
                auxilio_objetos.setChecked(animal.getAuxilio_objetos());
            }

            CheckBox visitas_ao_animal = view.findViewById(R.id.visitas_ao_animal);
            visitas_ao_animal.setChecked(animal.getVisitas_ao_animal());
        }

        // Ajuda
        if (animal.getCadastro_ajuda()) {

            CheckBox alimento = view.findViewById(R.id.alimento);
            alimento.setChecked(animal.getAlimento());

            CheckBox ajuda_financeira = view.findViewById(R.id.ajuda_financeira);
            ajuda_financeira.setChecked(animal.getAjuda_financeira());

            CheckBox medicamento = view.findViewById(R.id.medicamento);
            medicamento.setChecked(animal.getAjuda_medicamento());
            if (animal.getAjuda_medicamento()) {
                EditText medicamento_text = view.findViewById(R.id.medicamento_text);
                if (!animal.getAjuda_medicamento_nome().isEmpty())
                    medicamento_text.setText(animal.getAjuda_medicamento_nome());
            }

            CheckBox ajuda_objetos = view.findViewById(R.id.ajuda_objetos);
            ajuda_objetos.setChecked(animal.getAjuda_objeto());
            if (animal.getAjuda_objeto()) {
                EditText objetos_text = view.findViewById(R.id.objetos_text);
                if (!animal.getAjuda_objetos_nome().isEmpty())
                    objetos_text.setText(animal.getAjuda_objetos_nome());
            }
        }

        EditText sobre_o_animal = view.findViewById(R.id.sobre_o_animal);
        if (!animal.getHistoria().isEmpty())
            sobre_o_animal.setText(animal.getHistoria());
    }

    private void addToDatabase() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        newAnimal = new Animal();

        newAnimal.setDono(currentUser.getUid());
        newAnimal.setDono_nome(currentUser.getDisplayName());

        ToggleButton button_adocao = view.findViewById(R.id.button_adocao);
        newAnimal.setCadastro_adocao(button_adocao.isChecked());

        ToggleButton button_apadrinhar = view.findViewById(R.id.button_apadrinhar);
        newAnimal.setCadastro_apadrinhar(button_apadrinhar.isChecked());

        ToggleButton button_ajuda = view.findViewById(R.id.button_ajuda);
        newAnimal.setCadastro_ajuda(button_ajuda.isChecked());

        EditText nome_do_animal = view.findViewById(R.id.nome_do_animal);
        newAnimal.setNome(nome_do_animal.getText().toString());

        RadioGroup especie = view.findViewById(R.id.especie);
        switch (especie.getCheckedRadioButtonId()) {
            case R.id.cachorro:
                newAnimal.setEspecie("Cachorro");
                break;
            case R.id.gato:
                newAnimal.setEspecie("Gato");
                break;
        }

        RadioGroup sexo = view.findViewById(R.id.sexo);
        switch (sexo.getCheckedRadioButtonId()) {
            case R.id.macho:
                newAnimal.setSexo("Macho");
                break;
            case R.id.femea:
                newAnimal.setSexo("Fêmea");
                break;
        }

        RadioGroup porte = view.findViewById(R.id.porte);
        switch (porte.getCheckedRadioButtonId()) {
            case R.id.pequeno:
                newAnimal.setPorte("Pequeno");
                break;
            case R.id.medio:
                newAnimal.setPorte("Médio");
                break;
            case R.id.grande:
                newAnimal.setPorte("Grande");
                break;
        }

        RadioGroup idade = view.findViewById(R.id.idade);
        switch (idade.getCheckedRadioButtonId()) {
            case R.id.filhote:
                newAnimal.setIdade("Filhote");
                break;
            case R.id.adulto:
                newAnimal.setIdade("Adulto");
                break;
            case R.id.idoso:
                newAnimal.setIdade("Idoso");
                break;
        }

        // Temperamento
        CheckBox brincalhao = view.findViewById(R.id.brincalhao);
        newAnimal.setBrincalhao(brincalhao.isChecked());

        CheckBox timido = view.findViewById(R.id.timido);
        newAnimal.setTimido(timido.isChecked());

        CheckBox calmo = view.findViewById(R.id.calmo);
        newAnimal.setCalmo(calmo.isChecked());

        CheckBox guarda = view.findViewById(R.id.guarda);
        newAnimal.setGuarda(guarda.isChecked());

        CheckBox amoroso = view.findViewById(R.id.amoroso);
        newAnimal.setAmoroso(amoroso.isChecked());

        CheckBox preguicoso = view.findViewById(R.id.preguicoso);
        newAnimal.setPreguicoso(preguicoso.isChecked());

        // Saúde
        CheckBox vacinado = view.findViewById(R.id.vacinado);
        newAnimal.setVacinado(vacinado.isChecked());

        CheckBox vermifugado = view.findViewById(R.id.vermifugado);
        newAnimal.setVermifugado(vermifugado.isChecked());

        CheckBox castrado = view.findViewById(R.id.castrado);
        newAnimal.setCastrado(castrado.isChecked());

        CheckBox doente = view.findViewById(R.id.doente);
        if (doente.isChecked()) {
            EditText doencas = view.findViewById(R.id.doencas);
            newAnimal.setDoencas(doencas.getText().toString());
        }

        // Adoção
        CheckBox termo_de_adocao = view.findViewById(R.id.termo_de_adocao);
        newAnimal.setTermo_de_adocao(termo_de_adocao.isChecked());

        CheckBox fotos_da_casa = view.findViewById(R.id.fotos_da_casa);
        newAnimal.setFotos_da_casa(fotos_da_casa.isChecked());

        CheckBox visita_previa_ao_animal = view.findViewById(R.id.visita_previa_ao_animal);
        newAnimal.setVisita_previa_ao_animal(visita_previa_ao_animal.isChecked());

        CheckBox acompanhamento = view.findViewById(R.id.acompanhamento);
        if (acompanhamento.isChecked()) {
            RadioGroup acompanhamento_radio_group = view.findViewById(R.id.acompanhamento_radio_group);
            switch (acompanhamento_radio_group.getCheckedRadioButtonId()) {
                case R.id.um_mes:
                    newAnimal.setAcompanhamento_pos_adocao("1 mês");
                    break;
                case R.id.tres_meses:
                    newAnimal.setAcompanhamento_pos_adocao("3 meses");
                    break;
                case R.id.seis_meses:
                    newAnimal.setAcompanhamento_pos_adocao("6 meses");
                    break;
            }
        }

        // Apadrinhamento
        CheckBox termo_apadrinhamento = view.findViewById(R.id.termo_apadrinhamento);
        newAnimal.setTermo_de_apadrinhamento(termo_apadrinhamento.isChecked());

        CheckBox auxilio_financeiro = view.findViewById(R.id.auxilio_financeiro);
        newAnimal.setAuxilio_financeiro(auxilio_financeiro.isChecked());
        if (auxilio_financeiro.isChecked()) {

            CheckBox auxilio_alimentacao = view.findViewById(R.id.auxilio_alimentacao);
            if (auxilio_alimentacao.isChecked()) newAnimal.setAuxilio_alimentacao(true);

            CheckBox auxilio_saude = view.findViewById(R.id.auxilio_saude);
            if (auxilio_saude.isChecked()) newAnimal.setAuxilio_saude(true);

            CheckBox auxilio_objetos = view.findViewById(R.id.auxilio_objetos);
            if (auxilio_objetos.isChecked()) newAnimal.setAuxilio_objetos(true);
        }

        CheckBox visitas_ao_animal = view.findViewById(R.id.visitas_ao_animal);
        newAnimal.setVisitas_ao_animal(visitas_ao_animal.isChecked());

        // Ajuda
        CheckBox alimento = view.findViewById(R.id.alimento);
        newAnimal.setAlimento(alimento.isChecked());

        CheckBox ajuda_financeira = view.findViewById(R.id.ajuda_financeira);
        newAnimal.setAjuda_financeira(ajuda_financeira.isChecked());

        CheckBox medicamento = view.findViewById(R.id.medicamento);
        newAnimal.setAjuda_medicamento(medicamento.isChecked());
        if (medicamento.isChecked()) {
            EditText medicamento_text = view.findViewById(R.id.medicamento_text);
            if (medicamento_text.getText() != null)
                newAnimal.setAjuda_medicamento_nome(medicamento_text.getText().toString());
        }

        CheckBox ajuda_objetos = view.findViewById(R.id.ajuda_objetos);
        newAnimal.setAjuda_objeto(ajuda_objetos.isChecked());
        if (ajuda_objetos.isChecked()) {
            EditText objetos_text = view.findViewById(R.id.objetos_text);
            if (objetos_text.getText() != null)
                newAnimal.setAjuda_objetos_nome(objetos_text.getText().toString());
        }

        EditText sobre_o_animal = view.findViewById(R.id.sobre_o_animal);
        if (!sobre_o_animal.getText().toString().isEmpty())
            newAnimal.setHistoria(sobre_o_animal.getText().toString());
        else
            newAnimal.setHistoria("");

        if (downloadUrl.size() > 0) {
            String fotos = TextUtils.join(",", downloadUrl);
            newAnimal.setFotos(fotos);
        } else {
            if (editarPerfil && !animal.getFotos().isEmpty())
                newAnimal.setFotos(animal.getFotos());
            else
                newAnimal.setFotos("");
        }

        newAnimal.setNovos_interessados(0);
        newAnimal.setFavoritos(new HashMap<String, Boolean>());

        if (!editarPerfil) {
            animalId = Long.toString(System.currentTimeMillis()) + "_" + nome_do_animal.getText().toString();
        }

        db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Localização: " + task.getResult().get("cidade") + " - " + task.getResult().get("estado"));

                    newAnimal.setLocalizacao(task.getResult().get("cidade") + " - " + task.getResult().get("estado"));

                    db.collection("animals").document(animalId)
                            .set(newAnimal)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + animalId);

                                        CadastroAnimalSucessoFragment cadastroAnimalSucessoFragment = new CadastroAnimalSucessoFragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.content_frame, cadastroAnimalSucessoFragment, MainActivity.FRAGMENT_CADASTRO_ANIMAL_SUCESSO_TAG)
                                                .addToBackStack("CADASTRO_TAG")
                                                .commit();

                                    } else {
                                        Log.w(TAG, "Error adding document", task.getException());
                                        Toast.makeText(getActivity(), "Erro ao cadastrar o animal", Toast.LENGTH_SHORT).show();
                                    }
                                    hideProgressDialog();
                                }
                            });

                } else {
                    Log.w(TAG, "onComplete: Error getting user location", task.getException());
                    Toast.makeText(getActivity(), "Erro ao verificar localização do usuário", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }
        });
    }

    private void uploadFile(Uri filePath) {

        finalizar.setEnabled(false);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageRef = mStorageRef.child("animals/" + System.currentTimeMillis() + ".jpg");

        Toast.makeText(getActivity(), "Fazendo upload da imagem", Toast.LENGTH_SHORT).show();

        imageRef.putFile(filePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            downloadUrl.add(task.getResult().getDownloadUrl().toString());
                            Log.d(TAG, "onComplete: Photo uploaded: " + downloadUrl.get(downloadUrl.size() - 1));
                            Toast.makeText(getActivity(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "onComplete: Error uploading photo", task.getException());
                            Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                        }
                        finalizar.setEnabled(true);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Log.d(TAG, "onActivityResult: " + filePath);
            uploadFile(filePath);
        }
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}
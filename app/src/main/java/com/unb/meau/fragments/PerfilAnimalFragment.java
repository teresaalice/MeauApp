package com.unb.meau.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Animal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilAnimalFragment extends Fragment implements Button.OnClickListener {

    private static final String TAG = "PerfilAnimalFragment";

    ProgressBar mProgressBar;
    ConstraintLayout foto_layout;
    ConstraintLayout cadastro_animal_second_layout;

    LinearLayout buttons_meu_pets;
    LinearLayout buttons_finalizar;

    Button button_adotar;
    Button button_apadrinhar;
    Button button_ajudar;
    Button button_interessados;
    Button button_remover;

    FloatingActionButton fab;

    Animal animal;

    String nomeAnimal;
    String acao;
    String animalId;

    FirebaseUser currentUser;

    FirebaseFirestore db;
    private String processId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil_animal, container, false);

        mProgressBar = v.findViewById(R.id.progress_bar);
        foto_layout = v.findViewById(R.id.foto_layout);
        cadastro_animal_second_layout = v.findViewById(R.id.cadastro_animal_second_layout);

        buttons_meu_pets = v.findViewById(R.id.buttons_meu_pets);

        button_adotar = v.findViewById(R.id.button_adotar);
        button_apadrinhar = v.findViewById(R.id.button_apadrinhar);
        button_ajudar = v.findViewById(R.id.button_ajudar);

        buttons_finalizar = v.findViewById(R.id.buttons_finalizar);
        button_interessados = v.findViewById(R.id.button_interessados);
        button_remover = v.findViewById(R.id.button_remover);

        fab = v.findViewById(R.id.fab);

        showProgressDialog();

        foto_layout.setVisibility(View.GONE);
        cadastro_animal_second_layout.setVisibility(View.GONE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        if (bundle == null) {
            Log.d(TAG, "onCreate: bundle null");
            return v;
        }

        nomeAnimal = bundle.getString("nome");
        String dono = bundle.getString("dono");
        acao = bundle.getString("acao");

        if (!acao.equals("Meus Pets") && dono.equals(currentUser.getUid())) {
            acao = "Meus Pets";
        }

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("animals").whereEqualTo("dono", dono).whereEqualTo("nome", nomeAnimal).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0) {
                        animal = task.getResult().getDocuments().get(0).toObject(Animal.class);
                        animalId = task.getResult().getDocuments().get(0).getId();
                        bindData(animal);
                    }
                } else {
                    Log.w(TAG, "onComplete: Animal not found", task.getException());
                    Toast.makeText(getActivity(), "Animal não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (acao.equals("Meus Pets")) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
            buttons_meu_pets.setVisibility(View.VISIBLE);
        } else {
            buttons_finalizar.setVisibility(View.VISIBLE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (acao.equals("Meus Pets")) {
                    ((MainActivity) getActivity()).showCadastrarAnimalFragment(animalId);
                } else {
                    fab.setSelected(!fab.isSelected());

                    if (fab.isSelected()) {
                        Log.d(TAG, "onClick: " + animal.getNome() + " favoritado");
                        favoritarAnimal(animalId, true);
                    } else {
                        Log.d(TAG, "onClick: " + animal.getNome() + " desfavoritado");
                        favoritarAnimal(animalId, false);
                    }
                }
            }
        });

        button_adotar.setOnClickListener(this);
        button_apadrinhar.setOnClickListener(this);
        button_ajudar.setOnClickListener(this);

        button_interessados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Interessados");

                ListPeopleFragment listPeopleFragment = new ListPeopleFragment();

                Bundle args = new Bundle();

                args.putString("animalId", animalId);
                listPeopleFragment.setArguments(args);

                getFragmentManager().beginTransaction().replace(R.id.content_frame, listPeopleFragment, MainActivity.FRAGMENT_LISTAR_PESSOAS_TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });

        button_remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Remover");
                showProgressDialog();

                db.collection("animals").document(animalId)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    hideProgressDialog();

                                    RemocaoAnimalSucessoFragment remocaoAnimalSucessoFragment = new RemocaoAnimalSucessoFragment();

                                    Bundle args = new Bundle();
                                    args.putString("nome", nomeAnimal);
                                    remocaoAnimalSucessoFragment.setArguments(args);

                                    getFragmentManager().beginTransaction().replace(R.id.content_frame, remocaoAnimalSucessoFragment, MainActivity.FRAGMENT_REMOCAO_ANIMAL_SUCESSO_TAG)
                                            .addToBackStack(null)
                                            .commit();

                                } else {
                                    Log.w(TAG, "Error deleting document", task.getException());
                                    hideProgressDialog();
                                    Toast.makeText(getActivity(), "Erro ao remover o animal", Toast.LENGTH_SHORT).show();
                                }
                                hideProgressDialog();
                            }
                        });

                db.collection("processes")
                        .whereEqualTo("animal", animalId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    WriteBatch batch = db.batch();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        batch.delete(document.getReference());
                                    }
                                    batch.commit();
                                }
                            }
                        });

                db.collection("users")
                        .whereEqualTo("interesses." + animalId, true)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    WriteBatch batch = db.batch();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        batch.update(document.getReference(), "interesses." + animalId, FieldValue.delete());
                                    }
                                    batch.commit();
                                }
                            }
                        });

            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle(nomeAnimal);

        if (acao.equals("Meus Pets")) {
            ((MainActivity) getActivity()).setActionBarTheme("Verde");
            setLabelGreen();
        } else {
            ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
        }

        ((MainActivity) getActivity()).menuItemName = "share";
        getActivity().invalidateOptionsMenu();

    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).menuItemName = "";
        getActivity().invalidateOptionsMenu();
    }

    private void bindData(Animal animal) {
        ImageView foto = getView().findViewById(R.id.foto_animal);
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

        if (!acao.equals("Meus Pets")) {
            for (Map.Entry<String, Boolean> entry : animal.getFavoritos().entrySet()) {
                if (entry.getKey().equals(currentUser.getUid()) && entry.getValue()) {
                    fab.setSelected(true);
                    break;
                }
            }
        }

        if (animal.getCadastro_adocao()) {
            button_adotar.setVisibility(View.VISIBLE);
        }

        if (animal.getCadastro_apadrinhar()) {
            button_apadrinhar.setVisibility(View.VISIBLE);
        }

        if (animal.getCadastro_ajuda()) {
            button_ajudar.setVisibility(View.VISIBLE);
        }

        String fotos = animal.getFotos();

        if (fotos != null && !fotos.isEmpty()) {
            List<String> fotosList = Arrays.asList(fotos.split(","));

            if (fotosList.size() > 0) {
                Uri fotoUri = Uri.parse(fotosList.get(0));
                Glide.with(this)
                        .load(fotoUri)
                        .into(foto);
            }
        } else {
            if (animal.getEspecie() != null && animal.getEspecie().equals("Cachorro")) {
                foto.setImageResource(R.drawable.dog_silhouette);
            } else {
                foto.setImageResource(R.drawable.cat_silhouette);
            }
            foto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

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

        hideProgressDialog();
        foto_layout.setVisibility(View.VISIBLE);
        cadastro_animal_second_layout.setVisibility(View.VISIBLE);
    }

    private String getTemperamentoString(Animal animal) {
        List<String> temperamentoArray = new ArrayList<String>();

        if (animal.getBrincalhao()) temperamentoArray.add("Brincalhão");
        if (animal.getTimido()) temperamentoArray.add("Tímido");
        if (animal.getCalmo()) temperamentoArray.add("Calmo");
        if (animal.getGuarda()) temperamentoArray.add("Guarda");
        if (animal.getAmoroso()) temperamentoArray.add("Amoroso");
        if (animal.getPreguicoso()) temperamentoArray.add("Preguiçoso");

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

        if (animal.getTermo_de_adocao()) adocaoArray.add("Termo de adoção");
        if (animal.getFotos_da_casa()) adocaoArray.add("Fotos da casa");
        if (animal.getVisita_previa_ao_animal()) adocaoArray.add("Visita prévia ao animal");
        if (animal.getAcompanhamento_pos_adocao() != null)
            adocaoArray.add("Acompanhamento durante " + animal.getAcompanhamento_pos_adocao());

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

        if (animal.getTermo_de_apadrinhamento()) apadrinhamentoArray.add("Termo de apadrinhamento");
        if (animal.getVisitas_ao_animal()) apadrinhamentoArray.add("Visitas ao animal");
        if (animal.getAuxilio_financeiro()) apadrinhamentoArray.add("Auxílio financeiro");

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
        List<String> ajudaArray = new ArrayList<>();

        if (animal.getAlimento()) ajudaArray.add("Alimento");
        if (animal.getAjuda_financeira()) ajudaArray.add("Ajuda financeira");
        if (animal.getAjuda_medicamento()) ajudaArray.add(animal.getAjuda_medicamento_nome());
        if (animal.getAjuda_objeto()) ajudaArray.add(animal.getAjuda_objetos_nome());

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

    private void setLabelGreen() {
        ((TextView) getView().findViewById(R.id.sexo_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.porte_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.idade_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.localizacao_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.castrado_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.vermifugado_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.vacinado_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.doencas_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.temperamento_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.exigencias_doacao_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.exigencias_apadrinhamento_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.exigencias_ajuda_info)).setTextColor(getResources().getColor(R.color.verde3));
        ((TextView) getView().findViewById(R.id.sobre_info)).setTextColor(getResources().getColor(R.color.verde3));
    }

    @Override
    public void onClick(View v) {

        final Map<String, Object> interesseObj;
        interesseObj = new HashMap<>();
        interesseObj.put("dono", animal.getDono());
        interesseObj.put("interessado", currentUser.getUid());
        interesseObj.put("animal", animalId);
        interesseObj.put("animalNome", animal.getNome());
        interesseObj.put("estagio", "interesse");
        interesseObj.put("interessadoNome", currentUser.getDisplayName());

        switch (v.getId()) {
            case R.id.button_adotar:
                interesseObj.put("acao", "adoção");
                break;
            case R.id.button_apadrinhar:
                interesseObj.put("acao", "apadrinhamento");
                break;
            case R.id.button_ajudar:
                interesseObj.put("acao", "ajuda");
                break;
        }

        processId = Integer.toString((int) (System.currentTimeMillis() / 1000)) + "_" + nomeAnimal + "_" + currentUser.getDisplayName();

        showProgressDialog();

        db.collection("processes")
                .whereEqualTo("interessado", currentUser.getUid())
                .whereEqualTo("animal", animalId)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().isEmpty()) {
                                Log.d(TAG, "onComplete: novo interesse");


                                db.collection("processes").document(processId)
                                        .set(interesseObj)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "DocumentSnapshot written with ID: " + processId);
                                                    Toast.makeText(getActivity(), "Sucesso", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.w(TAG, "Error adding processes document", task.getException());
                                                    Toast.makeText(getActivity(), "Erro ao adicionar interesse", Toast.LENGTH_SHORT).show();
                                                }
                                                hideProgressDialog();
                                            }
                                        });

                                db.collection("users").document(currentUser.getUid())
                                        .update("interesses." + animalId, true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User document updated");
                                                } else {
                                                    Log.w(TAG, "Error updating user document", task.getException());
                                                    Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                                                }
                                                hideProgressDialog();
                                            }
                                        });

                                db.collection("animals").document(animalId)
                                        .update("novos_interessados", animal.getNovos_interessados() + 1)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Animal document updated");
                                                } else {
                                                    Log.w(TAG, "Error updating animal document", task.getException());
                                                }
                                                hideProgressDialog();
                                            }
                                        });

                            } else {
                                Log.d(TAG, "onComplete: interesse existente");
                                Toast.makeText(getActivity(), "Interesse já existente", Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getActivity(), "Erro ao verificar interesses", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    public void favoritarAnimal(String animalId, final Boolean favoritar) {

        showProgressDialog();

        // favoritar: adiciona userId:true
        // desfavoritar: remove o campo userId:true
        db.collection("animals").document(animalId)
                .update("favoritos." + currentUser.getUid(), (favoritar) ? true : FieldValue.delete())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Document updated");
                            if (favoritar)
                                Toast.makeText(getActivity(), "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), "Removido dos favoritos", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.w(TAG, "Error updating document", task.getException());
                            Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

}

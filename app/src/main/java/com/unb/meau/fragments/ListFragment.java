package com.unb.meau.fragments;

import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Animal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    String acao;
    String uid;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("acao") != null) {
            Log.d(TAG, "onCreateView: Listar pets: " + bundle.getString("acao"));
            acao = bundle.getString("acao");
            if (acao.equals("Meus Pets")) {
                uid = bundle.getString("uid");
                Log.d(TAG, "onCreateView: uid: " + uid);
            }
        } else {
            Log.d(TAG, "onCreateView: bundle null");
            acao = "Animais";
        }

        getAnimalList();

        return rootView;
    }

    private void getAnimalList() {
        Query query;

        switch (acao) {
            case "Adotar":
                query = db.collection("animals").whereEqualTo("cadastro_adocao", true);
                break;
            case "Apadrinhar":
                query = db.collection("animals").whereEqualTo("cadastro_apadrinhar", true);
                break;
            case "Ajudar":
                query = db.collection("animals").whereEqualTo("cadastro_ajuda", true);
                break;
            case "Meus Pets":
                query = db.collection("animals").whereEqualTo("dono", uid);
                break;
            default:
                query = db.collection("animals");
                break;
        }

        FirestoreRecyclerOptions<Animal> options = new FirestoreRecyclerOptions.Builder<Animal>()
                .setQuery(query, Animal.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Animal, AnimalsHolder>(options) {
            @Override
            public void onBindViewHolder(final AnimalsHolder holder, final int position, Animal model) {

                holder.textNome.setText(model.getNome());

                if (acao.equals("Meus Pets")) {
                    holder.atributos.setVisibility(View.GONE);
                    holder.textLocalizacao.setVisibility(View.GONE);
                    holder.buttonFav.setVisibility(View.GONE);
                    holder.textNome.setBackgroundColor(getResources().getColor(R.color.verde1));

                    holder.textInteressados.setText("0 novos interessados");

                    List<String> categoriaArray = new ArrayList<String>();
                    if (model.getCadastro_adocao()) categoriaArray.add("Adoção");
                    if (model.getCadastro_apadrinhar()) categoriaArray.add("Apadrinhamento");
                    if (model.getCadastro_ajuda()) categoriaArray.add("Ajuda");

                    holder.textCategoria.setText(TextUtils.join(" | ", categoriaArray));

                } else {
                    holder.textInteressados.setVisibility(View.GONE);
                    holder.textCategoria.setVisibility(View.GONE);
                    holder.iconError.setVisibility(View.GONE);

                    holder.textSexo.setText(model.getSexo());
                    holder.textIdade.setText(model.getIdade());
                    holder.textPorte.setText(model.getPorte());
                    holder.textLocalizacao.setText(model.getLocalizacao());
                }

                String fotos = model.getFotos();

                if (fotos != null && !fotos.isEmpty()) {
                    List<String> fotosList = Arrays.asList(fotos.split(","));

                    if (fotosList.size() > 0) {
                        Uri fotoUri = Uri.parse(fotosList.get(0));
                        Glide.with(getActivity())
                                .load(fotoUri)
                                .into(holder.image);
                    }
                } else {
                    if (model.getEspecie() != null && model.getEspecie().equals("Cachorro")) {
                        holder.image.setImageResource(R.drawable.dog_silhouette);
                    } else {
                        holder.image.setImageResource(R.drawable.cat_silhouette);
                    }
                    holder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                holder.buttonFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.buttonFav.setSelected(!holder.buttonFav.isSelected());

                        if (holder.buttonFav.isSelected()) {
                            Log.d(TAG, "onClick: " + holder.textNome.getText() + " favoritado");
                            Toast.makeText(getActivity(), "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onClick: " + holder.textNome.getText() + " desfavoritado");
                            Toast.makeText(getActivity(), "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: " + holder.textNome.getText());
                        Animal animal = (Animal) adapter.getItem(position);

                        PerfilAnimalFragment perfilAnimalFragment = new PerfilAnimalFragment();

                        Bundle args = new Bundle();
                        args.putString("nome", animal.getNome());
                        args.putString("dono", animal.getDono());
                        args.putString("acao", acao);
                        perfilAnimalFragment.setArguments(args);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, perfilAnimalFragment)
                                .addToBackStack("LIST_PERFIL_ANIMAL_TAG")
                                .commit();
                    }
                });
            }

            @Override
            public AnimalsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.layout_animal_item, group, false);

                return new AnimalsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle(acao);

        if (acao.equals("Meus Pets")) {
            ((MainActivity) getActivity()).setActionBarTheme("Verde");
        } else {
            ((MainActivity) getActivity()).setActionBarTheme("Amarelo");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class AnimalsHolder extends RecyclerView.ViewHolder {

        TextView textNome;
        ImageButton buttonFav;
        ImageButton iconError;
        ImageView image;

        TextView textSexo;
        TextView textIdade;
        TextView textPorte;
        TextView textLocalizacao;

        TextView textInteressados;
        TextView textCategoria;

        LinearLayout atributos;

        private AnimalsHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.nome);
            buttonFav = itemView.findViewById(R.id.button_fav);
            iconError = itemView.findViewById(R.id.icon_error);
            image = itemView.findViewById(R.id.image_animal);
            textSexo = itemView.findViewById(R.id.sexo);
            textIdade = itemView.findViewById(R.id.idade);
            textPorte = itemView.findViewById(R.id.porte);
            textLocalizacao = itemView.findViewById(R.id.localizacao);
            textInteressados = itemView.findViewById(R.id.interessados);
            textCategoria = itemView.findViewById(R.id.categoria);
            atributos = itemView.findViewById(R.id.atributos);
        }
    }
}
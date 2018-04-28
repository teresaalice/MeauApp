package com.unb.meau.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Animal;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";

    RecyclerView animalList;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    String acao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        animalList = rootView.findViewById(R.id.listRecyclerView);

        animalList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();

        if (bundle == null) {
            Log.d(TAG, "onCreateView: bundle null");
            acao = "Animais";
        } else {
            Log.d(TAG, "onCreateView: Listar pets para " + bundle.getString("acao"));
            acao = bundle.getString("acao");
        }

        getAnimalList(acao);

        return rootView;
    }

    private void getAnimalList(final String acao){
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
                holder.textSexo.setText(model.getSexo());
                holder.textIdade.setText(model.getIdade());
                holder.textPorte.setText(model.getPorte());
                holder.textLocalizacao.setText(model.getLocalizacao());

//                holder.image;

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
                        
                        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, perfilAnimalFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

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
        animalList.setAdapter(adapter);
    }

    public class AnimalsHolder extends RecyclerView.ViewHolder {

        TextView textNome;
        ImageButton buttonFav;
        ImageView image;
        TextView textSexo;
        TextView textIdade;
        TextView textPorte;
        TextView textLocalizacao;

        public AnimalsHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.nome);
            buttonFav = itemView.findViewById(R.id.button_fav);
            image = itemView.findViewById(R.id.image_animal);
            textSexo = itemView.findViewById(R.id.sexo);
            textIdade = itemView.findViewById(R.id.idade);
            textPorte = itemView.findViewById(R.id.porte);
            textLocalizacao = itemView.findViewById(R.id.localizacao);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle(acao);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
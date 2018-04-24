package com.unb.meau.fragments;

import android.app.Fragment;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unb.meau.R;
import com.unb.meau.objects.Animal;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";

    RecyclerView animalList;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_recyclerview, container, false);

        Log.d(TAG, "onCreate: Created.");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        animalList = rootView.findViewById(R.id.listRecyclerView);

        animalList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();

        getAnimalList();

        return rootView;
    }

    private void getAnimalList(){
        Query query = db.collection("animals");

        FirestoreRecyclerOptions<Animal> options = new FirestoreRecyclerOptions.Builder<Animal>()
                .setQuery(query, Animal.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Animal, AnimalsHolder>(options) {
            @Override
            public void onBindViewHolder(final AnimalsHolder holder, int position, Animal model) {

                holder.textNome.setText(model.getNome());
                holder.textSexo.setText(model.getSexo());
                holder.textIdade.setText(model.getIdade());
                holder.textPorte.setText(model.getPorte());
                holder.textLocalizacao.setText(model.getDono());

//                holder.image;

                holder.buttonFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.buttonFav.setSelected(!holder.buttonFav.isSelected());

                        if (holder.buttonFav.isSelected()) {
                            Log.d(TAG, "onClick: " + holder.textNome.getText() + " favoritado");
                        } else {
                            Log.d(TAG, "onClick: " + holder.textNome.getText() + " desfavoritado");
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: " + holder.textNome.getText());
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
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
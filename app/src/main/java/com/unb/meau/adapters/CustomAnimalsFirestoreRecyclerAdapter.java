package com.unb.meau.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.unb.meau.R;
import com.unb.meau.fragments.ListFragment;
import com.unb.meau.objects.Animal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomAnimalsFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter {

    private static final String TAG = "AnimalsFirestorRecycler";

    private static final int VIEW_TYPE_OWN = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    final private ListAnimalClickListener mOnClickListener;

    private ListFragment context;
    private String acao;
    private String currentUserUid;

    public CustomAnimalsFirestoreRecyclerAdapter(ListFragment context, @NonNull FirestoreRecyclerOptions options, String acao, String currentUserUid, ListAnimalClickListener listener) {
        super(options);
        this.context = context;
        this.acao = acao;
        this.currentUserUid = currentUserUid;
        this.mOnClickListener = listener;
    }

    public interface ListAnimalClickListener {
        void onListAnimalClick(Animal animal);

        void onListAnimalFavClick(Animal animal, Boolean favoritar);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == VIEW_TYPE_OWN) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meu_animal_item, parent, false);
            viewHolder = new CustomAnimalsFirestoreRecyclerAdapter.MyAnimalsHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_animal_item, parent, false);
            viewHolder = new CustomAnimalsFirestoreRecyclerAdapter.AnimalsHolder(view);
        }

        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

        Animal mModel = (Animal) model;

        if (acao.equals("Meus Pets"))
            bindMyAnimalsHolder(mModel, (MyAnimalsHolder) holder);
        else
            bindAnimalsHolder(mModel, (AnimalsHolder) holder);
    }

    private void bindAnimalsHolder(Animal mModel, AnimalsHolder mHolder) {
        mHolder.textNome.setText(mModel.getNome());

        if (acao.equals("Favoritos"))
            mHolder.card.setBackgroundColor(context.getResources().getColor(R.color.verde1));

        mHolder.textSexo.setText(mModel.getSexo());
        mHolder.textIdade.setText(mModel.getIdade());
        mHolder.textPorte.setText(mModel.getPorte());
        mHolder.textLocalizacao.setText(mModel.getLocalizacao());

        if (mModel.getFavoritos() != null) {
            for (Map.Entry<String, Boolean> entry : mModel.getFavoritos().entrySet()) {
                if (entry.getKey().equals(currentUserUid) && entry.getValue()) {
                    mHolder.buttonFav.setSelected(true);
                    break;
                }
            }
        }

        String fotos = mModel.getFotos();

        if (fotos != null && !fotos.isEmpty()) {
            List<String> fotosList = Arrays.asList(fotos.split(","));

            if (fotosList.size() > 0) {
                Uri fotoUri = Uri.parse(fotosList.get(0));
                Glide.with(context)
                        .load(fotoUri)
                        .into(mHolder.image);
            }
        } else {
            if (mModel.getEspecie() != null && mModel.getEspecie().equals("Cachorro")) {
                mHolder.image.setImageResource(R.drawable.dog_silhouette);
            } else {
                mHolder.image.setImageResource(R.drawable.cat_silhouette);
            }
            mHolder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    private void bindMyAnimalsHolder(Animal mModel, MyAnimalsHolder mHolder) {
        mHolder.textNome.setText(mModel.getNome());

        if (mModel.getNovos_interessados() == 1)
            mHolder.textInteressados.setText(mModel.getNovos_interessados().toString() + " novo interessado");
        else
            mHolder.textInteressados.setText(mModel.getNovos_interessados().toString() + " novos interessados");

        if (mModel.getNovos_interessados() == 0)
            mHolder.iconError.setVisibility(View.GONE);

        List<String> categoriaArray = new ArrayList<>();
        if (mModel.getCadastro_adocao()) categoriaArray.add("Adoção");
        if (mModel.getCadastro_apadrinhar()) categoriaArray.add("Apadrinhamento");
        if (mModel.getCadastro_ajuda()) categoriaArray.add("Ajuda");

        mHolder.textCategoria.setText(TextUtils.join(" | ", categoriaArray));

        String fotos = mModel.getFotos();

        if (fotos != null && !fotos.isEmpty()) {
            List<String> fotosList = Arrays.asList(fotos.split(","));

            if (fotosList.size() > 0) {
                Uri fotoUri = Uri.parse(fotosList.get(0));
                Glide.with(context)
                        .load(fotoUri)
                        .into(mHolder.image);
            }
        } else {
            if (mModel.getEspecie() != null && mModel.getEspecie().equals("Cachorro")) {
                mHolder.image.setImageResource(R.drawable.dog_silhouette);
            } else {
                mHolder.image.setImageResource(R.drawable.cat_silhouette);
            }
            mHolder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (acao.equals("Meus Pets")) {
            return VIEW_TYPE_OWN;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    public class AnimalsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView card;

        TextView textNome;
        ImageButton buttonFav;
        ImageView image;

        TextView textSexo;
        TextView textIdade;
        TextView textPorte;
        TextView textLocalizacao;

        private AnimalsHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_animal);
            textNome = itemView.findViewById(R.id.nome);
            buttonFav = itemView.findViewById(R.id.button_fav);
            image = itemView.findViewById(R.id.image_animal);
            textSexo = itemView.findViewById(R.id.sexo);
            textIdade = itemView.findViewById(R.id.idade);
            textPorte = itemView.findViewById(R.id.porte);
            textLocalizacao = itemView.findViewById(R.id.localizacao);
            itemView.setOnClickListener(this);
            buttonFav.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            if (v.getId() == buttonFav.getId()) {
                buttonFav.setSelected(!buttonFav.isSelected());
                mOnClickListener.onListAnimalFavClick((Animal) getItem(clickedPosition), buttonFav.isSelected());
            } else {
                mOnClickListener.onListAnimalClick((Animal) getItem(clickedPosition));
            }
        }
    }

    public class MyAnimalsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textNome;
        ImageButton iconError;
        ImageView image;

        TextView textInteressados;
        TextView textCategoria;

        private MyAnimalsHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.nome);
            iconError = itemView.findViewById(R.id.icon_error);
            image = itemView.findViewById(R.id.image_animal);
            textInteressados = itemView.findViewById(R.id.interessados);
            textCategoria = itemView.findViewById(R.id.categoria);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListAnimalClick((Animal) getItem(clickedPosition));
        }
    }
}

package com.unb.meau.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.unb.meau.R;
import com.unb.meau.fragments.ListStoryFragment;
import com.unb.meau.objects.Story;

import java.util.Arrays;
import java.util.List;

public class CustomStoryFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter {

    private static final String TAG = "StoryFirestoreRecycler";

    final private ListStoryClickListener mOnClickListener;

    private ListStoryFragment context;

    public CustomStoryFirestoreRecyclerAdapter(ListStoryFragment context, @NonNull FirestoreRecyclerOptions options, ListStoryClickListener listener) {
        super(options);
        this.context = context;
        this.mOnClickListener = listener;
    }

    public interface ListStoryClickListener {
        void onListStoryClick(Story story);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_story_item, parent, false);

        return new StorysHolder(view);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

        Story mModel = (Story) model;

        final StorysHolder mHolder = (StorysHolder) holder;

        mHolder.textNome.setText(mModel.getNome());

        switch (mModel.getTipo()) {
            case "adocao":
                mHolder.textData.setText("Adotado(a) em " + mModel.getData());
                mHolder.textContador.setText("História contada pelo(a) adotante " + mModel.getUser());
                break;
            case "ajuda":
                mHolder.textData.setText("Ajudado(a) em " + mModel.getData());
                mHolder.textContador.setText("História contada pelo(a) doador(a) " + mModel.getUser());
                break;
            case "apadrinhamento":
                mHolder.textData.setText("Apadrinhado(a) em " + mModel.getData());
                mHolder.textContador.setText("História contada pelo(a) padrinho(a) " + mModel.getUser());
                break;
            default:
                mHolder.textData.setText(mModel.getData());
                mHolder.textContador.setText("História por " + mModel.getUser());
                break;
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
            mHolder.image.setImageResource(R.drawable.dog_silhouette);
            mHolder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    public class StorysHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textNome;
        ImageView image;
        TextView textData;
        TextView textContador;

        private StorysHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.nome);
            image = itemView.findViewById(R.id.image_animal);
            textData = itemView.findViewById(R.id.data);
            textContador = itemView.findViewById(R.id.contador);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            mOnClickListener.onListStoryClick((Story) getItem(clickedPosition));
        }
    }
}

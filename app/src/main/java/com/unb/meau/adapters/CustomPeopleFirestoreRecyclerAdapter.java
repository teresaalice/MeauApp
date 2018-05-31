package com.unb.meau.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.unb.meau.R;
import com.unb.meau.fragments.ListPeopleFragment;
import com.unb.meau.objects.User;

public class CustomPeopleFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter {

    private static final String TAG = "CustomPeopleFirestoreRecycler";

    final private ListUserClickListener mOnClickListener;

    private ListPeopleFragment context;

    public CustomPeopleFirestoreRecyclerAdapter(ListPeopleFragment context, @NonNull FirestoreRecyclerOptions options, ListUserClickListener listener) {
        super(options);
        this.context = context;
        this.mOnClickListener = listener;
    }

    public interface ListUserClickListener {
        void onListUserClick(User people);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_pessoa_grid_item, parent, false);

        return new PeoplesHolder(view);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

        User mModel = (User) model;
        PeoplesHolder mHolder = (PeoplesHolder) holder;

        mHolder.textNome.setText(mModel.getNome());
        mHolder.textIdade.setText(mModel.getIdade().toString() + " anos");

        String foto = mModel.getFoto();

        if (foto != null) {
            Glide.with(context)
                    .load(foto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mHolder.image);
        } else {
            mHolder.image.setImageResource(R.drawable.dog_silhouette);
            mHolder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    public class PeoplesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView textNome;
        TextView textIdade;

        private PeoplesHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image_people);
            textIdade = itemView.findViewById(R.id.age);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListUserClick((User) getItem(clickedPosition));
        }
    }
}

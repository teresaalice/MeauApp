package com.unb.meau.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.unb.meau.R;
import com.unb.meau.fragments.ListEventFragment;
import com.unb.meau.objects.Event;

public class CustomEventFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter {

    private static final String TAG = "EventFirestoreRecycler";

    private ListEventFragment context;

    public CustomEventFirestoreRecyclerAdapter(ListEventFragment context, @NonNull FirestoreRecyclerOptions options) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_evento_item, parent, false);

        return new EventsHolder(view);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

        Event mModel = (Event) model;
        EventsHolder mHolder = (EventsHolder) holder;

        mHolder.textTitulo.setText(mModel.getTitulo());
        mHolder.textData.setText(mModel.getData());
        mHolder.textHora.setText(mModel.getHora());
        mHolder.textLocal.setText(mModel.getLocal());
        mHolder.textInfo.setText(mModel.getInformacoes());
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    public class EventsHolder extends RecyclerView.ViewHolder {

        TextView textTitulo;
        TextView textData;
        TextView textHora;
        TextView textLocal;
        TextView textInfo;

        private EventsHolder(View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.titulo);
            textData = itemView.findViewById(R.id.data);
            textHora = itemView.findViewById(R.id.hora);
            textLocal = itemView.findViewById(R.id.local);
            textInfo = itemView.findViewById(R.id.informacoes);
        }
    }
}

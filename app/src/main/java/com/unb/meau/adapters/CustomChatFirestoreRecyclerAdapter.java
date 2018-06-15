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
import com.unb.meau.objects.Message;

public class CustomChatFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter {

    private static final String TAG = "CustomChatRecycler";

    private static final int VIEW_TYPE_OWN = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private String currentUser;

    public CustomChatFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions options, String user) {
        super(options);
        this.currentUser = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == VIEW_TYPE_OWN) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_own, parent, false);
            viewHolder = new MessageOwnHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_other, parent, false);
            viewHolder = new MessageOtherHolder(view);
        }

        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

        Message mModel = (Message) model;

        if (mModel.getReceiverId().equals(currentUser)) {
            MessageOtherHolder mHolder = (MessageOtherHolder) holder;
            mHolder.message_balloon.setText(mModel.getText());
        } else {
            MessageOwnHolder mHolder = (MessageOwnHolder) holder;
            mHolder.message_balloon.setText(mModel.getText());
        }
    }

    @Override
    public int getItemViewType(int position) {

        Message message = (Message) getItem(position);

        if (message.getReceiverId().equals(currentUser)) {
            return VIEW_TYPE_OTHER;
        } else {
            return VIEW_TYPE_OWN;
        }
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    public class MessageOwnHolder extends RecyclerView.ViewHolder {

        TextView message_balloon;

        private MessageOwnHolder(View itemView) {
            super(itemView);
            message_balloon = itemView.findViewById(R.id.message_balloon);
        }
    }

    public class MessageOtherHolder extends RecyclerView.ViewHolder {

        TextView message_balloon;

        private MessageOtherHolder(View itemView) {
            super(itemView);
            message_balloon = itemView.findViewById(R.id.message_balloon);
        }
    }
}

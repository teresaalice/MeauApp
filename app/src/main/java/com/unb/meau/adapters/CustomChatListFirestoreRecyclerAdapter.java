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
import com.unb.meau.fragments.ListChatFragment;
import com.unb.meau.objects.Chat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CustomChatListFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter {

    private static final String TAG = "CustomChatListRecycler";

    final private ListChatClickListener mOnClickListener;

    private ListChatFragment context;

    private String currentUser;

    public CustomChatListFirestoreRecyclerAdapter(ListChatFragment context, @NonNull FirestoreRecyclerOptions options, String user, ListChatClickListener listener) {
        super(options);
        this.context = context;
        this.currentUser = user;
        this.mOnClickListener = listener;
    }

    public interface ListChatClickListener {
        void onListChatClick(Chat chat);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_chat, parent, false);

        return new PeoplesHolder(view);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

        Chat mModel = (Chat) model;
        PeoplesHolder mHolder = (PeoplesHolder) holder;

        String userName = "User Name";
        HashMap<String, String> names = mModel.getUsersNames();

        for (Map.Entry<String, String> name : names.entrySet()) {
            if (!name.getKey().equals(currentUser)) {
                userName = name.getValue();
                break;
            }
        }

        mHolder.textNome.setText(userName);

        mHolder.textPreview.setText(mModel.getLastMessage());

        if (mModel.getLastMessageDate() != null) {
            Format formatter = new SimpleDateFormat("HH:mm");
            mHolder.textTime.setText(formatter.format(mModel.getLastMessageDate()));
        } else {
            mHolder.textTime.setText("");
        }

        Log.d(TAG, "currentUser: " + currentUser);

        HashMap<String, Boolean> visualized = mModel.getVisualized();
        Log.d(TAG, "visualized: " + visualized);
        for (Map.Entry<String, Boolean> visualizedUser : visualized.entrySet()) {
            if (visualizedUser.getKey().equals(currentUser)) {
                if (visualizedUser.getValue()) {
                    Log.d(TAG, "onBindViewHolder: Visualized");
                    Log.d(TAG, "onBindViewHolder: " + visualizedUser.getValue());
                    mHolder.textNome.setTextColor(context.getResources().getColor(R.color.nav_primary));
                    mHolder.textPreview.setTextColor(context.getResources().getColor(R.color.cinza_button));
                } else {
                    Log.d(TAG, "onBindViewHolder: NOT Visualized");
                    mHolder.textNome.setTextColor(context.getResources().getColor(R.color.colorPrimary2));
                    mHolder.textPreview.setTextColor(context.getResources().getColor(R.color.cinza_medio));
                }
                break;
            }
        }

        String fotoUrl = "";
        HashMap<String, String> photos = mModel.getPhotos();

        for (Map.Entry<String, String> photo : photos.entrySet()) {
            if (!photo.getKey().equals(currentUser)) {
                fotoUrl = photo.getValue();
                break;
            }
        }

        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(context)
                    .load(fotoUrl)
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
        TextView textPreview;
        TextView textTime;

        private PeoplesHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_user);
            textNome = itemView.findViewById(R.id.name);
            textPreview = itemView.findViewById(R.id.message_preview);
            textTime = itemView.findViewById(R.id.message_time);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListChatClick((Chat) getItem(clickedPosition));
        }
    }
}

package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.objects.Chat;
import com.unb.meau.objects.User;

import java.util.HashMap;

public class PerfilUsuarioFragment extends Fragment {

    private static final String TAG = "PerfilUsuarioFragment";

    ProgressBar mProgressBar;

    LinearLayout profile_layout;
    LinearLayout profile_buttons;
    LinearLayout buttons;

    Button button_chat;
    Button button_history;
    Button button_editprofile;

    User user;

    String nameUser;
    String userID;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    private String chatId;
    private String animal;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_userprofile, container, false);

        mProgressBar = view.findViewById(R.id.progress_bar);
        profile_layout = view.findViewById(R.id.profile_layout);
        profile_buttons = view.findViewById(R.id.buttons_userprofile);
        buttons = view.findViewById(R.id.buttons);

        button_chat = view.findViewById(R.id.button_profile_chat);
        button_history = view.findViewById(R.id.button_profile_history);
        button_editprofile = view.findViewById(R.id.button_profile_edit);

        profile_layout.setVisibility(View.GONE);
        buttons.setVisibility(View.GONE);

        showProgressDialog();

        button_editprofile.setVisibility(View.GONE);
        button_chat.setVisibility(View.GONE);
        button_history.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        if (bundle != null && bundle.getString("acao") != null) {
            Log.d(TAG, "onCreateView: Perfil: " + bundle.getString("acao"));
            acao = bundle.getString("acao");
            if (acao.equals("Meu perfil")) {
                userID = bundle.getString("userID");
                Log.d(TAG, "onCreateView: userID: " + userID);
            }
        } else {
            Log.d(TAG, "onCreateView: bundle null");
            acao = "profile";
        }

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("users").whereEqualTo("nome", nameUser).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        user = task.getResult().toObject(User.class);

                        if (currentUser.getUid().equals(userID)) {
                            bindData(user, true);
                        } else {
                            db.collection("processes")
                                    .whereEqualTo("participantes." + currentUser.getUid(), true)
                                    .whereEqualTo("participantes." + userID, true)
                                    .whereEqualTo("estagio", "finalizado")
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                                    bindData(user, true);
                                                } else {
                                                    bindData(user, false);
                                                }
                                            } else {
                                                Log.w(TAG, "onComplete: Error checking processes", task.getException());
                                                Toast.makeText(getActivity(), "Erro ao verificar amizade", Toast.LENGTH_SHORT).show();
                                                bindData(user, false);
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    Log.w(TAG, "onComplete: User not found", task.getException());
                    Toast.makeText(getActivity(), "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (acao.equals("Meu perfil")) {
            button_editprofile.setVisibility(View.VISIBLE);
        } else {
            button_chat.setVisibility(View.VISIBLE);
            button_history.setVisibility(View.VISIBLE);
        }

        button_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked edit profile");
                ((MainActivity) getActivity()).showEditSignUpFragment();
            }
        });

        button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked chat");
            }
        });

        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked history");
            }
        });
        return view;
    }

    private void showOrCreateChat() {

        db.collection("chat")
                .whereEqualTo(currentUser.getUid(), true)
                .whereEqualTo(user.getUserID(), true)
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        Log.d(TAG, "onComplete: chat found");
                        ((MainActivity) getActivity()).showChatFragment(task.getResult().getDocuments().get(0).getId(), user.getUserID(), (Boolean) task.getResult().getDocuments().get(0).get("blocked"));
                    } else {
                        Log.d(TAG, "onComplete: chat not found");
                        createChat();
                    }
                } else {
                    Log.w(TAG, "onComplete: Error", task.getException());
                    Toast.makeText(getActivity(), "Erro procurar chat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();

        HashMap<String, Boolean> users = new HashMap<>();
        users.put(currentUser.getUid(), true);
        users.put(user.getUserID(), true);
        chat.setUsers(users);

        HashMap<String, String> usersNames = new HashMap<>();
        usersNames.put(currentUser.getUid(), currentUser.getDisplayName());
        usersNames.put(user.getUserID(), user.getNome());
        chat.setUsersNames(usersNames);

        HashMap<String, String> photos = new HashMap<>();
        photos.put(currentUser.getUid(), currentUser.getPhotoUrl().toString());
        photos.put(user.getUserID(), user.getFoto());
        chat.setPhotos(photos);

        HashMap<String, Boolean> visualized = new HashMap<>();
        visualized.put(currentUser.getUid(), false);
        visualized.put(user.getUserID(), false);
        chat.setVisualized(visualized);

        chat.setBlocked(false);

        chat.setAnimal(animal);

        chatId = currentUser.getDisplayName() + "_" + user.getNome();

        db.collection("chats").document(chatId)
                .set(chat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + chatId);
                            ((MainActivity) getActivity()).showChatFragment(chatId, user.getUserID(), false);
                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                            Toast.makeText(getActivity(), "Erro ao criar o chat", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle(nameUser);
        ((MainActivity) getActivity()).setActionBarTheme("Verde");
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).menuItemName = "";
        getActivity().invalidateOptionsMenu();
    }

    private void bindData(User user, Boolean known) {
        ImageView profile_picture = getView().findViewById(R.id.profile_image);
        TextView userName = getView().findViewById(R.id.profileinfo_user_name);
        TextView userFullname = getView().findViewById(R.id.profileinfo_user_fullname);
        TextView userAge = getView().findViewById(R.id.profileinfo_user_age);
        TextView userEmail = getView().findViewById(R.id.profileinfo_user_email);
        TextView userLocation = getView().findViewById(R.id.profileinfo_user_location);
        TextView userAddress = getView().findViewById(R.id.profileinfo_user_address);
        TextView userPhone = getView().findViewById(R.id.profileinfo_user_phone);
        TextView userUsername = getView().findViewById(R.id.profileinfo_user_username);
        TextView userHistorycount = getView().findViewById(R.id.profileinfo_user_historycount);

        if (user.getFoto() != null) {
            Glide.with(this)
                    .load(user.getFoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile_picture);
        } else {
            profile_picture.setImageResource(R.mipmap.ic_launcher_round);
        }

        userName.setText(user.getNome());
        userFullname.setText(user.getNome());
        userEmail.setText(user.getEmail());
        userUsername.setText(user.getUsername());

        if (user.getIdade() != null) {
            userAge.setVisibility(View.VISIBLE);
            userAge.setText(user.getIdade());
        } else {
            userAge.setVisibility(View.GONE);

        if (user.getCidade() != null) {
            userCity.setVisibility(View.VISIBLE);
            userCity.setText(user.getCidade());
        } else {
            userCity.setVisibility(View.GONE);
        }

        if (user.getEstado() != null) {
            userState.setVisibility(View.VISIBLE);
            userState.setText(user.getEstado());
        } else {
            userState.setVisibility(View.GONE);
        }

        if (user.getEndereco() != null) {
            userAdress.setVisibility(View.VISIBLE);
            userAdress.setText(user.getEndereco());
        } else {
            userAdress.setVisibility(View.GONE);
        }

        if (user.getTelefone() != null) {
            userPhone.setVisibility(View.VISIBLE);
            userPhone.setText(user.getTelefone());
        } else {
            userPhone.setVisibility(View.GONE);

        if (user.getHistory_count() != null) {
            userHistorycount.setVisibility(View.VISIBLE);
            userHistorycount.setText(user.getHistory_count());
        } else {
            userHistorycount.setVisibility(View.GONE);

        profile_layout.setVisibility(View.VISIBLE);
        buttons.setVisibility(View.VISIBLE);
        hideProgressDialog();


    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}

package com.unb.meau.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unb.meau.R;
import com.unb.meau.activities.MainActivity;
import com.unb.meau.adapters.ViewPagerAdapter;
import com.unb.meau.objects.Story;

import java.util.Arrays;
import java.util.List;

public class StoryFragment extends Fragment {

    private static final String TAG = "StoryFragment";

    ProgressBar mProgressBar;

    Button button_ver_historias;
    Button button_contar_historia;

    Story story;

    String nomeAnimal;
    String storyId;

    ViewPager viewPager;

    FirebaseUser currentUser;
    FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_historia, container, false);

        mProgressBar = v.findViewById(R.id.progress_bar);

        button_ver_historias = v.findViewById(R.id.button_ver_historias);
        button_contar_historia = v.findViewById(R.id.button_contar_historia);
        viewPager = v.findViewById(R.id.fotos_animal);

        showProgressDialog();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        if (bundle == null) {
            Log.d(TAG, "onCreate: bundle null");
            return v;
        }

        if (bundle.getString("story_id") == null || bundle.getString("animal_name") == null) {
            Log.d(TAG, "onCreate: missing arguments");
            return v;
        }

        storyId = bundle.getString("story_id");
        nomeAnimal = bundle.getString("animal_name");

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("story").whereEqualTo("storyId", storyId).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0) {
                        story = task.getResult().getDocuments().get(0).toObject(Story.class);
                        bindData(story);
                    }
                } else {
                    Log.w(TAG, "onComplete: Story not found", task.getException());
                    Toast.makeText(getActivity(), "História não encontrada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_ver_historias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_ver_historias");
                ((MainActivity) getActivity()).showListarHistoriasFragment();
            }
        });

        button_contar_historia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button_contar_historia");
                ((MainActivity) getActivity()).showContarHistoriaFragment();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle(nomeAnimal);
        ((MainActivity) getActivity()).setActionBarTheme("Verde");

        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_share).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: share");

        int id = item.getItemId();

        if (id == R.id.action_share)
            ((MainActivity) getActivity()).shareText(getShareText());

        return super.onOptionsItemSelected(item);
    }

    private String getShareText() {
        Log.d(TAG, "getShareText");

        String text = "";

        switch (story.getTipo()) {
            case "adocao":
                text = "História da adoção de " + story.getNome();
                break;
            case "apadrinhamento":
                text = "História do apadrinhamento de " + story.getNome();
                break;
            case "ajuda":
                text = "História da ajuda de " + story.getNome();
                break;
        }

        text = text + "\nContada por " + story.getUser();
        text = text + "\n\n" + story.getData();
        text = text + "\n\n" + story.getHistoria();

        return text;
    }

    private void bindData(Story story) {

        TextView nome = getView().findViewById(R.id.nome);
        TextView historia = getView().findViewById(R.id.historia);

        String fotos = story.getFotos();

        if (fotos != null && !fotos.isEmpty()) {
            List<String> fotosList = Arrays.asList(fotos.split(","));
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), fotosList);
            viewPager.setAdapter(viewPagerAdapter);
        }

        nome.setText(story.getNome());
        historia.setText(story.getHistoria());

        hideProgressDialog();
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }
}

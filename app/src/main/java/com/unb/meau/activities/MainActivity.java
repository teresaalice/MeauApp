package com.unb.meau.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unb.meau.R;
import com.unb.meau.adapters.CustomExpandableListAdapter;
import com.unb.meau.fragments.CadastroAnimalFragment;
import com.unb.meau.fragments.IntroFragment;
import com.unb.meau.fragments.ListFragment;
import com.unb.meau.fragments.NotLoggedFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String FRAGMENT_INTRO_TAG = "FRAGMENT_INTRO_TAG";
    private static final String FRAGMENT_CADASTRO_ANIMAL_TAG = "FRAGMENT_CADASTRO_ANIMAL_TAG";
    private static final String FRAGMENT_SIGN_IN_TAG = "FRAGMENT_SIGN_IN_TAG";
    private static final String FRAGMENT_LISTAR_ANIMAIS_TAG = "FRAGMENT_LISTAR_ANIMAIS_TAG";

    Fragment fragment;
    Bundle args;
    FragmentManager fragmentManager;

    public DrawerLayout drawer;

    private ExpandableListView expandableListView;
    private List<String> listTitle;
    private LinkedHashMap<String,List<String>> listItem;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();

        expandableListView = findViewById(R.id.expandable_list);

        enterFullScreen();

        fragment = new IntroFragment();

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragment, FRAGMENT_INTRO_TAG)
                .commit();

        setDrawerInfo();

    }

    public void enterFullScreen() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void exitFullScreen() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    public void setDrawerInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView fotoPerfil = navigationView.getHeaderView(0).findViewById(R.id.fotoPerfil);

        if (user != null) {
            initializeNavigationDrawer(user.getDisplayName());
            if (user.getPhotoUrl() != null) {
                Log.d(TAG, "setDrawerInfo: " + user.getPhotoUrl());
                if (fotoPerfil != null) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .into(fotoPerfil);
                }
            }
        } else {
            fotoPerfil.setImageResource(R.mipmap.ic_launcher_round);
            initializeNavigationDrawer("Usuário");
        }
        addDrawerItems();
    }

    private void initializeNavigationDrawer(String user) {

        List <String> usuario = Arrays.asList("Meu perfil", "Meus pets", "Favoritos", "Chat");
        List <String> atalhos = Arrays.asList("Cadastrar um pet", "Adotar um pet", "Ajudar um pet", "Apadrinhar um pet");
        List <String> informacoes = Arrays.asList("Dicas", "Eventos", "Legislação", "Termo de Adoção", "Histórias de adoção");
        List <String> configuracoes = Arrays.asList("Privacidade");

        listItem = new LinkedHashMap<>();
        listItem.put(user, usuario);
        listItem.put("Atalhos", atalhos);
        listItem.put("Informações", informacoes);
        listItem.put("Configurações", configuracoes);

        listTitle = new ArrayList<>(listItem.keySet());
    }

    private void addDrawerItems() {
        ExpandableListAdapter expandableListViewAdapter = new CustomExpandableListAdapter(this, listTitle, listItem);

        expandableListView.setAdapter(expandableListViewAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (listItem.get(listTitle.get(groupPosition)))).get(childPosition).toString();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                if (mAuth.getCurrentUser() == null) {
                    signIn();
                } else {
                    switch (selectedItem) {
                        case "Cadastrar um pet":
                            fragment = new CadastroAnimalFragment();
                            fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, fragment, FRAGMENT_CADASTRO_ANIMAL_TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case "Adotar um pet":
                            listarAnimais("Adotar");
                            break;
                        case "Ajudar um pet":
                            listarAnimais("Ajudar");
                            break;
                        case "Apadrinhar um pet":
                            listarAnimais("Apadrinhar");
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem search = menu.findItem(R.id.action_search);
//        search.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivityForResult(intent, 0);
//            return true;
//        }

    }

    public void onClick(View view) {
        Toast.makeText(this, "Sair", Toast.LENGTH_SHORT).show();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void signIn() {
        fragment = new NotLoggedFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragment, FRAGMENT_SIGN_IN_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void listarAnimais(String acao) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putString("acao", acao);
        fragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}

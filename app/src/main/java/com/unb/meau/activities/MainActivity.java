package com.unb.meau.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unb.meau.R;
import com.unb.meau.adapters.CustomExpandableListAdapter;
import com.unb.meau.fragments.CadastroAnimalFragment;
import com.unb.meau.fragments.IntroFragment;
import com.unb.meau.fragments.Legislacao;
import com.unb.meau.fragments.ListFragment;
import com.unb.meau.fragments.NotLoggedFragment;
import com.unb.meau.fragments.SignInFragment;
import com.unb.meau.fragments.SignUpFragment;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_INTRO_TAG = "FRAGMENT_INTRO_TAG";
    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_CADASTRO_ANIMAL_TAG = "FRAGMENT_CADASTRO_ANIMAL_TAG";
    private static final String FRAGMENT_NOT_LOGGED_TAG = "FRAGMENT_NOT_LOGGED_TAG";
    private static final String FRAGMENT_LISTAR_ANIMAIS_TAG = "FRAGMENT_LISTAR_ANIMAIS_TAG";
    private static final String FRAGMENT_SIGN_IN_TAG = "FRAGMENT_SIGN_IN_TAG";
    private static final String FRAGMENT_SIGN_UP_TAG = "FRAGMENT_SIGN_UP_TAG";
    private static final String FRAGMENT_LEGISLACAO_TAG = "FRAGMENT_LEGISLACAO_TAG";
    public DrawerLayout drawer;
    Fragment fragment;
    FragmentManager fragmentManager;
    private LinkedHashMap<String, List<String>> listItem;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = new IntroFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragment, FRAGMENT_INTRO_TAG)
                .commit();

        mAuth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setDrawerInfo();
    }

    public void setDrawerInfo() {
        String userName = "Usuário";
        FirebaseUser user = mAuth.getCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView fotoPerfil = navigationView.getHeaderView(0).findViewById(R.id.fotoPerfil);

        if (user != null) {
            userName = user.getDisplayName();
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(fotoPerfil);
            }
        } else {
            fotoPerfil.setImageResource(R.mipmap.ic_launcher_round);
        }

        final List<String> groupNames = Arrays.asList(userName, "Atalhos", "Informações", "Configurações");

        listItem = new LinkedHashMap<>();
        listItem.put(userName, Arrays.asList("Meu perfil", "Meus pets", "Favoritos", "Chat"));
        listItem.put("Atalhos", Arrays.asList("Cadastrar um pet", "Adotar um pet", "Ajudar um pet", "Apadrinhar um pet"));
        listItem.put("Informações", Arrays.asList("Dicas", "Eventos", "Legislação", "Termo de Adoção", "Histórias de adoção"));
        listItem.put("Configurações", Arrays.asList("Privacidade"));

        ExpandableListAdapter expandableListViewAdapter = new CustomExpandableListAdapter(this, groupNames, listItem);
        ExpandableListView expandableListView = findViewById(R.id.expandable_list);
        expandableListView.setAdapter(expandableListViewAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (listItem.get(groupNames.get(groupPosition)))).get(childPosition).toString();

                drawer.closeDrawer(GravityCompat.START);

                if (mAuth.getCurrentUser() == null) {
                    showNotLoggedFragment();
                } else {
                    switch (selectedItem) {
                        case "Cadastrar um pet":
                            showCadastrarAnimalFragment();
                            break;
                        case "Adotar um pet":
                            showListarAnimaisFragment("Adotar");
                            break;
                        case "Ajudar um pet":
                            showListarAnimaisFragment("Ajudar");
                            break;
                        case "Apadrinhar um pet":
                            showListarAnimaisFragment("Apadrinhar");
                            break;
                        case "Legislação":
                            showLegislacaoFragment();
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
        // fechar aplicacao
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void showNotLoggedFragment() {
        fragment = new NotLoggedFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_NOT_LOGGED_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showSignUpFragment() {
        fragment = new SignUpFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_SIGN_UP_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showSignInFragment() {
        fragment = new SignInFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_SIGN_IN_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showCadastrarAnimalFragment() {
        fragment = new CadastroAnimalFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_CADASTRO_ANIMAL_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showListarAnimaisFragment(String acao) {
        fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putString("acao", acao);
        fragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_LISTAR_ANIMAIS_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showLegislacaoFragment(){
        fragment = new Legislacao();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragment,FRAGMENT_LEGISLACAO_TAG)
                .addToBackStack(null)
                .commit();
    }
}

package com.unb.meau.activities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.unb.meau.R;
import com.unb.meau.adapters.CustomExpandableListAdapter;
import com.unb.meau.fragments.CadastroAnimalFragment;
import com.unb.meau.fragments.CadastroAnimalSucessoFragment;
import com.unb.meau.fragments.ChatFragment;
import com.unb.meau.fragments.ContarHistoriaFragment;
import com.unb.meau.fragments.ContarHistoriaSucessoFragment;
import com.unb.meau.fragments.DicasFragment;
import com.unb.meau.fragments.FiltroFragment;
import com.unb.meau.fragments.FinalizarProcessoSucessoFragment;
import com.unb.meau.fragments.IntroducaoFragment;
import com.unb.meau.fragments.LegislacaoFragment;
import com.unb.meau.fragments.ListChatFragment;
import com.unb.meau.fragments.ListEventFragment;
import com.unb.meau.fragments.ListFragment;
import com.unb.meau.fragments.ListStoryFragment;
import com.unb.meau.fragments.NotLoggedFragment;
import com.unb.meau.fragments.PerfilUsuarioFragment;
import com.unb.meau.fragments.PrivacidadeFragment;
import com.unb.meau.fragments.RemocaoAnimalSucessoFragment;
import com.unb.meau.fragments.SentTermFragment;
import com.unb.meau.fragments.SignInFragment;
import com.unb.meau.fragments.SignUpFragment;
import com.unb.meau.fragments.StoryFragment;
import com.unb.meau.fragments.TermoAdocaoFragment;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String FRAGMENT_INTRO_TAG = "FRAGMENT_INTRO_TAG";
    public static final String FRAGMENT_CADASTRO_ANIMAL_TAG = "FRAGMENT_CADASTRO_ANIMAL_TAG";
    public static final String FRAGMENT_NOT_LOGGED_TAG = "FRAGMENT_NOT_LOGGED_TAG";
    public static final String FRAGMENT_PROFILE_TAG = "FRAGMENT_PROFILE_TAG";
    public static final String FRAGMENT_LISTAR_ANIMAIS_TAG = "FRAGMENT_LISTAR_ANIMAIS_TAG";
    public static final String FRAGMENT_SIGN_IN_TAG = "FRAGMENT_SIGN_IN_TAG";
    public static final String FRAGMENT_SIGN_UP_TAG = "FRAGMENT_SIGN_UP_TAG";
    public static final String FRAGMENT_LEGISLACAO_TAG = "FRAGMENT_LEGISLACAO_TAG";
    public static final String FRAGMENT_REMOCAO_ANIMAL_SUCESSO_TAG = "FRAGMENT_REMOCAO_ANIMAL_SUCESSO_TAG";
    public static final String FRAGMENT_CADASTRO_ANIMAL_SUCESSO_TAG = "FRAGMENT_CADASTRO_ANIMAL_SUCESSO_TAG";
    public static final String FRAGMENT_PRIVACIDADE_TAG = "FRAGMENT_PRIVACIDADE_TAG";
    public static final String FRAGMENT_TERMOS_TAG = "FRAGMENT_TERMOS_TAG";
    public static final String FRAGMENT_SENT_TERM_TAG = "FRAGMENT_SENT_TERM_TAG";
    public static final String FRAGMENT_LISTAR_PESSOAS_TAG = "FRAGMENT_LISTAR_PESSOAS_TAG";
    public static final String FRAGMENT_FILTRO_TAG = "FRAGMENT_FILTRO_TAG";
    public static final String FRAGMENT_FILTRO_ERRO_TAG = "FRAGMENT_FILTRO_ERRO_TAG";
    public static final String FRAGMENT_LISTAR_CHATS_TAG = "FRAGMENT_LISTAR_CHATS_TAG";
    public static final String FRAGMENT_CHAT_TAG = "FRAGMENT_CHAT_TAG";
    public static final String FRAGMENT_DICAS_TAG = "FRAGMENTE_DICAS_TAG";
    public static final String FRAGMENT_DICA_TAG = "FRAGMENT_DICA_TAG";
    public static final String FRAGMENT_FINALIZAR_PROCESSO_SUCESSO_TAG = "FRAGMENT_FINALIZAR_PROCESSO_SUCESSO_TAG";
    public static final String FRAGMENT_EVENTOS_TAG = "FRAGMENT_EVENTOS_TAG";
    public static final String FRAGMENT_HISTORIA_TAG = "FRAGMENT_HISTORIA_TAG";
    public static final String FRAGMENT_LISTAR_HISTORIAS_TAG = "FRAGMENT_LISTAR_HISTORIAS_TAG";
    public static final String FRAGMENT_CONTAR_HISTORIA_TAG = "FRAGMENT_CONTAR_HISTORIA_TAG";
    public static final String FRAGMENT_CONTAR_HISTORIA_SUCESSO_TAG = "FRAGMENT_CONTAR_HISTORIA_SUCESSO_TAG";

    public DrawerLayout drawer;
    Fragment fragment;
    FragmentManager fragmentManager;
    private LinkedHashMap<String, List<String>> listItem;

    public String menuItemName = "";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = new IntroducaoFragment();
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragment, FRAGMENT_INTRO_TAG)
                .commit();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setDrawerInfo();
    }

    public void setDrawerInfo() {
        String userName = "Usuário";
        final FirebaseUser user = mAuth.getCurrentUser();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView fotoPerfil = navigationView.getHeaderView(0).findViewById(R.id.fotoPerfil);

        if (user != null) {
            userName = user.getDisplayName();
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
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

                switch (selectedItem) {
                    case "Meu perfil":
                    case "Meus pets":
                    case "Cadastrar um pet":
                    case "Adotar um pet":
                    case "Ajudar um pet":
                    case "Apadrinhar um pet":
                        if (mAuth.getCurrentUser() == null) {
                            showNotLoggedFragment();
                            return false;
                        }
                        break;
                }

                switch (selectedItem) {
                    case "Meu perfil":
                        showPerfilUsuarioFragment("Meu perfil");
                        break;
                    case "Meus pets":
                        showListarAnimaisFragment("Meus Pets");
                        break;
                    case "Favoritos":
                        showListarAnimaisFragment("Favoritos");
                        break;
                    case "Chat":
                        showListChatFragment();
                        break;
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
                    case "Eventos":
                        showEventsFragment();
                        break;
                    case "Legislação":
                        showLegislacaoFragment();
                        break;
                    case "Privacidade":
                        showPrivacidadeFragment();
                        break;
                    case "Termo de Adoção":
                        showTermoAdocaoFragment();
                        break;
                    case "Dicas":
                        showDicasFragment();
                        break;
                    case "Histórias de adoção":
                        showListarHistoriasFragment();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        switch (menuItemName) {
            case "search":
                menu.findItem(R.id.action_search).setVisible(true);
                break;
            case "share":
                menu.findItem(R.id.action_share).setVisible(true);
                break;
            case "more":
                menu.findItem(R.id.action_more).setVisible(true);
                break;
            default:
                menu.findItem(R.id.action_search).setVisible(false);
                menu.findItem(R.id.action_share).setVisible(false);
                break;
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                Log.d(TAG, "onOptionsItemSelected: search");

                fragment = new FiltroFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment, FRAGMENT_FILTRO_TAG)
                        .addToBackStack(null)
                        .commit();

                return true;
            case R.id.action_share:
                Log.d(TAG, "onOptionsItemSelected: share");
                return true;
            case R.id.action_more:
                Log.d(TAG, "onOptionsItemSelected: more");

                final ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CHAT_TAG);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View mView = getLayoutInflater().inflate(R.layout.fragment_chat_dialog, null);
                TextView remover = mView.findViewById(R.id.remover_contato);
                TextView bloquear = mView.findViewById(R.id.bloquear_contato);
                TextView perfil = mView.findViewById(R.id.ver_perfil);
                Button cancelar = mView.findViewById(R.id.button_cancelar);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.CENTER);

                dialog.show();

                remover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Log.d(TAG, "onClick: remover");
                        chatFragment.removeChat();
                    }
                });

                bloquear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Log.d(TAG, "onClick: bloquear");
                        chatFragment.blockUser();
                    }
                });

                perfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Log.d(TAG, "onClick: perfil");
                        chatFragment.openUserProfile();
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                return true;
        }

        return super.onOptionsItemSelected(item);

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

    public void setActionBarTheme(String theme) {
        Toolbar actionBarToolbar = findViewById(R.id.toolbar);

        switch (theme) {
            case "Amarelo":
                actionBarToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case "Verde":
                actionBarToolbar.setBackgroundColor(getResources().getColor(R.color.verde2));
                break;
            case "Cinza":
                actionBarToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showNotLoggedFragment() {
        fragment = new NotLoggedFragment();
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

    public void showCompleteSignUpFragment() {
        fragment = new SignUpFragment();

        Bundle args = new Bundle();
        args.putBoolean("providerComplete", true);
        fragment.setArguments(args);

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
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_CADASTRO_ANIMAL_TAG)
                .addToBackStack("CADASTRAR_TAG")
                .commit();
    }

    public void showPerfilUsuarioFragment(String acao) {
        fragment = new PerfilUsuarioFragment();

        Bundle args = new Bundle();

        args.putString("acao", acao);

        if (acao.equals("Meu perfil")) {
            FirebaseUser user = mAuth.getCurrentUser();
            args.putString("nome", user.getDisplayName());
            args.putString("userID", user.getUid());
        }

        fragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_PROFILE_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showCadastrarAnimalFragment(String animalId) {
        fragment = new CadastroAnimalFragment();

        Bundle args = new Bundle();
        args.putString("animalId", animalId);
        fragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_CADASTRO_ANIMAL_TAG)
                .addToBackStack("CADASTRAR_TAG")
                .commit();
    }

    public void showListarAnimaisFragment(String acao) {
        fragment = new ListFragment();

        Bundle args = new Bundle();

        args.putString("acao", acao);

        if (acao.equals("Meus Pets") || acao.equals("Favoritos") || acao.equals("Filtro")) {
            FirebaseUser user = mAuth.getCurrentUser();
            args.putString("userID", user.getUid());
        }

        fragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_LISTAR_ANIMAIS_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showDicasFragment() {
        fragment = new DicasFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_DICAS_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void showEventsFragment() {
        fragment = new ListEventFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_EVENTOS_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showLegislacaoFragment() {
        fragment = new LegislacaoFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_LEGISLACAO_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showPrivacidadeFragment() {
        fragment = new PrivacidadeFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_PRIVACIDADE_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showTermoAdocaoFragment() {
        fragment = new TermoAdocaoFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_TERMOS_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showSentTermFragment() {
        fragment = new SentTermFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_SENT_TERM_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showContarHistoriaFragment() {
        fragment = new ContarHistoriaFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_CONTAR_HISTORIA_TAG)
                .addToBackStack("CONTAR_HISTORIA_TAG")
                .commit();
    }

    public void showListarHistoriasFragment() {
        fragment = new ListStoryFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_LISTAR_HISTORIAS_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showHistoriaFragment(String storyId, String animalNome) {

        fragment = new StoryFragment();

        Bundle args = new Bundle();
        args.putString("story_id", storyId);
        args.putString("animal_name", animalNome);
        fragment.setArguments(args);

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_HISTORIA_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void showListChatFragment() {
        fragment = new ListChatFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENT_LISTAR_CHATS_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
//        Log.d(TAG, "onBackPressed");

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);

        if (currentFragment instanceof SignUpFragment || currentFragment instanceof SignInFragment) {
            Log.d(TAG, "onBackPressed: SignIn/UpFragment");
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (currentFragment instanceof CadastroAnimalSucessoFragment) {
            Log.d(TAG, "onBackPressed: CadastroAnimalSucessoFragment");
            fragmentManager.popBackStack("CADASTRAR_TAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (currentFragment instanceof ContarHistoriaSucessoFragment) {
            Log.d(TAG, "onBackPressed: ContarHistoriaSucessoFragment");
            fragmentManager.popBackStack("CONTAR_HISTORIA_TAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (currentFragment instanceof RemocaoAnimalSucessoFragment) {
            Log.d(TAG, "onBackPressed: RemocaoAnimalSucessoFragment");
            fragmentManager.popBackStack("LIST_PERFIL_ANIMAL_TAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (currentFragment instanceof FinalizarProcessoSucessoFragment) {
            Log.d(TAG, "onBackPressed: FinalizarProcessoSucessoFragment");
            fragmentManager.popBackStack("FINALIZAR_PROCESSO_TAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (currentFragment instanceof IntroducaoFragment && !drawer.isDrawerOpen(GravityCompat.START)) {
            Log.d(TAG, "onBackPressed: IntroducaoFragment");
            drawer.openDrawer(GravityCompat.START);
            return;
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Log.d(TAG, "onBackPressed: super.onBackPressed()");
        super.onBackPressed();

    }

    public void updateToken() {
        FirebaseUser user = mAuth.getCurrentUser();
        String token = FirebaseInstanceId.getInstance().getToken();
        db.collection("users").document(user.getUid()).update("token", token);
    }
}

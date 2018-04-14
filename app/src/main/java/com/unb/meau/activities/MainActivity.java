package com.unb.meau.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.unb.meau.R;
import com.unb.meau.adapters.CustomExpandableListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ExpandableListView expandableListView;
    private List<String> listTitle;
    private LinkedHashMap<String,List<String>> listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        expandableListView = findViewById(R.id.expandable_list);

        initializeNavigationDrawer();
        addDrawerItems();

        }

    private void initializeNavigationDrawer() {

        List <String> usuario = Arrays.asList("Meu perfil", "Meus pets", "Favoritos", "Chat");
        List <String> atalhos = Arrays.asList("Cadastrar um pet", "Adotar um pet", "Ajudar um pet", "Apadrinhar um pet");
        List <String> informacoes = Arrays.asList("Dicas", "Eventos", "Legislação", "Termo de Adoção", "Histórias de adoção");
        List <String> configuracoes = Arrays.asList("Privacidade");

        listItem = new LinkedHashMap<>();
        listItem.put("User", usuario);
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
                getSupportActionBar().setTitle(selectedItem);

                Toast.makeText(MainActivity.this, selectedItem, Toast.LENGTH_SHORT).show();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
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
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivityForResult(intent, 0);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Toast.makeText(this, "Sair", Toast.LENGTH_SHORT).show();
    }

}

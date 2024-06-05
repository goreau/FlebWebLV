package com.sucen.flebweblv;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import util.MyToast;
import util.Storage;
import util.gerenciaBanco;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InicialFragment.OnFragmentInteractionListener,
        TratamentoFragment.OnFragmentInteractionListener, TratamentoDetFragment.OnFragmentInteractionListener,
        ImportaFragment.OnFragmentInteractionListener, RelImportaFragment.OnFragmentInteractionListener,
        CapturaFragment.OnFragmentInteractionListener, CapturaDetFragment.OnFragmentInteractionListener,
        AmbienteFragment.OnFragmentInteractionListener, AmbienteDetFragment.OnFragmentInteractionListener,
        ConsultaFragment.OnFragmentInteractionListener, ConsRegistrosFragment.OnFragmentInteractionListener,
        EnvioFragment.OnFragmentInteractionListener, ConfigFragment.OnFragmentInteractionListener{

    public static Context flebContext;
    gerenciaBanco gerenciador;
    private MyToast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        flebContext = getApplicationContext();
        inicial();
        int tema = Storage.recuperaI("appTema");
        if (tema>0) {
            getDelegate().setLocalNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        toast = new MyToast(this, Toast.LENGTH_SHORT);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void inicial() {
        Fragment frag = new InicialFragment();
        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, frag);
        // Committing the transaction
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void mnuSobre() {
        String versName = "FlebWebLV vs: " + BuildConfig.VERSION_NAME;
        String sobre = "Sistema para automação da coleta de informações sobre atividades do Programa de Leishmaniose Visceral.\r\nSucen";
        new AlertDialog.Builder(this)
                .setTitle(versName)
                .setMessage(sobre)
                .setCancelable(true)
                .create().show();
    }

    private void mnuTema() {
        int tema = Storage.recuperaI("appTema");
        new AlertDialog.Builder(this)
                .setTitle("Tema do App")
                .setSingleChoiceItems(R.array.temas, tema, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Storage.insere("appTema",i);
                        if (i>0) {
                            getDelegate().setLocalNightMode(
                                    AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            getDelegate().setLocalNightMode(
                                    AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.executor) {
            Fragment frag = new ConfigFragment();
            // Getting reference to the FragmentManager
            FragmentManager fragmentManager = getFragmentManager();
            // Creating a fragment transaction
            FragmentTransaction ft = fragmentManager.beginTransaction();
            // Adding a fragment to the fragment transaction
            ft.replace(R.id.content_frame, frag);
            // Committing the transaction
            ft.commit();
            return true;
        } else if (id == R.id.config) {
            mnuTema();
            return true;
        } else if (id == R.id.action_settings) {
            mnuSobre();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment rFragment = null;

        if (id == R.id.nav_ambiente) {
            rFragment = new AmbienteFragment();
        } else if (id == R.id.nav_captura) {
            rFragment = new CapturaFragment();
        } else if (id == R.id.nav_inquerito) {
            toast.show("Opção não implementada.");
            return false;
        } else if (id == R.id.nav_tratamento) {
            rFragment = new TratamentoFragment();
        } else if (id == R.id.nav_manage) {
            rFragment = new ConsultaFragment();
        } else if (id == R.id.nav_importa) {
            rFragment = new ImportaFragment();
        } else if (id == R.id.nav_sincro) {
            rFragment = new EnvioFragment();
        }
        // Creating a Bundle object
        Bundle data = new Bundle();
        // Setting the index of the currently selected item of mDrawerList
        data.putInt("position", id);
        data.putLong("id_inseto", 0);
        // Setting the position to the fragment
        rFragment.setArguments(data);

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, rFragment);

        // Committing the transaction
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Context getFlebContext(){
        return flebContext;
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    //------------------Classes Assincronas ----------------//

    private class VerificaBanco extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(flebContext);
            dialog.setMessage("Verificando base de dados...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        @Override
        protected String doInBackground(String... params) {
            gerenciador = new gerenciaBanco(getApplicationContext());
            return "Verificado";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            toast.show(result);
            gerenciador.closeDB();
        }

    }
}

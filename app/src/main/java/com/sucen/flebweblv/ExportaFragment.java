package com.sucen.flebweblv;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.JsonParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entidades.Ambiente;
import entidades.Captura;
import entidades.Tratamento;
import util.MyToast;

import webservice.Utils;
import webservice.onServiceCallCompleted;


public class ExportaFragment extends Fragment {
    static Context context;
    MyToast toast;
    private ProgressDialog load;
    private TextView tvConecta, tvResumo;
    private Button btExpo;
    private int registros = 0;
    int rec = 0;
    String webUri;
    // int tabela;
    postAsync pAsync;
    String resultado = "Enviados:\n";

    HashMap<String, String> map = new HashMap<String, String>();
    List<String> valQuart;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ExportaFragment() {
        // Required empty public constructor
    }

    public static ExportaFragment newInstance(String param1, String param2) {
        ExportaFragment fragment = new ExportaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_envio, container, false);
        setupComps(v);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupComps(View v) {
        context = getActivity();
        toast = new MyToast(context, Toast.LENGTH_SHORT);

        //Initialize Progress Dialog properties
        load = new ProgressDialog(context);
        load.setMessage("Sincronizando dados com servidor remoto. Aguarde...");
        load.setCancelable(false);

        tvConecta = (TextView) v.findViewById(R.id.tvConecta);
        tvResumo = (TextView) v.findViewById(R.id.tvResEnvio);
        btExpo = (Button) v.findViewById(R.id.btSincroniza);
        btExpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincro(v);
            }
        });

        montaDados();
        if(isConnected()){
            //  tvIsConnected.setBackgroundColor(0xFF00CC00);
            Drawable img = context.getResources().getDrawable(R.drawable.verde);
            tvConecta.setText("Conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        else{
            Drawable img = context.getResources().getDrawable(R.drawable.vermelho);
            tvConecta.setText("Não conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void sincro(View v){
        sincAmbiente();
    }

    public void sincAmbiente(){
        tvResumo.setText("Sincronizados:\n");
        final Ambiente controller = new Ambiente(0);
        controller.getAllambientes();
        if(controller.dbSyncCount() != 0){
            load.show();
            String dados = controller.composeJSONfromSQLite();
            //System.out.println(dados);
            //  webUri = "http://200.144.1.24/dados/exporta.php?tipo=vc_folha&dados=" + dados;
            //   pAsync.setmTipo("vc_folha");
            //   pAsync.setmDados(dados);
            //    pAsync.execute();
            final postAsync download = new postAsync();
            download.mTipo  = "ambiente";
            download.mDados = dados;
            //  download.context = context;
            download.mTab=1;
            download.execute();
        }else{
            //   toast.show("Visitas a imóveis: 0 registros!");
        }
        //   toast.show("Visitas a imóveis Ok!");
        sincCaptura();
        registros++;
    }


    private void sincCaptura(){

        final Captura controller = new Captura(0);
        controller.getAllcapturas();
        if(controller.dbSyncCount() != 0){
            load.show();
            String dados = controller.composeJSONfromSQLite();

            final postAsync download = new postAsync();
            download.mTipo  = "captura";
            download.mDados = dados;
            download.mTab=2;
            download.execute();
        }else{
            //  toast.show("Visita Imóveis: 0 registros!");
        }
        //  toast.show("Visita Imóveis Ok!");
        sincTratamento();
        registros++;
    }

    public void sincTratamento(){

        final Tratamento controller = new Tratamento(0);
        controller.getAlltratamentos();
        if(controller.dbSyncCount() != 0){
            load.show();
            String dados = controller.composeJSONfromSQLite();

            final postAsync download = new postAsync();
            download.mTipo  = "tratamento";
            download.mDados = dados;
            download.mTab=3;
            download.execute();

        }else{
            //   toast.show("Ovitrampa: 0 registros!");
        }
        //  toast.show("Ovitrampa Ok!");
     //   sincCoordenadas();
        registros++;
    }


    private void montaDados(){
        int quant;
        String resumo = "";

        Ambiente amb = new Ambiente(0);
        quant = amb.dbSyncCount();
        if (quant>0){
            map.put("ambiente", amb.composeJSONfromSQLite());
            resumo += "\n  Ambiente: " + quant + " registros";
        }
        Captura capt = new Captura(0);
        quant = capt.dbSyncCount();
        if (quant>0){
            map.put("captura", capt.composeJSONfromSQLite());
            resumo += "\n  Capturas: " + quant + " registros";
        }
        Tratamento trat = new Tratamento(0);
        quant = trat.dbSyncCount();
        if (quant>0){
            map.put("tratamento", trat.composeJSONfromSQLite());
            resumo += "\n  VTratamentos: " + quant + " registros";
        }

        tvResumo.setText(tvResumo.getText().toString() + resumo);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class GetJson extends AsyncTask<Void, Void, String> {
        private Context context;
        private int tab;
        @Override
        protected void onPreExecute(){
            load.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Utils util = new Utils(context);
            return util.sendInformacao(webUri,tab);
        }

        @Override
        protected void onPostExecute(String result){
            tvResumo.append(result+"\n");
            load.dismiss();
        }
    }

    public class postAsync extends AsyncTask<String, Boolean, Boolean> implements onServiceCallCompleted
    {
        private onServiceCallCompleted mListener = this;
        //  private static final String URL = "http://200.144.1.24/dados/exporta.php?tipo=";
        private String mTipo;
        private String mDados;
        private int mTab;

        @Override
        protected Boolean doInBackground(final String... params)
        {
            final String url = "http://10.8.150.23/sisapi/api/recebe/dados.php?tipo="+mTipo; //"http://200.144.1.24/sisapi/api/recebe/dados.php?tipo="+mTipo;//
         //   final String url = "https://vigent.saude.sp.gov.br/sisapi/api/recebe/dados.php?tipo="+mTipo;
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try
                            {
                                Utils util = new Utils(context);
                                String retorno = util.parseRetorno(response,mTab);
                                mListener.onServiceCallComplete(retorno);
                            }
                            catch (JsonParseException e)
                            {
                                Log.e(mTipo, e.getMessage());
                                tvResumo.append(e.getMessage());
                                load.dismiss();
                            }
                        }
                    }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    tvResumo.append("Erro: "+error.getMessage());
                    toast.show("Verifique se os registros foram recebidos.");
                    load.dismiss();
                }
            })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> postParams = new HashMap<>();
                    postParams.put("dados", mDados);
                    return postParams;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return true;
        }

        @Override
        public boolean onServiceCallComplete(String response)
        {
            tvResumo.append(response+"\n");
            load.dismiss();
            toast.show("Sincronização concluída!");
            return true;
        }

    }
}

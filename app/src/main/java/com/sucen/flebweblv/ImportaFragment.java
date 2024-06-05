package com.sucen.flebweblv;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import util.MyToast;
import webservice.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImportaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImportaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportaFragment extends Fragment {
    RadioGroup rgImporta;
    RadioButton rbSistema, rbCadastro;
    AutoCompleteTextView etLocal;
    Button btImporta;
    TextView tvConecta;
    Switch swJunta;

    String webUri;
    String resultado = "Recebidos:\n";
    String strJunta = "0";
    List<String> base, id_base;

    LinearLayout layCadastro;
    MyToast toast;

    static Context context;
    private ProgressDialog load;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ImportaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImportaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImportaFragment newInstance(String param1, String param2) {
        ImportaFragment fragment = new ImportaFragment();
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
        View v = inflater.inflate(R.layout.fragment_importa, container, false);
        setupComps(v);
        return v;
    }

    private void setupComps(View v) {
        this.context = PrincipalActivity.getFlebContext();
        tvConecta   = (TextView) v.findViewById(R.id.tvConecta);
        rgImporta   = (RadioGroup) v.findViewById(R.id.rgImporta);
        rbSistema   = (RadioButton) v.findViewById(R.id.rbSistema);
        swJunta     = (Switch) v.findViewById(R.id.swJunta);

        rbCadastro  = (RadioButton) v.findViewById(R.id.rbCadastro);
        etLocal     = (AutoCompleteTextView) v.findViewById(R.id.etLocal);
        btImporta   = (Button) v.findViewById(R.id.btImporta);

        layCadastro = (LinearLayout) v.findViewById(R.id.layCadastro);

        if(isConnected()){
            //  tvIsConnected.setBackgroundColor(0xFF00CC00);
            Drawable img = this.context.getResources().getDrawable(R.drawable.verde);
            tvConecta.setText("Conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        else{
            Drawable img = this.context.getResources().getDrawable(R.drawable.vermelho);
            tvConecta.setText("Não conectado");
            tvConecta.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }

        rgImporta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int chk) {
                if (chk == R.id.rbSistema){
                    layCadastro.setVisibility(View.GONE);
                } else {
                    layCadastro.setVisibility(View.VISIBLE);
                }
            }
        });

        btImporta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                strJunta = (swJunta.isChecked() ? "1" : "0");
                chamaProcessa(v);
            }
        });
        criaEntradas();
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

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void criaEntradas() {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            //carrega os xml
            InputStream in_mun = getActivity().getApplicationContext().getAssets().open("municipio.xml");
            InputStream in_idmun = getActivity().getApplicationContext().getAssets().open("id_municipio.xml");

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            //popula os List
            parser.setInput(in_mun, null);
            base = parseXML(parser);

            parser.setInput(in_idmun, null);
            id_base = parseXML(parser);

            addItemsOnLocal(base);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            //   Log.e("ERRO","Erro Pull Parser");
        } catch (IOException e) {
            e.printStackTrace();
            //  Log.e("ERRO","Erro IO");
        }
    }

    public void addItemsOnLocal(List<String> list) {
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etLocal.setAdapter(dados);
    }

    private List<String> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        String parc="";
        List<String> generico = new ArrayList<String>();

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.TEXT:
                    parc = parser.getText().trim();
                    if (!parc.equals("")){
                        generico.add(parc);
                    }
                    break;
            }
            eventType = parser.next();
        }
        return generico;
    }

    public void chamaProcessa(View v){
        String id = "1";
        int pos;
        final GetJson download = new GetJson();
        download.context = context;

        switch (rgImporta.getCheckedRadioButtonId()) {
            case R.id.rbCadastro:
                pos = base.indexOf(etLocal.getText().toString());
                id  = id_base.get(pos);
                //webUri = "http://10.8.150.23/sisapi/api/flebweb/cadastro.php?nivel=1&id=" + id;
                webUri = "https://vigent.saude.sp.gov.br/sisapi/api/flebweb/cadastro.php?nivel=1&id=" + id;
                break;
            default:
                //webUri = "http://10.8.150.23/sisapi/api/flebweb/base.php";
                webUri = "https://vigent.saude.sp.gov.br/sisapi/api/flebweb/base.php";
                break;
        }
        download.execute();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //importação
    private class GetJson extends AsyncTask<Void, Void, String> {
        private Context context;
        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(getActivity(), "Por favor, aguarde ...", "Recuperando Informações do Servidor...");
        }

        @Override
        protected String doInBackground(Void... params) {
            Utils util = new Utils(context);

            return util.getInformacao(webUri,strJunta);
        }

        @Override
        protected void onPostExecute(String result){
            mostraResultado(result);
            //Log.d("Resultado", result);
            load.dismiss();
        }
    }

    private void mostraResultado(String resultado){
        Fragment rFragment = new RelImportaFragment();
        Bundle data = new Bundle();
        // Setting the index of the currently selected item of mDrawerList
        data.putString("resultado", resultado);

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
    }

}

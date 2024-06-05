package com.sucen.flebweblv;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.util.List;

import entidades.Ambiente_det;
import entidades.Auxiliares;
import entidades.Tipo_imovel;
import util.CustomCheck;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AmbienteDetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AmbienteDetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AmbienteDetFragment extends Fragment {
    TextInputEditText etOrdem, etNumero;
    EditText etLatitude, etLongitude;

    LinearLayout laySituacao;
    TextInputEditText etHumano, etCao, etGato, etAve, etOutros;
    Spinner spPavimentacao, spTipoImovel;
    int id_pavimentacao, id_tipo_imovel;
    List<String> valPavimentacao, valTipoImovel;
    CustomCheck ccExtrato, ccAnimal, ccOrienta, ccPoda, ccCapina, ccRecolha;
    CustomCheck ccRetorno, ccProtocolo;
    RadioGroup rgSituacao;
    RadioButton rbTrab, rbFech, rbRec, rbDesab;
    Button btSalva, btVolta;
    int position;
    Long id_ambiente, id_ambienteDet;
    int MY_PERMISSIONS_REQUEST_GPS;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AmbienteDetFragment() {
        // Required empty public constructor
    }

    public static AmbienteDetFragment newInstance(String param1, String param2) {
        AmbienteDetFragment fragment = new AmbienteDetFragment();
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
            position = getArguments().getInt("position");
            id_ambiente = getArguments().getLong("id_ambiente");
            id_ambienteDet = getArguments().getLong("id_ambienteDet");
        }
        id_pavimentacao = id_tipo_imovel = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ambiente_det, container, false);
        setupComps(v);
        startGPS(v);
        return v;
    }

    private void setupComps(View v) {
        laySituacao     = (LinearLayout) v.findViewById(R.id.layAmbienteSituacao);
        etOrdem         = (TextInputEditText) v.findViewById(R.id.ambDetTxOrdem);
        etNumero        = (TextInputEditText) v.findViewById(R.id.ambDetTxNumero_casa);
        etHumano        = (TextInputEditText) v.findViewById(R.id.ambDetTxHumano);
        etCao           = (TextInputEditText) v.findViewById(R.id.ambDetTxCao);
        etGato          = (TextInputEditText) v.findViewById(R.id.ambDetTxGato);
        etAve           = (TextInputEditText) v.findViewById(R.id.ambDetTxAves);
        etOutros        = (TextInputEditText) v.findViewById(R.id.ambDetTxOutros);
        etLatitude      = (EditText) v.findViewById(R.id.ambDetTxLatitude);
        etLongitude     = (EditText) v.findViewById(R.id.ambDetTxLongitude);
        spPavimentacao  = (Spinner) v.findViewById(R.id.ambDetSpPavimento);
        spTipoImovel    = (Spinner) v.findViewById(R.id.ambDetSpTipoImovel);
        rgSituacao      = (RadioGroup) v.findViewById(R.id.ambDetRgSituacao);
        rbTrab          = (RadioButton) v.findViewById(R.id.ambDetRbTrab);
        rbFech          = (RadioButton) v.findViewById(R.id.ambDetRbFech);
        rbRec           = (RadioButton) v.findViewById(R.id.ambDetRbRec);
        rbDesab         = (RadioButton) v.findViewById(R.id.ambDetRbDesab);
        ccExtrato       = (CustomCheck) v.findViewById(R.id.ambDetCcExtrato);
        ccAnimal        = (CustomCheck) v.findViewById(R.id.ambDetCcAnimal);
        ccOrienta       = (CustomCheck) v.findViewById(R.id.ambDetCcOrientacao);
        ccPoda          = (CustomCheck) v.findViewById(R.id.ambDetCcPoda);
        ccCapina        = (CustomCheck) v.findViewById(R.id.ambDetCcCapina);
        ccRecolha       = (CustomCheck) v.findViewById(R.id.ambDetCcRecolhimento);
        ccRetorno       = (CustomCheck) v.findViewById(R.id.ambDetCcRetorno);
        ccProtocolo     = (CustomCheck) v.findViewById(R.id.ambDetCcProtocolo);

        btSalva         = (Button) v.findViewById(R.id.btSalvaAmb);
        btVolta         = (Button) v.findViewById(R.id.btVoltaAmb);

        btSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva();
            }
        });
        btVolta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment rFragment = new AmbienteFragment();
                Bundle data = new Bundle();
                data.putInt("position", 1);
                data.putLong("id_ambiente", id_ambiente);
                rFragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, rFragment);
                ft.commit();
            }
        });

        rgSituacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int bt) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(bt);
                stateLL(laySituacao,checkedRadioButton == rbTrab);
            }
        });

        addItensOnPavimentacao();
        addItensOnTipoImovel();
        if (id_ambienteDet>0){
            edita();
        }
    }

    private void stateLL(View view, boolean state){
        view.setEnabled(state);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                stateLL(child, state);
            }
        }
    }


    private void edita() {
        Ambiente_det det = new Ambiente_det(id_ambienteDet);
        etOrdem.setText(String.valueOf(det.getOrdem()));
        etNumero.setText(det.getNumero_casa());
        id_tipo_imovel = valTipoImovel.indexOf(String.valueOf(det.getId_tipo_imovel()));
        spTipoImovel.setSelection(id_tipo_imovel);
        int val = det.getId_situacao_imovel();
        rbTrab.setChecked(val==1);
        rbFech.setChecked(val==2);
        rbRec.setChecked(val==3);
        rbDesab.setChecked(val==4);
        id_pavimentacao = valPavimentacao.indexOf(String.valueOf(det.getId_aux_pavimento()));
        spPavimentacao.setSelection(id_pavimentacao);
        ccExtrato.setValue(det.getMat_organica());
        ccAnimal.setValue(det.getAbrigo_animal());
        etHumano.setText(String.valueOf(det.getNum_humano()));
        etCao.setText(String.valueOf(det.getNum_cao()));
        etGato.setText(String.valueOf(det.getNum_gato()));
        etAve.setText(String.valueOf(det.getNum_ave()));
        etOutros.setText(String.valueOf(det.getNum_outro()));
        ccOrienta.setValue(det.getOrientacao());
        ccPoda.setValue(det.getPoda());
        ccCapina.setValue(det.getCapina());
        ccRecolha.setValue(det.getRecolha());
        ccProtocolo.setValue(det.getProtocolo());
        ccRetorno.setValue(det.getRetorno());
    }

    private void addItensOnTipoImovel() {
        Tipo_imovel obj = new Tipo_imovel();

        List<String> list = obj.combo();
        valTipoImovel = obj.idTipo_imovel;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoImovel.setAdapter(dados);
       // spTipoImovel.setSelection(id_tipo_imovel);
    }

    private void addItensOnPavimentacao() {
        Auxiliares obj = new Auxiliares();

        List<String> list = obj.combo(1);
        valPavimentacao = obj.idAuxiliares;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPavimentacao.setAdapter(dados);
      //  spPavimentacao.setSelection(id_pavimentacao);
    }

    private void salva() {
        Ambiente_det amb = new Ambiente_det(id_ambienteDet);
        amb.setId_ambiente(id_ambiente);
        amb.setOrdem(Integer.valueOf(etOrdem.getText().toString()));
        amb.setNumero_casa(etNumero.getText().toString());
        int val = Integer.valueOf(valTipoImovel.get(spTipoImovel.getSelectedItemPosition()));
        amb.setId_tipo_imovel(val);
        amb.setId_situacao_imovel(rgSituacao.indexOfChild(getActivity().findViewById(rgSituacao.getCheckedRadioButtonId()))+1);
        String valor = etHumano.getText().toString();
        val = valor.matches("") ? 0 : Integer.valueOf(valor);
        amb.setNum_humano(val);
        valor = etCao.getText().toString();
        val = valor.matches("") ? 0 : Integer.valueOf(valor);
        amb.setNum_cao(val);
        valor = etGato.getText().toString();
        val = valor.matches("") ? 0 : Integer.valueOf(valor);
        amb.setNum_gato(val);
        valor = etAve.getText().toString();
        val = valor.matches("") ? 0 : Integer.valueOf(valor);
        amb.setNum_ave(val);
        valor = etOutros.getText().toString();
        val = valor.matches("") ? 0 : Integer.valueOf(valor);
        amb.setNum_outro(val);
        val = Integer.valueOf(valPavimentacao.get(spPavimentacao.getSelectedItemPosition()));
        amb.setId_aux_pavimento(val);
        amb.setMat_organica(ccExtrato.getValue());
        amb.setAbrigo_animal(ccAnimal.getValue());
        amb.setOrientacao(ccOrienta.getValue());
        amb.setPoda(ccPoda.getValue());
        amb.setCapina(ccCapina.getValue());
        amb.setRecolha(ccRecolha.getValue());
        amb.setProtocolo(ccProtocolo.getValue());
        amb.setRetorno(ccRetorno.getValue());
        amb.setLatitude(etLatitude.getText().toString());
        amb.setLongitude(etLongitude.getText().toString());
        amb.manipula();
        //se estiver inseridno, limpa os campos
        if (id_ambienteDet==0) {
            limpa();
        } else {
            btVolta.callOnClick();
        }
    }

    private void limpa() {
        int ord = Integer.valueOf(etOrdem.getText().toString())+1;
        etOrdem.setText(String.valueOf(ord));
        rgSituacao.check(R.id.ambDetRbTrab);
        spPavimentacao.setSelection(0);
        ccExtrato.setValue(2);
        ccAnimal.setValue(2);
        ccOrienta.setValue(2);
        ccPoda.setValue(2);
        ccCapina.setValue(2);
        ccRecolha.setValue(2);
        ccProtocolo.setValue(2);
        ccRetorno.setValue(2);
        etHumano.setText("");
        etCao.setText("");
        etGato.setText("");
        etAve.setText("");
        etOutros.setText("");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void startGPS(View v) {
        final View v1 = v;
        Context ctx = getActivity();
        LocationManager lManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            LocationListener lListener = new LocationListener() {

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
                @Override
                public void onLocationChanged(Location locat) {
                    updateView(locat, v1);
                }
            };
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GPS);
                }

                //  MyToast toast = new MyToast(ctx, Toast.LENGTH_SHORT);
                // toast.show("É necessário autorizar o uso do GPS");
                return;
            }
            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
            //lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lListener);
        }
    }

    public void updateView(Location locat, View v){
        DecimalFormat df = new DecimalFormat("##0.00000");

        Double latitude 	= locat.getLatitude();
        Double longitude 	= locat.getLongitude();

        etLatitude.setText(df.format(latitude));
        etLongitude.setText(df.format(longitude));
    }
}

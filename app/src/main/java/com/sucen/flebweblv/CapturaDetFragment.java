package com.sucen.flebweblv;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import entidades.Auxiliares;
import entidades.Captura_det;
import entidades.Quarteirao;


public class CapturaDetFragment extends Fragment {
    Spinner spIdentifica, spQuarteirao, spFonteIntra, spFontePeri, spLocal;
    TextInputEditText etHoraIni, etHoraFim, etAmostra, etDistancia, etOrdem;
    EditText etLat, etLong;
    RadioGroup rgMetodo, rgLocal;
    RadioButton rbArmadilha, rbColeta, rbIntra, rbPeri;
    CheckBox ckDesligada;
    Button btSalva, btVolta;
    List<String> valQuarteirao, valFonteIntra, valFontePeri, valLocal;
    LinearLayout llIntra, llPeri;
    int position, id_area, id_quarteirao;
    String id_identifica;
    int chkLoc; //so pra atualizar o valor editado
    Long id_captura, id_capturaDet;
    int MY_PERMISSIONS_REQUEST_GPS;

    private TimePickerDialog tpHora;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CapturaDetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CapturaDetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CapturaDetFragment newInstance(String param1, String param2) {
        CapturaDetFragment fragment = new CapturaDetFragment();
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
            id_captura = getArguments().getLong("id_captura");
            id_capturaDet = getArguments().getLong("id_capturaDet");
            id_area = getArguments().getInt("id_area");
            id_quarteirao = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_captura_det, container, false);
        setupComps(v);
        startGPS(v);
        return v;
    }

    private void setupComps(View v) {
        spFonteIntra    = (Spinner) v.findViewById(R.id.capDetSpFonteIntra);
        spFontePeri     = (Spinner) v.findViewById(R.id.capDetSpFontePeri);
        spLocal         = (Spinner) v.findViewById(R.id.capDetSpLocal);
        etDistancia     = (TextInputEditText) v.findViewById(R.id.capDetTxDistancia);
        spIdentifica    = (Spinner) v.findViewById(R.id.capDetSpIdentificador);
        spQuarteirao    = (Spinner) v.findViewById(R.id.capDetSpQuarteirao);
        etOrdem         = (TextInputEditText) v.findViewById(R.id.capDetTxOrdem);
        etHoraIni       = (TextInputEditText) v.findViewById(R.id.capDetTxHoraIni);
        etHoraFim       = (TextInputEditText) v.findViewById(R.id.capDetTxHoraFim);
        etHoraIni.setInputType(InputType.TYPE_NULL);
        etHoraFim.setInputType(InputType.TYPE_NULL);
        etHoraIni.setOnClickListener(onMudaHoraIni);
        etHoraFim.setOnClickListener(onMudaHoraFim);

        etAmostra       = (TextInputEditText) v.findViewById(R.id.capDetTxAmostra);
        etLat           = (EditText) v.findViewById(R.id.capDetTxLatitude);
        etLong          = (EditText) v.findViewById(R.id.capDetTxLongitude);
        rgMetodo        = (RadioGroup) v.findViewById(R.id.capDetRgMetodo);
        rbArmadilha     = (RadioButton) v.findViewById(R.id.capDetRbArmadilha);
        rbColeta        = (RadioButton) v.findViewById(R.id.capDetRbCaptura);
        rgLocal         = (RadioGroup) v.findViewById(R.id.capDetRgLocal);
        rbIntra         = (RadioButton) v.findViewById(R.id.capDetRbIntra);
        rbPeri          = (RadioButton) v.findViewById(R.id.capDetRbPeri);
        ckDesligada     = (CheckBox) v.findViewById(R.id.capDetCkDesligada);
        btVolta         = (Button) v.findViewById(R.id.btVoltaCap);
        btSalva         = (Button) v.findViewById(R.id.btSalvaCap);
        llIntra         = (LinearLayout) v.findViewById(R.id.capDetLlIntra);
        llPeri          = (LinearLayout) v.findViewById(R.id.capDetLlPeri);

        btSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva();
            }
        });
        btVolta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment rFragment = new CapturaFragment();
                Bundle data = new Bundle();
                data.putInt("position", 1);
                data.putLong("id_captura", id_captura);
                rFragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, rFragment);
                ft.commit();
            }
        });
        rgLocal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int chk = rgLocal.indexOfChild(getActivity().findViewById(rgLocal.getCheckedRadioButtonId()));
                if (chk==-1){
                    chk = chkLoc+chk;
                }
                if (chk==0){
                    llIntra.setVisibility(View.VISIBLE);
                    llPeri.setVisibility(View.GONE);
                } else {
                    llIntra.setVisibility(View.GONE);
                    llPeri.setVisibility(View.VISIBLE);
                }
            }
        });
        spIdentifica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ident = parent.getItemAtPosition(position).toString();
                addItensOnQuartFromIdent(ident);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addItensOnIdentificador(id_area);
        addItensOnQuarteirao(id_area);
        addItensOnFonteIntra();
        addItensOnFontePeri();
        addItensOnLocal();
        if(id_capturaDet>0){
            editar();
        }
    }

    private void addItensOnIdentificador(int area) {
        Quarteirao obj = new Quarteirao();

        List<String> list = obj.comboIdent(area);
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIdentifica.setAdapter(dados);
        spIdentifica.setSelection(dados.getPosition(id_identifica));
    }

    private void addItensOnQuartFromIdent(String ident) {
        Quarteirao obj = new Quarteirao();

        List<String> list = obj.comboByIdent(ident,id_area);
        valQuarteirao = obj.idQuarteirao;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spQuarteirao.setAdapter(dados);
        spQuarteirao.setSelection(valQuarteirao.indexOf(String.valueOf(id_quarteirao)));
    }

    private void editar(){
        Captura_det det = new Captura_det(id_capturaDet);
        id_identifica = Quarteirao.retIdent(det.getId_quarteirao(),PrincipalActivity.getFlebContext());
        id_quarteirao = det.getId_quarteirao();
        spQuarteirao.setSelection(valQuarteirao.indexOf(String.valueOf(id_quarteirao)));
        etOrdem.setText(String.valueOf(det.getOrdem()));
        etHoraIni.setText(det.getHora_ini());
        etHoraFim.setText(det.getHora_fim());

        int val = det.getId_metodo();
        rbArmadilha.setChecked(val==1);
        rbColeta.setChecked(val==2);
        val = det.getId_local_captura();
        chkLoc = val;
        rbIntra.setChecked(val==1);
        rbPeri.setChecked(val==2);
        etAmostra.setText(det.getAmostra());
        ckDesligada.setChecked(det.getFleb()==1);
        etLat.setText(det.getLatitude());
        etLong.setText(det.getLongitude());
        spFonteIntra.setSelection(valFonteIntra.indexOf(String.valueOf(det.getId_habito_alimentar_intra())));
        spFontePeri.setSelection(valFontePeri.indexOf(String.valueOf(det.getId_habito_alimentar_peri())));
        etDistancia.setText(String.valueOf(det.getDistancia()));
        spLocal.setSelection(valLocal.indexOf(String.valueOf(det.getId_aux_local_arm())));
    }

    View.OnClickListener onMudaHoraIni = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Calendar newCalendar = Calendar.getInstance();
            final String formato ="H:mm";
            tpHora = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hora, int min) {
                    SimpleDateFormat timeFormatter = new SimpleDateFormat(formato, Locale.US);
                    Calendar nHora = Calendar.getInstance();
                    //nHora.set(2020,7,9);
                    nHora.set(Calendar.HOUR_OF_DAY,hora);
                    nHora.set(Calendar.MINUTE,min);
                    etHoraIni.setText(timeFormatter.format(nHora.getTime()));
                }
            },newCalendar.get(Calendar.HOUR_OF_DAY),newCalendar.get(Calendar.MINUTE),true);
            tpHora.show();
        }
    };

    View.OnClickListener onMudaHoraFim = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Calendar newCalendar = Calendar.getInstance();
            final String formato = "H:mm";//esquema ? "H:mm" : "hh:mm";
            tpHora = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hora, int min) {
                    SimpleDateFormat timeFormatter = new SimpleDateFormat(formato, Locale.US);
                    Calendar nHora = Calendar.getInstance();
                    //nHora.set(2020,7,9);
                    nHora.set(Calendar.HOUR_OF_DAY,hora);
                    nHora.set(Calendar.MINUTE,min);
                    etHoraFim.setText(timeFormatter.format(nHora.getTime()));
                }
            },newCalendar.get(Calendar.HOUR_OF_DAY),newCalendar.get(Calendar.MINUTE),true);
            tpHora.show();
        }
    };


    private void addItensOnFonteIntra() {
        Auxiliares obj = new Auxiliares();

        List<String> list = obj.combo(5);
        valFonteIntra = obj.idAuxiliares;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFonteIntra.setAdapter(dados);
    }

    private void addItensOnFontePeri() {
        Auxiliares obj = new Auxiliares();

        List<String> list = obj.combo(6);
        valFontePeri = obj.idAuxiliares;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFontePeri.setAdapter(dados);
    }

    private void addItensOnLocal() {
        Auxiliares obj = new Auxiliares();

        List<String> list = obj.combo(7);
        valLocal = obj.idAuxiliares;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocal.setAdapter(dados);
    }

    private void addItensOnQuarteirao(int area) {
        Quarteirao obj = new Quarteirao();

        List<String> list = obj.combo(area);
        valQuarteirao = obj.idQuarteirao;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spQuarteirao.setAdapter(dados);
    }

    private void salva() {
        Captura_det trat = new Captura_det(id_capturaDet);
        trat.setOrdem(Integer.valueOf(etOrdem.getText().toString()));
        trat.setId_captura(id_captura);
        int val = Integer.valueOf(valQuarteirao.get(spQuarteirao.getSelectedItemPosition()));
        trat.setId_quarteirao(val);
        trat.setFant_quart(spQuarteirao.getSelectedItem().toString());
        trat.setNumero_imovel(etOrdem.getText().toString());
        trat.setHora_ini(etHoraIni.getText().toString());
        trat.setHora_fim(etHoraFim.getText().toString());
        trat.setAmostra(etAmostra.getText().toString());
        trat.setLatitude(etLat.getText().toString());
        trat.setLongitude(etLong.getText().toString());
        if (rgMetodo.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher uma metodologia de pesquisa!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        trat.setId_metodo(rgMetodo.indexOfChild(getActivity().findViewById(rgMetodo.getCheckedRadioButtonId()))+1);
        if (ckDesligada.isChecked()) {
            trat.setFleb(1);
        } else {
            trat.setFleb(0);
        }
        if (rgLocal.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário indicar o local de captura!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        int chk = rgLocal.indexOfChild(getActivity().findViewById(rgLocal.getCheckedRadioButtonId()));
        trat.setId_local_captura(chk+1);
        if (chk==0){
            val = Integer.valueOf(valFonteIntra.get(spFonteIntra.getSelectedItemPosition()));
            trat.setId_habito_alimentar_intra(val);
            trat.setId_habito_alimentar_peri(0);
            trat.setId_aux_local_arm(0);
            trat.setDistancia(0);
        } else {
            val = Integer.valueOf(valFontePeri.get(spFontePeri.getSelectedItemPosition()));
            trat.setId_habito_alimentar_peri(val);
            val = Integer.valueOf(valLocal.get(spLocal.getSelectedItemPosition()));
            trat.setId_aux_local_arm(val);
            trat.setDistancia(Integer.valueOf(etDistancia.getText().toString()));
            trat.setId_habito_alimentar_intra(0);
        }
        trat.manipula();
        //id_capturaDet = trat.getId_captura_det();
        if (id_capturaDet==0) {
            limpa();
        } else {
            btVolta.callOnClick();
        }
    }

    private void limpa() {
        int ord = Integer.valueOf(etOrdem.getText().toString())+1;
        etOrdem.setText(String.valueOf(ord));
        etOrdem.requestFocus();
        //etHoraIni.setText("");
        //etHoraFim.setText("");
        etAmostra.setText("");
        //rbArmadilha.setChecked(false);
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

        etLat.setText(df.format(latitude));
        etLong.setText(df.format(longitude));
    }
}

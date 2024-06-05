package com.sucen.flebweblv;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import entidades.Area;
import entidades.Captura;
import entidades.Municipio;
import util.EditaRegAdapter;
import util.RegistrosList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CapturaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CapturaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CapturaFragment extends Fragment {
    Spinner spMunicipio, spArea;
    TextInputEditText etData, etTempIni, etTempFim, etUmidIni, etUmidFim;
    RadioGroup rgExec, rgAtiv;
    RadioButton rbSucen, rbMunicipio, rbLe, rbPeum, rbPef, rbNot;
    CheckBox ckVento, ckChuva;
    Button btDetalhe;
    ListView lvRegistros;
    private DatePickerDialog dpData;
    private SimpleDateFormat dateFormatter;
    List<String> valMunicipio, valArea;
    EditaRegAdapter edLista;
    int position, id_area;
    Long id_captura;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CapturaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CapturaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CapturaFragment newInstance(String param1, String param2) {
        CapturaFragment fragment = new CapturaFragment();
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
            position    = getArguments().getInt("position");
            id_captura  = getArguments().getLong("id_captura");
        } else {
            id_captura  = 0L;
            position    = 1;
        }
        id_area     = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_captura, container, false);
        setupComps(v);
        calculeHeightListView();
        return v;
    }

    private void setupComps(View v) {
        etData      = (TextInputEditText) v.findViewById(R.id.capTxData);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        etData.setInputType(InputType.TYPE_NULL);
        Date now = new Date();
        etData.setText(DateFormat.format("dd-MM-yyyy",now).toString());

        etTempIni   = (TextInputEditText) v.findViewById(R.id.capTxTempIni);
        etTempFim   = (TextInputEditText) v.findViewById(R.id.capTxTempFim);
        etUmidIni   = (TextInputEditText) v.findViewById(R.id.capTxUmidIni);
        etUmidFim   = (TextInputEditText) v.findViewById(R.id.capTxUmidFim);
        spMunicipio = (Spinner) v.findViewById(R.id.capSpMunicipio);
        spArea      = (Spinner) v.findViewById(R.id.capSpArea);
        rgExec      = (RadioGroup) v.findViewById(R.id.capRgExec);
        rbSucen     = (RadioButton) v.findViewById(R.id.capRbSucen);
        rbMunicipio = (RadioButton) v.findViewById(R.id.capRbMunicipio);
        rgAtiv      = (RadioGroup) v.findViewById(R.id.capRgAtiv);
        rbLe        = (RadioButton) v.findViewById(R.id.capRbLe);
        rbPeum      = (RadioButton) v.findViewById(R.id.capRbPeum);
        rbPef       = (RadioButton) v.findViewById(R.id.capRbPef);
        rbNot       = (RadioButton) v.findViewById(R.id.capRbNotif);
        btDetalhe   = (Button) v.findViewById(R.id.capBtDetalhe);

        ckVento     = (CheckBox) v.findViewById(R.id.cbVento);
        ckChuva     = (CheckBox) v.findViewById(R.id.cbChuva);
        lvRegistros = (ListView) v.findViewById(R.id.capConsulta);
        addItensOnMunicipio();

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sel = Integer.valueOf(valMunicipio.get(position));
                addItensOnArea(sel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setDateTimeField();
        addDetalhes();

        btDetalhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (salva()) {
                    Fragment rFragment = new CapturaDetFragment();
                    // Creating a Bundle object
                    Bundle data = new Bundle();
                    // passando os valores para o prÃ³ximo form
                    data.putInt("position", 1);

                    data.putLong("id_captura", id_captura);
                    data.putInt("id_area", id_area);
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
        });

        lvRegistros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                RegistrosList item = (RegistrosList) edLista.getItem(position);
                int id_reg = item.getId();
                Fragment rFragment = new CapturaDetFragment();
                Bundle data = new Bundle();
                data.putInt("position", 1);
                data.putLong("id_captura", id_captura);
                data.putLong("id_capturaDet",id_reg);
                id_area = Integer.valueOf(valArea.get(id_area));
                data.putInt("id_area", id_area);
                rFragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, rFragment);
                ft.commit();
            }
        });
        if (id_captura>0){
            editar();
        }
    }

    private void editar() {
        Captura reg    = new Captura(id_captura);
        etData.setText(reg.getDt_cadastro().toString());
        int val = reg.getId_execucao();
        rbSucen.setChecked(val==1);
        rbMunicipio.setChecked(val==2);

        //String mun = reg.getId_municipio();
        spMunicipio.setSelection(valMunicipio.indexOf(reg.getId_municipio()+""));
        //addItensOnArea(reg.getId_municipio());
        id_area = reg.getId_area();//valArea.indexOf(String.valueOf(reg.getId_area()));
        val = reg.getId_atividade();
        rbLe.setChecked(val==11);
        rbPeum.setChecked(val==12);
        rbPef.setChecked(val==13);
        rbNot.setChecked(val==14);
        etTempIni.setText(String.valueOf(reg.getTemp_ini()));
        etTempFim.setText(String.valueOf(reg.getTemp_fim()));
        etUmidIni.setText(String.valueOf(reg.getUmid_ini()));
        etUmidFim.setText(String.valueOf(reg.getUmid_fim()));
        ckVento.setChecked(reg.getVento()==1);
        ckChuva.setChecked(reg.getChuva()==1);
    }

    private boolean salva() {
        Captura cap = new Captura(id_captura);
        cap.setDt_cadastro(etData.getText().toString());
        int val = Integer.valueOf(valMunicipio.get(spMunicipio.getSelectedItemPosition()));
        cap.setId_municipio(val);

        cap.setId_usuario(1);
        val = Integer.valueOf(valArea.get(spArea.getSelectedItemPosition()));
        cap.setId_area(val);
        id_area = val;
        cap.setFant_area(spArea.getSelectedItem().toString());
        Float fval = etTempIni.getText().toString().equals("")  ? 0L : Float.valueOf(etTempIni.getText().toString());
        cap.setTemp_ini(fval);
        fval = etTempFim.getText().toString().equals("")  ? 0L : Float.valueOf(etTempFim.getText().toString());
        cap.setTemp_fim(fval);
        val = etUmidIni.getText().toString().equals("")  ? 0 : Integer.valueOf(etUmidIni.getText().toString());
        cap.setUmid_ini(val);
        val = etUmidIni.getText().toString().equals("")  ? 0 : Integer.valueOf(etUmidIni.getText().toString());
        cap.setUmid_fim(val);
        if (rgExec.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher um orgão executor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return false;
        }
        cap.setId_execucao(rgExec.indexOfChild(getActivity().findViewById(rgExec.getCheckedRadioButtonId()))+1);
        if (rgAtiv.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher uma atividade", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return false;
        }
        cap.setId_atividade(rgAtiv.indexOfChild(getActivity().findViewById(rgAtiv.getCheckedRadioButtonId()))+12);
        cap.setStatus(0);
        if (ckVento.isChecked()) {
            cap.setVento(1);
        } else {
            cap.setVento(0);
        }
        if (ckChuva.isChecked()) {
            cap.setChuva(1);
        } else {
            cap.setChuva(0);
        }

        cap.manipula();
        id_captura = cap.getId_captura();
        return true;
    }

    private void addItensOnMunicipio() {
        Municipio mun       = new Municipio();

        List<String> list   = mun.combo();
        valMunicipio        = mun.idMun;

        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMunicipio.setAdapter(dados);
    }

    private void addItensOnArea(int mun) {
        Area obj = new Area();

        List<String> list = obj.combo(mun);
        valArea = obj.idArea;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spArea.setAdapter(dados);
        id_area = valArea.indexOf(String.valueOf(id_area));
        spArea.setSelection(id_area);
    }

    private void setDateTimeField() {
        etData.setOnClickListener(onMudaData);

        Calendar newCalendar = Calendar.getInstance();
        dpData = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etData.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    View.OnClickListener onMudaData = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            dpData.show();
        }
    };

    private void addDetalhes() {
        edLista = new EditaRegAdapter(id_captura,2);
       // Log.d("lista",edLista.toString());
        if (edLista.getCount()>0){
            lvRegistros.setAdapter(edLista);
         //   Log.d("foi", (""+edLista.getCount()));
        } else {
          //  Log.d("fodeu ","Sem registros de retorno");
        }
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

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        spArea.setSelection(1);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void calculeHeightListView() {
        int totalHeight = 0;
        if (edLista.getCount()>0) {
            ListAdapter adapter = lvRegistros.getAdapter();
            int lenght = adapter.getCount();
            for (int i = 0; i < lenght; i++) {
                View listItem = adapter.getView(i, null, lvRegistros);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = lvRegistros.getLayoutParams();
            params.height = totalHeight
                    + (lvRegistros.getDividerHeight() * (adapter.getCount() - 1));
            lvRegistros.setLayoutParams(params);
            lvRegistros.requestLayout();
        }
    }
}

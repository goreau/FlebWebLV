package com.sucen.flebweblv;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import entidades.Ambiente;
import entidades.Area;
import entidades.Captura;
import entidades.Municipio;
import entidades.Quarteirao;
import util.EditaRegAdapter;
import util.RegistrosList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AmbienteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AmbienteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AmbienteFragment extends Fragment {
    TextInputEditText etData;
    Spinner spMunicipio, spArea, spIdentifica, spQuarteirao;
    int id_area, id_quarteirao;
    int area, quart;//valores reais do registro para edição
    String id_identifica;
    List<String> valMunicipio, valArea, valQuarteirao;
    RadioGroup rgExecucao, rgAtividade, rgManejo;
    RadioButton rbSucen, rbMunicipio, rbPef, rbPeum, rbManejo, rbRotina, rbBorrif, rbAcao;
    LinearLayout llManejo;
    Button btDetalhe;
    ListView lvRegistros;
    private DatePickerDialog dpData;
    private SimpleDateFormat dateFormatter;
    EditaRegAdapter edLista;
    int position;
    Long id_ambiente;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AmbienteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AmbienteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AmbienteFragment newInstance(String param1, String param2) {
        AmbienteFragment fragment = new AmbienteFragment();
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
            id_ambiente = getArguments().getLong("id_ambiente");
        } else {
            id_ambiente = 0L;
            position    = 1;
        }
        id_area = id_quarteirao = 0;
    }

    private void edita() {
        Ambiente amb    = new Ambiente(id_ambiente);
        etData.setText(amb.getDt_cadastro().toString());
        int val = amb.getId_execucao();
        rbSucen.setChecked(val==1);
        rbMunicipio.setChecked(val==2);
        spMunicipio.setSelection(valMunicipio.indexOf(amb.getId_municipio()));
        quart = amb.getId_quarteirao();
        area = Quarteirao.retArea(quart,PrincipalActivity.getFlebContext());
        id_identifica = Quarteirao.retIdent(amb.getId_quarteirao(),PrincipalActivity.getFlebContext());

        /*val = Quarteirao.retArea(amb.getId_quarteirao(),PrincipalActivity.getFlebContext());
        id_identifica = Quarteirao.retIdent(amb.getId_quarteirao(),PrincipalActivity.getFlebContext());
        addItensOnArea(amb.getId_municipio());
        id_area = valArea.indexOf(String.valueOf(val));
        addItensOnIdentificador(id_area);
        addItensOnQuarteirao(id_area);
        id_quarteirao = valQuarteirao.indexOf(amb.getId_quarteirao());*/

        val = amb.getId_aux_atividade();
        rbPef.setChecked(val==5);
        rbPeum.setChecked(val==6);
        rbManejo.setChecked(val==7);
        val = amb.getId_aux_tipo_manejo();
        rbBorrif.setChecked(val==8);
        rbRotina.setChecked(val==9);
        rbAcao.setChecked(val==10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ambiente, container, false);
        setupComps(v);
        calculeHeightListView();
        return v;
    }

    private void setupComps(View v) {
        etData          = (TextInputEditText) v.findViewById(R.id.ambTxData);
        dateFormatter   = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        etData.setInputType(InputType.TYPE_NULL);
        Date now = new Date();
        etData.setText(DateFormat.format("dd-MM-yyyy",now).toString());

        spMunicipio     = (Spinner) v.findViewById(R.id.ambSpMunicipio);
        spArea          = (Spinner) v.findViewById(R.id.ambSpArea);
        spIdentifica    = (Spinner) v.findViewById(R.id.ambSpIdentificador);
        spQuarteirao    = (Spinner) v.findViewById(R.id.ambSpQuarteirao);
        rgExecucao      = (RadioGroup) v.findViewById(R.id.ambRgExecucao);
        rbSucen         = (RadioButton) v.findViewById(R.id.ambRbSucen);
        rbMunicipio     = (RadioButton) v.findViewById(R.id.ambRbMunicipio);
        rgAtividade     = (RadioGroup) v.findViewById(R.id.ambRgAtividade);
        rbPef           = (RadioButton) v.findViewById(R.id.ambRbPef);
        rbPeum          = (RadioButton) v.findViewById(R.id.ambRbPeum);
        rbManejo        = (RadioButton) v.findViewById(R.id.ambRbManejo);
        rgManejo        = (RadioGroup) v.findViewById(R.id.ambRgManejo);
        rbRotina        = (RadioButton) v.findViewById(R.id.ambRbRotina);
        rbBorrif        = (RadioButton) v.findViewById(R.id.ambRbBorrif);
        rbAcao          = (RadioButton) v.findViewById(R.id.ambRbRotina);
        llManejo        = (LinearLayout) v.findViewById(R.id.ambLlManejo);

        btDetalhe   = (Button) v.findViewById(R.id.ambBtDetalhe);
        lvRegistros = (ListView) v.findViewById(R.id.ambConsulta);

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sel     = Integer.valueOf(valMunicipio.get(position));
                addItensOnArea(sel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int sel = Integer.valueOf(valArea.get(position));
                addItensOnIdentificador(sel);
                addItensOnQuarteirao(sel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        rgAtividade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int chk = rgAtividade.indexOfChild(getActivity().findViewById(rgAtividade.getCheckedRadioButtonId()));
                if (chk==2){
                    llManejo.setVisibility(View.VISIBLE);
                } else {
                    llManejo.setVisibility(View.GONE);
                }
            }
        });

        addItensOnMunicipio();
        setDateTimeField();
        addDetalhes();

        btDetalhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (salva()) {
                    Fragment rFragment = new AmbienteDetFragment();
                    // Creating a Bundle object
                    Bundle data = new Bundle();
                    // passando os valores para o próximo form
                    data.putInt("position", 1);
                    data.putLong("id_ambiente", id_ambiente);
                    data.putLong("id_ambienteDet", 0);
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
                Fragment rFragment = new AmbienteDetFragment();
                // Creating a Bundle object
                Bundle data = new Bundle();
                // passando os valores para o próximo form
                data.putInt("position", 1);
                data.putLong("id_ambiente", id_ambiente);
                data.putLong("id_ambienteDet",id_reg);
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
        });
        if (id_ambiente>0) {
            edita();
        }
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

    private boolean salva() {
        Ambiente cap = new Ambiente(id_ambiente);
        cap.setDt_cadastro(etData.getText().toString());
        int val = Integer.valueOf(valMunicipio.get(spMunicipio.getSelectedItemPosition()));
        cap.setId_municipio(val);

        cap.setId_usuario(1);
        val = Integer.valueOf(valQuarteirao.get(spQuarteirao.getSelectedItemPosition()));
        //Log.d("Quadra:","id "+val);
        cap.setId_quarteirao(val);
        cap.setFant_quart(spQuarteirao.getSelectedItem().toString());
        if (rgExecucao.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher um orgão executor!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return false;
        }
        cap.setId_execucao(rgExecucao.indexOfChild(getActivity().findViewById(rgExecucao.getCheckedRadioButtonId()))+1);
        if (rgAtividade.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher uma atividade!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return false;
        }
        int chk = rgAtividade.indexOfChild(getActivity().findViewById(rgAtividade.getCheckedRadioButtonId()));
        cap.setId_aux_atividade(chk + 5);
        if (chk==7){
            cap.setId_aux_tipo_manejo(rgManejo.indexOfChild(getActivity().findViewById(rgManejo.getCheckedRadioButtonId())) + 8);
        } else {
            cap.setId_aux_tipo_manejo(0);
        }
        cap.setStatus(0);
        cap.manipula();
        id_ambiente = cap.getId_ambiente();
        return true;
    }

    private void addItensOnMunicipio() {
        Municipio mun = new Municipio();

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
        spArea.setSelection(valArea.indexOf(String.valueOf(area)));
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
        int sel = Integer.valueOf(valArea.get(spArea.getSelectedItemPosition()));
        List<String> list = obj.comboByIdent(ident,sel);
        valQuarteirao = obj.idQuarteirao;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spQuarteirao.setAdapter(dados);
        spQuarteirao.setSelection(valQuarteirao.indexOf(String.valueOf(quart)));
    }

    private void addItensOnQuarteirao(int area) {
        Quarteirao obj = new Quarteirao();

        List<String> list = obj.combo(area);
        valQuarteirao = obj.idQuarteirao;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spQuarteirao.setAdapter(dados);
        spQuarteirao.setSelection(valQuarteirao.indexOf(String.valueOf(quart)));
    }

    private void addDetalhes() {
        edLista = new EditaRegAdapter(id_ambiente,1);
        //Log.d("lista",edLista.toString());
        if (edLista.getCount()>0){
            lvRegistros.setAdapter(edLista);
           // Log.d("foi", (""+edLista.getCount()));
        } else {
           // Log.d("fodeu ","Sem registros de retorno");
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
            params.height = totalHeight + (lvRegistros.getDividerHeight() * (adapter.getCount() - 1));
            lvRegistros.setLayoutParams(params);
            lvRegistros.requestLayout();
        }
    }
}

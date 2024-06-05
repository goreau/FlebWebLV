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
import entidades.Municipio;
import entidades.Produto;
import entidades.Quarteirao;
import entidades.Tratamento;
import entidades.Tratamento_det;
import util.EditaRegAdapter;
import util.RegistrosList;
import util.Storage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TratamentoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TratamentoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TratamentoFragment extends Fragment {
    Spinner spMunicipio, spArea, spIdentifica, spQuarteirao, spProduto, spEspalhante;
    RadioGroup rgExecucao;
    RadioButton rbSucen, rbMunicipio;
    TextInputEditText etSinan, etData;
    Button btTratDet;
    ListView lvRegistros;
    private DatePickerDialog dpData;
    private SimpleDateFormat dateFormatter;
    List<String> valMunicipio, valArea, valQuarteirao, valProduto, valEspalhante;
    int id_area, id_quarteirao;
    int area, quart;//valores reais do registro para edição
    String id_identifica;
    EditaRegAdapter edLista;
    int position;
    Long id_tratamento;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TratamentoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TratamentoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TratamentoFragment newInstance(String param1, String param2) {
        TratamentoFragment fragment = new TratamentoFragment();
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
            position        = getArguments().getInt("position");
            id_tratamento   = getArguments().getLong("id_tratamento");
        } else {
            id_tratamento   = 0L;
            position        = 1;
        }
        id_area = id_quarteirao = 0;
    }

    private void setupComps(View v) {
        spMunicipio = (Spinner) v.findViewById(R.id.tratSpMunicipio);
        spArea = (Spinner) v.findViewById(R.id.tratSpArea);
        spIdentifica    = (Spinner) v.findViewById(R.id.tratSpIdentificador);
        spQuarteirao = (Spinner) v.findViewById(R.id.tratSpQuarteirao);
        spProduto = (Spinner) v.findViewById(R.id.tratSpProduto);
        spEspalhante = (Spinner) v.findViewById(R.id.tratSpEspalhante);
        rgExecucao = (RadioGroup) v.findViewById(R.id.tratRgExecucao);
        rbSucen = (RadioButton) v.findViewById(R.id.tratRbSucen);
        rbMunicipio = (RadioButton) v.findViewById(R.id.tratRbMunicipio);
        etSinan = (TextInputEditText) v.findViewById(R.id.tratTxtSinan);
        etData = (TextInputEditText) v.findViewById(R.id.tratTxtData);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        etData.setInputType(InputType.TYPE_NULL);
        Date now = new Date();
        etData.setText(DateFormat.format("dd-MM-yyyy",now).toString());

        btTratDet = (Button) v.findViewById(R.id.btTratamentoDet);
        lvRegistros = (ListView) v.findViewById(R.id.tratConsulta);

        addItensOnMunicipio();
        addItensOnProduto();
        addItensOnEspalhante();
        setDateTimeField();
        addDetalhes();

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

        btTratDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (salva()) {
                    Fragment rFragment = new TratamentoDetFragment();
                    // Creating a Bundle object
                    Bundle data = new Bundle();
                    // passando os valores para o próximo form
                    data.putInt("position", 1);


                    data.putLong("id_tratamento", id_tratamento);
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
                Fragment rFragment = new TratamentoDetFragment();
                Bundle data = new Bundle();
                data.putInt("position", 1);
                data.putLong("id_tratamento", id_tratamento);
                data.putLong("id_tratamentoDet",id_reg);
        //        id_area = Integer.valueOf(valArea.get(id_area));
          //      data.putInt("id_area", id_area);
                rFragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, rFragment);
                ft.commit();
            }
        });
        if(id_tratamento>0){
            editar();
        }
    }

    private void editar() {
        Tratamento reg = new Tratamento(id_tratamento);
        etData.setText(reg.getDt_cadastro());
        spMunicipio.setSelection(valMunicipio.indexOf(reg.getId_municipio()));
        //int val = Quarteirao.retArea(reg.getId_quarteirao(),PrincipalActivity.getFlebContext());
        quart = reg.getId_quarteirao();
        area = Quarteirao.retArea(quart,PrincipalActivity.getFlebContext());
        id_identifica = Quarteirao.retIdent(reg.getId_quarteirao(),PrincipalActivity.getFlebContext());
       // addItensOnArea(reg.getId_municipio());
        //id_area = valArea.indexOf(String.valueOf(area));
        //addItensOnIdentificador(val);
        //addItensOnQuarteirao(val);
        //id_quarteirao = valQuarteirao.indexOf(String.valueOf(reg.getId_quarteirao()));

        int val = reg.getId_execucao();
        rbSucen.setChecked(val==1);
        rbMunicipio.setChecked(val==2);

        etSinan.setText(reg.getSinan_quart());
        spProduto.setSelection(valProduto.indexOf(reg.getId_inseticida()));
        spEspalhante.setSelection(valEspalhante.indexOf(reg.getId_espalhante()));

    }

    private void addDetalhes() {
        edLista = new EditaRegAdapter(id_tratamento,4);
        //Log.d("lista",edLista.toString());
        if (edLista.getCount()>0){
            lvRegistros.setAdapter(edLista);
            //Log.d("foi", (""+edLista.getCount()));
        } else {
            //Log.d("fodeu ","Sem registros de retorno");
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
        Tratamento trat = new Tratamento(id_tratamento);
        trat.setDt_cadastro(etData.getText().toString());
        int val = Integer.valueOf(valMunicipio.get(spMunicipio.getSelectedItemPosition()));
        trat.setId_municipio(val);
        if (rgExecucao.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário escolher um orgão executor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return false;
        }
        trat.setId_execucao(rgExecucao.indexOfChild(getActivity().findViewById(rgExecucao.getCheckedRadioButtonId()))+1);
        trat.setId_usuario(Storage.recuperaI("exec"));
        val = Integer.valueOf(valQuarteirao.get(spQuarteirao.getSelectedItemPosition()));
        trat.setId_quarteirao(val);
        trat.setSinan_quart(etSinan.getText().toString());
        val = Integer.valueOf(valProduto.get(spProduto.getSelectedItemPosition()));
        trat.setId_inseticida(val);
        val = Integer.valueOf(valEspalhante.get(spEspalhante.getSelectedItemPosition()));
        trat.setId_espalhante(val);
        trat.setStatus(0);
        trat.setFant_quart(spQuarteirao.getSelectedItem().toString());
        trat.manipula();
        id_tratamento = trat.getId_tratamento();
        return true;
    }

    private void addItensOnMunicipio() {
        Municipio mun = new Municipio();

        List<String> list = mun.combo();
        valMunicipio = mun.idMun;
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

    private void addItensOnProduto() {
        Produto obj = new Produto();

        List<String> list = obj.combo(1);
        valProduto = obj.idProduto;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduto.setAdapter(dados);
    }

    private void addItensOnEspalhante() {
        Produto obj = new Produto();

        List<String> list = obj.combo(2);
        valEspalhante = obj.idProduto;
        ArrayAdapter<String> dados = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);

        dados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEspalhante.setAdapter(dados);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tratamento, container, false);
        setupComps(v);
        calculeHeightListView();
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

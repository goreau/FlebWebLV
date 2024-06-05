package com.sucen.flebweblv;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import entidades.Tratamento;
import entidades.Tratamento_det;


public class TratamentoDetFragment extends Fragment {
    LinearLayout laySituacao;
    RadioGroup rgSituacao;
    RadioButton rbTrabalhado, rbRecusa, rbFechado, rbDesabitado;
    TextInputEditText etOrdem, etIntraExist, etIntraBorrif, etIntraAcab, etIntraRec, etIntraOut;
    TextInputEditText etAbrExist, etAbrBorrif, etParExist, etParBorrif, etInset, etEspalha;
    Button btSalva, btVolta;

    int position;
    Long id_tratamento, id_tratamentoDet;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM1 = "id_tratamento";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TratamentoDetFragment() {
        // Required empty public constructor
    }

    public static TratamentoDetFragment newInstance(Long id, int pos) {
        TratamentoDetFragment fragment = new TratamentoDetFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id);
        args.putInt(ARG_PARAM2, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            id_tratamento = getArguments().getLong("id_tratamento");
            id_tratamentoDet = getArguments().getLong("id_tratamentoDet");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tratamento_det, container, false);
        setupComps(v);
        return v;
    }

    private void setupComps(View v) {
        laySituacao     = (LinearLayout) v.findViewById(R.id.layTratSituacao);
        etOrdem         = (TextInputEditText) v.findViewById(R.id.tratTxtOrdem);
        rgSituacao      = (RadioGroup) v.findViewById(R.id.tratRgSituacao);
        rbTrabalhado    = (RadioButton) v.findViewById(R.id.tratRbTrabalhado);
        rbRecusa        = (RadioButton) v.findViewById(R.id.tratRbRecusa);
        rbFechado       = (RadioButton) v.findViewById(R.id.tratRbFechado);
        rbDesabitado    = (RadioButton) v.findViewById(R.id.tratRbDesabitado);
        etIntraExist    = (TextInputEditText) v.findViewById(R.id.trabTxtExistentes);
        etIntraBorrif   = (TextInputEditText) v.findViewById(R.id.trabTxtBorrifados);
        etIntraAcab     = (TextInputEditText) v.findViewById(R.id.trabTxtAcabamento);
        etIntraRec      = (TextInputEditText) v.findViewById(R.id.trabTxtRecusa);
        etIntraOut      = (TextInputEditText) v.findViewById(R.id.trabTxtOutros);
        etAbrExist      = (TextInputEditText) v.findViewById(R.id.tratTxtAbrigoExist);
        etAbrBorrif     = (TextInputEditText) v.findViewById(R.id.tratTxtAbrigoBorrif);
        etParExist      = (TextInputEditText) v.findViewById(R.id.tratTxtParedeExist);
        etParBorrif     = (TextInputEditText) v.findViewById(R.id.tratTxtParedeBorrif);
        etInset         = (TextInputEditText) v.findViewById(R.id.tratTxtInseticida);
        etEspalha       = (TextInputEditText) v.findViewById(R.id.tratTxtEspalhante);
        btVolta         = (Button) v.findViewById(R.id.btVoltaTrat);
        btSalva         = (Button) v.findViewById(R.id.btSalvaTrat);
        btSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva();
            }
        });
        btVolta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment rFragment = new TratamentoFragment();
                Bundle data = new Bundle();
                data.putInt("position", 1);
                data.putLong("id_tratamento", id_tratamento);
                rFragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, rFragment);
                ft.commit();
            }
        });
        if(id_tratamentoDet>0){
            editar();
        }

        rgSituacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int bt) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(bt);
                stateLL(laySituacao,checkedRadioButton == rbTrabalhado);
            }
        });
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

    private void editar() {
        Tratamento_det det = new Tratamento_det(id_tratamentoDet);
        etOrdem.setText(String.valueOf(det.getOrdem()));
        int val = det.getId_situacao();
        rbTrabalhado.setChecked(val==1);
        rbRecusa.setChecked(val==3);
        rbFechado.setChecked(val==2);
        rbDesabitado.setChecked(val==4);
        etIntraExist.setText(String.valueOf(det.getIntra_comodo_existente()));
        etIntraBorrif.setText(String.valueOf(det.getIntra_comodo_borrifado()));
        etIntraAcab.setText(String.valueOf(det.getNao_borrifado_acabamento()));
        etIntraRec.setText(String.valueOf(det.getNao_borrifado_recusa()));
        etIntraOut.setText(String.valueOf(det.getNao_borrifado_outros()));
        etAbrExist.setText(String.valueOf(det.getPeri_abrigo_existente()));
        etAbrBorrif.setText(String.valueOf(det.getPeri_abrigo_borrifado()));
        etParExist.setText(String.valueOf(det.getPeri_muro()));
        etParBorrif.setText(String.valueOf(det.getPeri_outros()));
        etInset.setText(String.valueOf(det.getConsumo_inseticida()));
        etEspalha.setText(String.valueOf(det.getConsumo_espalhante()));
    }

    private void salva() {
        Tratamento_det trat = new Tratamento_det(id_tratamentoDet);
        trat.setId_tratamento(id_tratamento);
        if (etOrdem.getText().toString().equals("")){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Informe o numero de ordem do imóvel!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            etOrdem.requestFocus();
            return;
        }
        trat.setOrdem(Integer.valueOf(etOrdem.getText().toString()));
        if (rgSituacao.getCheckedRadioButtonId() == -1){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "É necessário indicar a situação do imóvel!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            rgSituacao.requestFocus();
            return;
        }
        int val = rgSituacao.indexOfChild(getActivity().findViewById(rgSituacao.getCheckedRadioButtonId()));
        trat.setId_situacao(val + 1);
        if (val == 0) {
            String valor = etIntraExist.getText().toString();
            int vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setIntra_comodo_existente(vals);
            valor = etIntraBorrif.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setIntra_comodo_borrifado(vals);
            valor = etIntraAcab.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setNao_borrifado_acabamento(vals);
            valor = etIntraRec.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setNao_borrifado_recusa(vals);
            valor = etIntraOut.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setNao_borrifado_outros(vals);
            valor = etAbrExist.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setPeri_abrigo_existente(vals);
            valor = etAbrBorrif.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setPeri_abrigo_borrifado(vals);
            valor = etParExist.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setPeri_muro(vals);
            valor = etParBorrif.getText().toString();
            vals = valor.matches("") ? 0 : Integer.valueOf(valor);
            trat.setPeri_outros(vals);
            valor = etInset.getText().toString();
            float valsf = valor.matches("") ? 0 : Float.valueOf(valor);
            trat.setConsumo_inseticida(valsf);
            valor = etInset.getText().toString();
            valsf = valor.matches("") ? 0 : Float.valueOf(valor);
            trat.setConsumo_espalhante(valsf);
        } else {
            trat.setIntra_comodo_existente(0);
            trat.setIntra_comodo_borrifado(0);
            trat.setNao_borrifado_acabamento(0);
            trat.setNao_borrifado_recusa(0);
            trat.setNao_borrifado_outros(0);
            trat.setPeri_abrigo_existente(0);
            trat.setPeri_abrigo_borrifado(0);
            trat.setPeri_muro(0);
            trat.setPeri_outros(0);
            trat.setConsumo_inseticida(0);
            trat.setConsumo_espalhante(0);
        }
        trat.manipula();
        if (id_tratamentoDet==0) {
            limpa();
        } else {
            btVolta.callOnClick();
        }
   //     id_tratamentoDet = trat.getId_tratamento_det();
    }

    private void limpa() {
        int ord = Integer.valueOf(etOrdem.getText().toString())+1;
        etOrdem.setText(String.valueOf(ord));
        etOrdem.requestFocus();
        rgSituacao.check(R.id.ambDetRbTrab);
        etIntraExist.setText("");
        etIntraBorrif.setText("");
        etIntraAcab.setText("");
        etIntraRec.setText("");
        etIntraOut.setText("");
        etAbrExist.setText("");
        etAbrBorrif.setText("");
        etParExist.setText("");
        etParBorrif.setText("");
        etInset.setText("");
        etEspalha.setText("");
        etOrdem.requestFocus();
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
}

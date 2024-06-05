package com.sucen.flebweblv;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import entidades.Ambiente;
import entidades.Captura;
import entidades.Tratamento;
import util.MyToast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsultaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsultaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultaFragment extends Fragment {
    RadioGroup rgModulo;
    Button btConsulta, btLimpa;
    ToggleButton tbTodos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConsultaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultaFragment newInstance(String param1, String param2) {
        ConsultaFragment fragment = new ConsultaFragment();
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
        View v = inflater.inflate(R.layout.fragment_consulta, container, false);
        rgModulo    = (RadioGroup) v.findViewById(R.id.consRgModulo);
        btConsulta  = (Button) v.findViewById(R.id.consBtConsulta);
        btConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mod = rgModulo.indexOfChild(getActivity().findViewById(rgModulo.getCheckedRadioButtonId()));
                abreConsulta(mod);
            }
        });

        tbTodos     = (ToggleButton) v.findViewById(R.id.tbTodos);
        btLimpa     = (Button) v.findViewById(R.id.btLimpa);
        btLimpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mod = rgModulo.indexOfChild(getActivity().findViewById(rgModulo.getCheckedRadioButtonId()));
                abreLimpeza(mod);
            }
        });
        return v;
    }

    private void abreLimpeza(int mod) {
        final String modulo = String.valueOf(mod); ;
        final int td      = (tbTodos.isChecked() ? 1 : 0);

        if (td == 1){
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Excluir tudo")
                    .setMessage("Essa opção irá excluir registros não enviados para a base oficial. Confirma essa ação?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Limpa().execute(modulo,"");
                        }

                    })
                    .setNegativeButton("Não", null)
                    .show();
        } else {
            new Limpa().execute(modulo,"WHERE status=1");
        }
    }

    private void abreConsulta(int mod) {
        if (mod==2){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Módulo não implementado!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        Fragment rFragment = null;
        rFragment = new ConsRegistrosFragment();
        Bundle data = new Bundle();
        data.putInt("modulo", mod);
        rFragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, rFragment);
        ft.commit();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class Limpa extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(PrincipalActivity.getFlebContext());
            dialog.setMessage("Apagando registros...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        @Override
        protected String doInBackground(String... params) {
            int regs = 0;
            switch (params[0]){
                case "0":
                    Ambiente tab = new Ambiente(0);
                    regs = tab.Limpar(params[1]);
                    break;
                case "1":
                    Captura tab1 = new Captura(0);
                    regs = tab1.Limpar(params[1]);
                    break;
                case "3":
                    Tratamento tab2 = new Tratamento(0);
                    regs = tab2.Limpar(params[1]);
                    break;
            }
            return (regs > 0 ? "Registros Excluídos!!" : "Nenhum registro a excluir!!");
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            MyToast toast = new MyToast(PrincipalActivity.getFlebContext(), Toast.LENGTH_LONG);
            toast.show(result);
        }
    }
}

package com.sucen.flebweblv;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import consulta.Children;
import consulta.ConsultaListAdapter;
import consulta.Grupo;
import util.gerenciaBanco;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsRegistrosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsRegistrosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsRegistrosFragment extends Fragment {
    gerenciaBanco db;
    Context context;
    SparseArray<Grupo> groups = new SparseArray<Grupo>();
    int j = 0;

    private static final String ARG_PARAM1 = "modulo";

    private int mParam1;

    private OnFragmentInteractionListener mListener;

    public ConsRegistrosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsRegistrosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsRegistrosFragment newInstance(String param1, String param2) {
        ConsRegistrosFragment fragment = new ConsRegistrosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cons_lista, container, false);

        context = PrincipalActivity.flebContext;
        db = new gerenciaBanco(context);
        switch (mParam1){
            case 0:
                createAmbiente();
                break;
            case 1:
                createCaptura();
                break;
            case 2:
                createCaptura();
                break;
            case 3:
                createTratamento();
                break;

        }

        if (j==0){
            groups.append(j, new Grupo("Nenhum registro cadastrado."));
        }
        ExpandableListView listView = (ExpandableListView) v.findViewById(R.id.listView);
        ConsultaListAdapter adapter = new ConsultaListAdapter(getActivity(), groups);
        listView.setAdapter(adapter);
        return v;
    }

    private void createAmbiente() {
        String sql="", strGrupo="", strLinha="", oldGrupo="";
        Cursor cursor;
        Grupo group = null;

        sql = "SELECT  m.nome as mun, p.dt_cadastro, p.fant_quart, a.descricao as ativ, p.status, p.id_ambiente " +
                "FROM ambiente p join ambiente_det pp using(id_ambiente) join municipio m using(id_municipio) join auxiliares a on p.id_aux_atividade=a.id_auxiliares";
        cursor = db.getWritableDatabase().rawQuery(sql, null);
       // Log.i("Registros: ",cursor.getCount()+" registros");
        if (cursor.moveToFirst()) {
            do {
                strGrupo = "Município:" + cursor.getString(0)+"\n- Atividade: "+cursor.getString(3);
                if (oldGrupo == ""){
                    oldGrupo = strGrupo;
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Quarteirão: "+cursor.getString(2);
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(4));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,0));
                } else if (strGrupo.equals(oldGrupo)){
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Quarteirão: "+cursor.getString(2);
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,0));
                } else {
                    groups.append(j++, group);
                    oldGrupo = strGrupo;
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Quarteirão: "+cursor.getString(2);
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(5));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,0));
                }
            } while (cursor.moveToNext());
            groups.append(j++, group);
        }
        db.close();
    }

    private void createCaptura() {
        String sql="", strGrupo="", strLinha="", oldGrupo="";
        Cursor cursor;
        Grupo group = null;

        sql = "SELECT  m.nome as mun, p.dt_cadastro, p.fant_area, a.descricao as ativ, p.status, p.id_captura " +
                "FROM captura p join municipio m using(id_municipio) join auxiliares a on p.id_atividade=a.id_auxiliares";
        cursor = db.getWritableDatabase().rawQuery(sql, null);
      //  Log.i("Registros: ",cursor.getCount()+" registros");
        if (cursor.moveToFirst()) {
            do {
                strGrupo = "Município:" + cursor.getString(0)+"\n- Atividade: "+cursor.getString(3);
                if (oldGrupo == ""){
                    oldGrupo = strGrupo;
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Área: "+cursor.getString(2);
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(4));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,1));
                } else if (strGrupo.equals(oldGrupo)){
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Área: "+cursor.getString(2);
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,1));
                } else {
                    groups.append(j++, group);
                    oldGrupo = strGrupo;
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Área: "+cursor.getString(2);
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(5));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,1));
                }
            } while (cursor.moveToNext());
            groups.append(j++, group);
        }
        db.close();
    }

    private void createTratamento() {
        String sql="", strGrupo="", strLinha="", oldGrupo="";
        Cursor cursor;
        Grupo group = null;

        sql = "SELECT  m.nome as mun, p.dt_cadastro, q.numero_quarteirao, p.sinan_quart, p.status, p.id_tratamento " +
                "FROM tratamento p join municipio m using(id_municipio) join quarteirao q using(id_quarteirao)";
        cursor = db.getWritableDatabase().rawQuery(sql, null);
      //  Log.i("Registros: ",cursor.getCount()+" registros");
        if (cursor.moveToFirst()) {
            do {
                strGrupo = "Município:" + cursor.getString(0)+"\n- Sinan: "+cursor.getString(3);
                if (oldGrupo == ""){
                    oldGrupo = strGrupo;
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Quarteirao: "+cursor.getString(2);
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(4));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,3));
                } else if (strGrupo.equals(oldGrupo)){
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Quarteirao: "+cursor.getString(2);
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,3));
                } else {
                    groups.append(j++, group);
                    oldGrupo = strGrupo;
                    strLinha = "- Data: " + cursor.getString(1)+"\n- Quarteirao: "+cursor.getString(2);
                    group = new Grupo(strGrupo);
                    group.setStatus(cursor.getInt(4));
                    group.children.add(new Children((long) cursor.getInt(5),strLinha,3));
                }
            } while (cursor.moveToNext());
            groups.append(j++, group);
        }
        db.close();
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
}

package entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sucen.flebweblv.PrincipalActivity;

import java.util.ArrayList;
import java.util.HashMap;

import util.MyToast;
import util.gerenciaBanco;

/**
 * Created by Acer on 03/07/2016.
 *
 */
public class Det_inquerito {
    long id_det_inquerito;
    int id_municipio, id_area, id_quarteirao, tr_execucao, tr_coleta, tr_posit, tr_negat, elisa_nr;
    int elisa_posit, elisa_negat, eutanasia, id_tipo_inquerito, id_usuario, status;
    String num_inquerito, fant_area, fant_quart, mes_ano;
    MyToast toast;
    Context context;
    
    public Det_inquerito(long id_det_inquerito) {
        this.id_det_inquerito = id_det_inquerito;
        context = PrincipalActivity.getFlebContext();
        if (id_det_inquerito>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT tr_execucao, tr_coleta, tr_posit, tr_negat, elisa_nr, elisa_posit, elisa_negat, eutanasia, id_municipio, " +
                "num_inquerito, id_tipo_inquerito, id_area, fant_area, id_quarteirao, fant_quart, mes_ano, id_usuario, status " +
                "FROM det_inquerito t where id_det_inquerito=" + this.id_det_inquerito;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.tr_execucao 	        = cursor.getInt(0);
            this.tr_coleta 	        = cursor.getInt(1);
            this.tr_posit     	    = cursor.getInt(2);
            this.tr_negat     	        = cursor.getInt(3);
            this.elisa_nr    	        = cursor.getInt(4);
            this.elisa_posit 	            = cursor.getInt(5);
            this.elisa_negat 	            = cursor.getInt(6);
            this.eutanasia 	        = cursor.getInt(7);
            this.id_municipio 	                = cursor.getInt(8);
            this.num_inquerito 	                = cursor.getString(9);
            this.id_tipo_inquerito 	            = cursor.getInt(10);
            this.id_area       	    = cursor.getInt(11);
            this.fant_area       	    = cursor.getString(12);
            this.id_quarteirao       	    = cursor.getInt(13);
            this.fant_quart       	    = cursor.getString(14);
            this.mes_ano       	    = cursor.getString(15);
            this.id_usuario 	        = cursor.getInt(16);
            this.status 		        = cursor.getInt(17);
        }
        db.close();
        cursor.close();
    }

    public boolean manipula() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        toast = new MyToast(this.context, Toast.LENGTH_SHORT);
        String msg  = "";
        int result  = 0;
        try {
            ContentValues valores = new ContentValues();
            valores.put("tr_execucao", this.tr_execucao);
            valores.put("tr_coleta", this.tr_coleta);
            valores.put("tr_posit", this.tr_posit);
            valores.put("tr_negat", this.tr_negat);
            valores.put("elisa_nr", this.elisa_nr);
            valores.put("elisa_posit", this.elisa_posit);
            valores.put("elisa_negat", this.elisa_negat);
            valores.put("eutanasia", this.eutanasia);
            valores.put("id_municipio", this.id_municipio);
            valores.put("num_inquerito", this.num_inquerito);
            valores.put("id_tipo_inquerito", this.id_tipo_inquerito);
            valores.put("id_area",this.id_area);
            valores.put("fant_area", this.fant_area);
            valores.put("id_quarteirao",this.id_quarteirao);
            valores.put("fant_quart", this.fant_quart);
            valores.put("mes_ano", this.mes_ano);
            valores.put("id_usuario", this.id_usuario);
            valores.put("status", 0);

            if (this.id_det_inquerito > 0) {
                String[] args = { Long.toString(this.id_det_inquerito) };
                result = db.getWritableDatabase().update("det_inquerito", valores, "id_det_inquerito=?", args);
                msg="Registro atualizado";
            } else {
                this.id_det_inquerito = db.getWritableDatabase().insert("det_inquerito", null, valores);
                result = (int) this.id_det_inquerito;
                msg="Registro inserido";
            }
            return true;
        } catch (SQLException e) {
            msg = e.getMessage();
            return false;
        } finally {
            db.close();
            msg = result>0 ? msg : "Erro na manipulação do registro";
            toast.show(msg);
        }
    }

    public boolean delete() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        try {
            String[] args = { Long.toString(this.id_det_inquerito) };

            db.getWritableDatabase().delete("det_inquerito", "id_det_inquerito=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllinqueritos() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_det_inquerito as id, fant_quart as texto FROM inquerito v";

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(0));
                map.put("texto", cursor.getString(1));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     *
     * @return
     */
    public String composeJSONfromSQLite() {
        gerenciaBanco db 	= new gerenciaBanco(context);

        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT id_municipio, localidade, numero_casa, situacao, id_aux_local_inquerito,  id_aux_atividade, id_usuario,"+
                " casa_tratada, peri_tratado, id_aux_inseticida, consumo_casa,  consumo_peri, dt_atendimento, id_inseto_suspeito, nao_tratado, latitude, longitude, status" +
                " FROM inquerito t WHERE status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> valores = new HashMap<>();
                valores.put("id_municipio", cursor.getString(0));
                valores.put("localidade", cursor.getString(1));

                valores.put("numero_casa", cursor.getString(2));
                valores.put("situacao", cursor.getString(3));
                valores.put("id_aux_local_inquerito",cursor.getString(4));
                valores.put("id_aux_atividade", cursor.getString(5));
                valores.put("id_usuario", cursor.getString(6));
                valores.put("casa_tratada", cursor.getString(7));
                valores.put("peri_tratado", cursor.getString(8));
                valores.put("id_aux_inseticida", cursor.getString(9));
                valores.put("consumo_casa", cursor.getString(10));
                valores.put("consumo_peri", cursor.getString(11));
                valores.put("dt_atendimento", cursor.getString(12));
                valores.put("id_inseto_suspeito", cursor.getString(13));
                valores.put("nao_tratado", cursor.getString(14));
                valores.put("latitude", cursor.getString(15));
                valores.put("longitude", cursor.getString(16));
                valores.put("status", cursor.getString(17));
                wordList.add(valores);
            } while (cursor.moveToNext());
        }
        db.close();
        Gson gson = new GsonBuilder().create();
        // Use GSON to serialize Array List to JSON
        //System.out.println(wordList);
        return gson.toJson(wordList);
    }

    public int dbSyncCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM det_inquerito where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM det_inquerito";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }
    /**
     * Update Sync status against each User ID
     * @param id
     * @param status
     */
    public void atualizaStatus(String id, String status){
        gerenciaBanco db = new gerenciaBanco(context);
        String updateQuery = "Update det_inquerito set status = '"+ status +"' where id_det_inquerito="+"'"+ id +"'";
        //Log.d("query",updateQuery);
        db.getWritableDatabase().execSQL(updateQuery);

        db.close();
    }

    public int Limpar(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        int id = 0;
        int regs=0;
        String sql = "DELETE FROM inquerito where status = 1";
        //Log.d("query",sql);
        db.getWritableDatabase().execSQL(sql);
        db.close();
        return regs;
    }
}

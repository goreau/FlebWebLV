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
import util.Storage;
import util.gerenciaBanco;

/**
 * Created by henrique on 27/06/2016.
 */
public class Ambiente {
    long id_ambiente;
    int id_municipio, id_execucao, id_quarteirao, id_aux_atividade, id_aux_tipo_manejo, id_usuario, status;
    String dt_cadastro, fant_quart;
    MyToast toast;
    Context context;

    public Ambiente(long id_ambiente) {
        this.id_ambiente = id_ambiente;
        context = PrincipalActivity.getFlebContext();
        if (id_ambiente>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_municipio, dt_cadastro, id_execucao, id_quarteirao, id_aux_atividade, id_aux_tipo_manejo, " +
                "fant_quart, id_usuario, status FROM ambiente t where id_ambiente=" + this.id_ambiente;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_municipio 	        = cursor.getInt(0);
            this.dt_cadastro 	        = cursor.getString(1);
            this.id_execucao     	    = cursor.getInt(2);
            this.id_quarteirao 	        = cursor.getInt(3);
            this.id_aux_atividade       = cursor.getInt(4);
            this.id_aux_tipo_manejo     = cursor.getInt(5);
            this.fant_quart      	    = cursor.getString(6);
            this.id_usuario 	        = cursor.getInt(7);
            this.status 		        = cursor.getInt(8);
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
            valores.put("id_municipio", this.id_municipio);
            valores.put("dt_cadastro", this.dt_cadastro);
            valores.put("id_execucao", this.id_execucao);
            valores.put("id_quarteirao", this.id_quarteirao);
            valores.put("id_aux_atividade", this.id_aux_atividade);
            valores.put("id_aux_tipo_manejo", this.id_aux_tipo_manejo);
            valores.put("fant_quart", this.fant_quart);
            valores.put("id_usuario", this.id_usuario);
            valores.put("status", 0);

            if (this.id_ambiente > 0) {
                String[] args = { Long.toString(this.id_ambiente) };
                result = db.getWritableDatabase().update("ambiente", valores, "id_ambiente=?", args);
                msg="Registro atualizado";
            } else {
                this.id_ambiente = db.getWritableDatabase().insert("ambiente", null, valores);
                result = (int) this.id_ambiente;
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
            String[] args = { Long.toString(this.id_ambiente) };

            db.getWritableDatabase().delete("ambiente", "id_ambiente=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllambientes() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_ambiente as id, fant_quart as texto FROM ambiente v";

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
        Ambiente_det det    = new Ambiente_det(0);

        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT id_municipio, dt_cadastro, id_execucao, id_quarteirao, id_aux_atividade, id_aux_tipo_manejo, " +
                "id_usuario, id_ambiente, fant_quart FROM ambiente t WHERE status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> valores = new HashMap<>();
                valores.put("id_municipio", cursor.getString(0));
                valores.put("dt_cadastro", cursor.getString(1));
                valores.put("id_execucao", cursor.getString(2));
                valores.put("id_quarteirao", cursor.getString(3));
                valores.put("id_aux_atividade",cursor.getString(4));
                valores.put("id_aux_tipo_manejo", cursor.getString(5));
                String ex = String.valueOf(Storage.recuperaI("exec"));
                valores.put("id_usuario", ex);
                valores.put("id_ambiente", cursor.getString(7));
                valores.put("fant_quart", cursor.getString(8).trim());

                valores.put("ambiente_det", det.composeJSONfromSQLite(cursor.getString(7)));
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
        String selectQuery = "SELECT  * FROM ambiente where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM ambiente";
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
        String updateQuery = "Update ambiente set status = '"+ status +"' where id_ambiente="+"'"+ id +"'";
        //Log.d("query",updateQuery);
        db.getWritableDatabase().execSQL(updateQuery);

        db.close();
    }

    public int Limpar(String filt){
        gerenciaBanco db = new gerenciaBanco(this.context);
        int id = 0;
        int regs=0;
        String sql = "SELECT id_ambiente as id FROM ambiente "+filt;
        Cursor cursor = db.getWritableDatabase().rawQuery(sql, null);
        regs = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                sql = "DELETE FROM ambiente_det where id_ambiente="+"'"+ id +"'";
                //Log.d("query",sql);
                db.getWritableDatabase().execSQL(sql);
            } while (cursor.moveToNext());
        }

        sql = "DELETE FROM ambiente "+filt;
        //Log.d("query",sql);
        db.getWritableDatabase().execSQL(sql);
        db.close();
        return regs;
    }

    public long getId_ambiente() {
        return id_ambiente;
    }

    public void setId_ambiente(long id_ambiente) {
        this.id_ambiente = id_ambiente;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public int getId_execucao() {
        return id_execucao;
    }

    public void setId_execucao(int id_execucao) {
        this.id_execucao = id_execucao;
    }

    public int getId_quarteirao() {
        return id_quarteirao;
    }

    public void setId_quarteirao(int id_quarteirao) {
        this.id_quarteirao = id_quarteirao;
    }

    public int getId_aux_atividade() {
        return id_aux_atividade;
    }

    public void setId_aux_atividade(int id_aux_atividade) {
        this.id_aux_atividade = id_aux_atividade;
    }

    public int getId_aux_tipo_manejo() {
        return id_aux_tipo_manejo;
    }

    public void setId_aux_tipo_manejo(int id_aux_tipo_manejo) {
        this.id_aux_tipo_manejo = id_aux_tipo_manejo;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDt_cadastro() {
        return dt_cadastro;
    }

    public void setDt_cadastro(String dt_cadastro) {
        this.dt_cadastro = dt_cadastro;
    }

    public String getFant_quart() {
        return fant_quart;
    }

    public void setFant_quart(String fant_quart) {
        this.fant_quart = fant_quart;
    }


}

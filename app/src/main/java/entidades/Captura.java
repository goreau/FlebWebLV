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
 * Created by Acer on 26/06/2016.
 */
public class Captura {
    long id_captura;
    int id_atividade, id_municipio, umid_ini, umid_fim, id_execucao, vento, chuva, id_area, id_usuario, status;
    String dt_cadastro, fant_area;
    float temp_ini, temp_fim;
    MyToast toast;
    Context context;

    public Captura(long id_captura) {
        this.id_captura = id_captura;
        context = PrincipalActivity.getFlebContext();
        if (id_captura>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_atividade, id_municipio, dt_cadastro, temp_ini, temp_fim, umid_ini, umid_fim, id_execucao, " +
                "vento, chuva, id_area, fant_area, id_usuario, status FROM captura t where id_captura=" + this.id_captura;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_atividade 	        = cursor.getInt(0);
            this.id_municipio 	        = cursor.getInt(1);
            this.dt_cadastro     	    = cursor.getString(2);
            this.temp_ini     	        = cursor.getFloat(3);
            this.temp_fim    	        = cursor.getFloat(4);
            this.umid_ini 	            = cursor.getInt(5);
            this.umid_fim 	            = cursor.getInt(6);
            this.id_execucao 	        = cursor.getInt(7);
            this.vento 	                = cursor.getInt(8);
            this.chuva 	                = cursor.getInt(9);
            this.id_area 	            = cursor.getInt(10);
            this.fant_area       	    = cursor.getString(11);
            this.id_usuario 	        = cursor.getInt(12);
            this.status 		        = cursor.getInt(13);
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
            valores.put("id_atividade", this.id_atividade);
            valores.put("id_municipio", this.id_municipio);
            valores.put("dt_cadastro", this.dt_cadastro);
            valores.put("temp_ini", this.temp_ini);
            valores.put("temp_fim", this.temp_fim);
            valores.put("umid_ini", this.umid_ini);
            valores.put("umid_fim", this.umid_fim);
            valores.put("id_execucao", this.id_execucao);
            valores.put("vento", this.vento);
            valores.put("chuva", this.chuva);
            valores.put("id_area", this.id_area);
            valores.put("fant_area", this.fant_area);
            valores.put("id_usuario", this.id_usuario);
            valores.put("status", 0);

            if (this.id_captura > 0) {
                String[] args = { Long.toString(this.id_captura) };
                result = db.getWritableDatabase().update("captura", valores, "id_captura=?", args);
                msg="Registro atualizado";
            } else {
                this.id_captura = db.getWritableDatabase().insert("captura", null, valores);
                result = (int) this.id_captura;
                msg="Registro inserido";
            }
            return true;
        } catch (SQLException e) {
            msg = e.getMessage();
            return false;
        } finally {
            db.close();
            msg = result>0 ? msg : "Erro na manipulaÃ§Ã£o do registro";
            toast.show(msg);
        }
    }

    public boolean delete() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        try {
            String[] args = { Long.toString(this.id_captura) };

            db.getWritableDatabase().delete("captura", "id_captura=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllcapturas() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_captura as id, fant_area as texto FROM captura v";

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
        Captura_det det     = new Captura_det(0);

        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT id_atividade, id_municipio, dt_cadastro, temp_ini, temp_fim, umid_ini, umid_fim, id_execucao, " +
                " vento, chuva, id_area, fant_area, id_usuario, status, id_captura FROM captura t WHERE status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> valores = new HashMap<>();
                valores.put("id_atividade", cursor.getString(0));
                valores.put("id_municipio", cursor.getString(1));

                valores.put("dt_cadastro", cursor.getString(2));
                valores.put("temp_ini", cursor.getString(3));
                valores.put("temp_fim",cursor.getString(4));
                valores.put("umid_ini", cursor.getString(5));
                valores.put("umid_fim", cursor.getString(6));
                valores.put("id_execucao", cursor.getString(7));
                valores.put("vento", cursor.getString(8));
                valores.put("chuva", cursor.getString(9));
                valores.put("id_area", cursor.getString(10));
                valores.put("fant_area", cursor.getString(11).trim());
                String ex = String.valueOf(Storage.recuperaI("exec"));
                valores.put("id_usuario",ex);
                valores.put("status", cursor.getString(13));
                valores.put("id_captura", cursor.getString(14));
                valores.put("captura_det", det.composeJSONfromSQLite(cursor.getString(14)));
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
        String selectQuery = "SELECT  * FROM captura where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM captura";
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
        String updateQuery = "Update captura set status = '"+ status +"' where id_captura="+"'"+ id +"'";
       // Log.d("query",updateQuery);
        db.getWritableDatabase().execSQL(updateQuery);

        db.close();
    }

    public int Limpar(String filt){
        gerenciaBanco db = new gerenciaBanco(this.context);
        int id = 0;
        int regs=0;
        String sql = "SELECT id_captura as id FROM captura "+filt;
        Cursor cursor = db.getWritableDatabase().rawQuery(sql, null);
        regs = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                sql = "DELETE FROM captura_det where id_captura="+"'"+ id +"'";
              //  Log.d("query",sql);
                db.getWritableDatabase().execSQL(sql);
            } while (cursor.moveToNext());
        }

        sql = "DELETE FROM captura "+filt;
     //   Log.d("query",sql);
        db.getWritableDatabase().execSQL(sql);
        db.close();
        return regs;
    }

    public long getId_captura() {
        return id_captura;
    }

    public void setId_captura(long id_captura) {
        this.id_captura = id_captura;
    }

    public int getId_atividade() {
        return id_atividade;
    }

    public void setId_atividade(int id_atividade) {
        this.id_atividade = id_atividade;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public int getUmid_ini() {
        return umid_ini;
    }

    public void setUmid_ini(int umid_ini) {
        this.umid_ini = umid_ini;
    }

    public int getUmid_fim() {
        return umid_fim;
    }

    public void setUmid_fim(int umid_fim) {
        this.umid_fim = umid_fim;
    }

    public int getId_execucao() {
        return id_execucao;
    }

    public void setId_execucao(int id_execucao) {
        this.id_execucao = id_execucao;
    }

    public int getVento() {
        return vento;
    }

    public void setVento(int vento) {
        this.vento = vento;
    }

    public int getChuva() {
        return chuva;
    }

    public void setChuva(int chuva) {
        this.chuva = chuva;
    }

    public int getId_area() {
        return id_area;
    }

    public void setId_area(int id_area) {
        this.id_area = id_area;
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

    public String getFant_area() {
        return fant_area;
    }

    public void setFant_area(String fant_area) {
        this.fant_area = fant_area;
    }

    public float getTemp_ini() {
        return temp_ini;
    }

    public void setTemp_ini(float temp_ini) {
        this.temp_ini = temp_ini;
    }

    public float getTemp_fim() {
        return temp_fim;
    }

    public void setTemp_fim(float temp_fim) {
        this.temp_fim = temp_fim;
    }
}

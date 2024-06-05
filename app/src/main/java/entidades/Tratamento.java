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
 * Created by Acer on 24/06/2016.
 *
 */
public class Tratamento {
    long id_tratamento;
    int id_municipio, id_execucao,  id_quarteirao, id_usuario, id_espalhante, id_inseticida, status;
    String dt_cadastro, sinan_quart, fant_quart;
    MyToast toast;
    Context context;

    public Tratamento(long id_tratamento) {
        this.id_tratamento = id_tratamento;
        context = PrincipalActivity.getFlebContext();
        if (id_tratamento>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_municipio, id_execucao, id_quarteirao, id_inseticida, id_espalhante, dt_cadastro, id_usuario, sinan_quart, status"
                + " FROM tratamento t where id_tratamento=" + this.id_tratamento;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_municipio 	        = cursor.getInt(0);
            this.id_execucao            = cursor.getInt(1);
            this.id_quarteirao 	        = cursor.getInt(2);
            this.id_inseticida          = cursor.getInt(3);
            this.id_espalhante       	= cursor.getInt(4);
            this.dt_cadastro     	    = cursor.getString(5);
            this.id_usuario 	        = cursor.getInt(6);
            this.sinan_quart 	        = cursor.getString(7);
            this.status 		        = cursor.getInt(8);
        }
        db.close();
        cursor.close();
    }

    public boolean manipula() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        toast = new MyToast(this.context, Toast.LENGTH_SHORT);
        String msg = "";
        try {
            ContentValues valores = new ContentValues();
            valores.put("id_municipio", this.id_municipio);
            valores.put("id_execucao", this.id_execucao);
            valores.put("id_quarteirao", this.id_quarteirao);
            valores.put("id_inseticida", this.id_inseticida);
            valores.put("id_espalhante", this.id_espalhante);
            valores.put("dt_cadastro", this.dt_cadastro);
            valores.put("id_usuario", this.id_usuario);
            valores.put("sinan_quart", this.sinan_quart);
            valores.put("fant_quart", this.fant_quart);
            valores.put("status", 0);

            if (this.id_tratamento > 0) {
                String[] args = { Long.toString(this.id_tratamento) };
                db.getWritableDatabase().update("tratamento", valores, "id_tratamento=?", args);
                msg="Registro atualizado";
            } else {
                this.id_tratamento = db.getWritableDatabase().insert("tratamento", null,
                        valores);
                msg="Registro inserido";
            }
            return true;
        } catch (SQLException e) {
            msg = e.getMessage();
            return false;
        } finally {
            db.close();
            toast.show(msg);
        }
    }

    public boolean delete() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        try {
            String[] args = { Long.toString(this.id_tratamento) };

            db.getWritableDatabase().delete("tratamento", "id_tratamento=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAlltratamentos() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_tratamento as id, sinan_quart as texto FROM tratamento v";

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
        String selectQuery = "SELECT t.id_tratamento, t.id_municipio, t.id_execucao, t.id_quarteirao, t.fant_quart, t.id_inseticida, t.id_espalhante, t.dt_cadastro, t.id_usuario, t.sinan_quart, t.status, "
                + "sum(intra_comodo_existente) as intra_comodo_existente, sum(intra_comodo_borrifado) as intra_comodo_borrifado, sum(peri_abrigo_existente) as peri_abrigo_existente, "
                + "sum(peri_abrigo_borrifado) as peri_abrigo_borrifado, sum(consumo_inseticida) as consumo_inseticida, sum(consumo_espalhante) as consumo_espalhante, sum(case when id_situacao = 1 then 1 else 0 end) as trabalhado, "
                + "sum(case when id_situacao = 2 then 1 else 0 end) as fechado, sum(case when id_situacao = 3 then 1 else 0 end) as recusa, sum(case when id_situacao = 4 then 1 else 0 end) as desabitado, "
                + "sum(nao_borrifado_acabamento) as nao_borrifado_acabamento, sum(nao_borrifado_recusa) as nao_borrifado_recusa, sum(nao_borrifado_outros) as nao_borrifado_outros, sum(peri_muro) as peri_muro, sum(peri_outros) as peri_outros "
                + "FROM tratamento t join tratamento_det dt using(id_tratamento) WHERE t.status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> valores = new HashMap<>();
                for (int i=0;i<cursor.getCount();i++){
                    for (int j=0;j<cursor.getColumnCount();j++) {
                        if(cursor.getColumnName(j).equals("id_execucao")){
                            valores.put("exec", cursor.getString(j).trim());
                        } else {
                            valores.put(cursor.getColumnName(j), cursor.getString(j).trim());
                        }
                    }
                }
              //  String ex = String.valueOf(Storage.recuperaI("exec"));
              //  valores.put("exec",ex);
                /* valores.put("id_municipio", cursor.getString(0));
                valores.put("id_execucao", cursor.getString(1));

                valores.put("id_quarteirao", cursor.getString(2));
                valores.put("fant_quart", cursor.getString(3));
                valores.put("id_inseticida",cursor.getString(4));
                valores.put("id_espalhante", cursor.getString(5));
                valores.put("dt_cadastro", cursor.getString(6));
                valores.put("id_usuario", cursor.getString(7));
                valores.put("sinan_quart", cursor.getString(8));
                valores.put("status", cursor.getString(9));

                valores.put("consumo_casa", cursor.getString(10));
                valores.put("consumo_peri", cursor.getString(11));
                valores.put("dt_atendimento", cursor.getString(12));
                valores.put("id_inseto_suspeito", cursor.getString(13));
                valores.put("nao_tratado", cursor.getString(14));
                valores.put("latitude", cursor.getString(15));
                valores.put("longitude", cursor.getString(16));
                valores.put("status", cursor.getString(17));*/
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
        String selectQuery = "SELECT  * FROM tratamento where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM tratamento";
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
        String updateQuery = "Update tratamento set status = '"+ status +"' where id_tratamento="+"'"+ id +"'";
       // Log.d("query",updateQuery);
        db.getWritableDatabase().execSQL(updateQuery);

        db.close();
    }

    public int Limpar(String filt){
        gerenciaBanco db = new gerenciaBanco(this.context);
        int id = 0;
        int regs=0;
        String sql = "SELECT id_tratamento as id FROM tratamento "+filt;
        Cursor cursor = db.getWritableDatabase().rawQuery(sql, null);
        regs = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                sql = "DELETE FROM tratamento_det where id_tratamento="+"'"+ id +"'";
               // Log.d("query",sql);
                db.getWritableDatabase().execSQL(sql);
            } while (cursor.moveToNext());
        }



        sql = "DELETE FROM tratamento "+filt;
       // Log.d("query",sql);
        db.getWritableDatabase().execSQL(sql);
        db.close();
        return regs;
    }

    public long getId_tratamento() {
        return id_tratamento;
    }

    public void setId_tratamento(long id_tratamento) {
        this.id_tratamento = id_tratamento;
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

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_espalhante() {
        return id_espalhante;
    }

    public void setId_espalhante(int id_espalhante) {
        this.id_espalhante = id_espalhante;
    }

    public int getId_inseticida() {
        return id_inseticida;
    }

    public void setId_inseticida(int id_inseticida) {
        this.id_inseticida = id_inseticida;
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

    public String getSinan_quart() {
        return sinan_quart;
    }

    public void setSinan_quart(String sinan_quart) {
        this.sinan_quart = sinan_quart;
    }

    public String getFant_quart() {
        return fant_quart;
    }

    public void setFant_quart(String fant_quart) {
        this.fant_quart = fant_quart;
    }
}

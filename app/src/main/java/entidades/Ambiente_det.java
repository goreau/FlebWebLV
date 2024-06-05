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
import java.util.List;

import util.MyToast;
import util.RegistrosList;
import util.gerenciaBanco;

/**
 * Created by henrique on 27/06/2016.
 */
public class Ambiente_det {
    long id_ambiente_det, id_ambiente;
    int id_tipo_imovel, id_situacao_imovel, ordem, id_aux_pavimento, mat_organica, abrigo_animal, num_humano, num_cao, num_gato, num_ave, num_outro;
    int orientacao, poda, capina, recolha, protocolo, retorno;
    String latitude, longitude, numero_casa;
    MyToast toast;
    Context context;

    public Ambiente_det(long id_ambiente_det) {
        this.id_ambiente_det = id_ambiente_det;
        context = PrincipalActivity.getFlebContext();
        if (id_ambiente_det>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_ambiente, id_tipo_imovel, id_situacao_imovel, latitude, longitude, ordem, numero_casa, id_aux_pavimento INTEGER, mat_organica INTEGER, " +
                "abrigo_animal, num_humano, num_cao, num_gato, num_ave, num_outro, orientacao, poda, capina, recolha, protocolo, retorno " +
                "FROM ambiente_det t where id_ambiente_det=" + this.id_ambiente_det;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_ambiente 	    = cursor.getInt(0);
            this.id_tipo_imovel     = cursor.getInt(1);
            this.id_situacao_imovel = cursor.getInt(2);
            this.latitude           = cursor.getString(3);
            this.longitude          = cursor.getString(4);
            this.ordem     	        = cursor.getInt(5);
            this.numero_casa 	    = cursor.getString(6);
            this.id_aux_pavimento   = cursor.getInt(7);
            this.mat_organica 		= cursor.getInt(8);
            this.abrigo_animal 	    = cursor.getInt(9);
            this.num_humano 		= cursor.getInt(10);
            this.num_cao 		    = cursor.getInt(11);
            this.num_gato 		    = cursor.getInt(12);
            this.num_ave 		    = cursor.getInt(13);
            this.num_outro 		    = cursor.getInt(14);
            this.orientacao 		= cursor.getInt(15);
            this.poda 		        = cursor.getInt(16);
            this.capina 		    = cursor.getInt(17);
            this.recolha 		    = cursor.getInt(18);
            this.protocolo 		    = cursor.getInt(19);
            this.retorno            = cursor.getInt(20);
        }
        db.close();
        cursor.close();
    }

    public boolean manipula() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        toast = new MyToast(this.context, Toast.LENGTH_SHORT);
        String msg = "";
        int result  = 0;
        try {
            ContentValues valores = new ContentValues();
            valores.put("id_ambiente", this.id_ambiente);
            valores.put("id_tipo_imovel", this.id_tipo_imovel);
            valores.put("id_situacao_imovel", this.id_situacao_imovel);

            valores.put("ordem", this.ordem);
            valores.put("numero_casa", this.numero_casa);
            valores.put("id_aux_pavimento", this.id_aux_pavimento);
            valores.put("mat_organica", this.mat_organica);
            valores.put("abrigo_animal", this.abrigo_animal);
            valores.put("num_humano", this.num_humano);
            valores.put("num_cao", this.num_cao);
            valores.put("num_gato", this.num_gato);
            valores.put("num_ave", this.num_ave);
            valores.put("num_outro", this.num_outro);
            valores.put("orientacao", this.orientacao);
            valores.put("poda", this.poda);
            valores.put("capina", this.capina);
            valores.put("recolha", this.recolha);
            valores.put("protocolo", this.protocolo);
            valores.put("retorno", this.retorno);

            if (this.id_ambiente_det > 0) {
                String[] args = { Long.toString(this.id_ambiente_det) };
                result = db.getWritableDatabase().update("ambiente_det", valores, "id_ambiente_det=?", args);
                msg="Registro atualizado";
            } else {
                valores.put("latitude", this.latitude);
                valores.put("longitude", this.longitude);

                this.id_ambiente_det = db.getWritableDatabase().insert("ambiente_det", null, valores);
                result = (int) this.id_ambiente_det;
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
            String[] args = { Long.toString(this.id_ambiente_det) };

            db.getWritableDatabase().delete("ambiente_det", "id_ambiente_det=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllambiente_dets() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_ambiente_det as id, numero_casa as texto FROM ambiente_det v";

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
        cursor.close();
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     *
     *
     */
    public String composeJSONfromSQLite(String id) {
        Context context = PrincipalActivity.flebContext;
        gerenciaBanco db = new gerenciaBanco(context);
        //String map = "[";
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT id_ambiente_det, id_tipo_imovel, id_situacao_imovel, latitude, longitude, ordem, numero_casa, id_aux_pavimento, mat_organica, " +
                " abrigo_animal, num_humano, num_cao, num_gato, num_ave, num_outro, orientacao, poda, " +
                " capina, recolha, protocolo, retorno FROM ambiente_det where id_ambiente=" + id;
      //  Log.w("detalhe",selectQuery);
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id_ambiente_det", cursor.getString(0));
                map.put("id_tipo_imovel", cursor.getString(1));
                map.put("id_situacao_imovel", cursor.getString(2));
                map.put("latitude", cursor.getString(3));
                map.put("longitude", cursor.getString(4));
                map.put("ordem", cursor.getString(5));
                map.put("numero_casa", cursor.getString(6));
                map.put("id_aux_pavimento", cursor.getString(7));
                map.put("mat_organica", cursor.getString(8));
                map.put("abrigo_animal", cursor.getString(9));
                map.put("num_humano", cursor.getString(10));
                map.put("num_cao", cursor.getString(11));
                map.put("num_gato", cursor.getString(12));
                map.put("num_ave", cursor.getString(13));
                map.put("num_outro", cursor.getString(14));
                map.put("orientacao", cursor.getString(15));
                map.put("poda", cursor.getString(16));
                map.put("capina", cursor.getString(17));
                map.put("recolha", cursor.getString(18));
                map.put("protocolo", cursor.getString(19));
                map.put("retorno", cursor.getString(20));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        //map+="]";
        db.close();
        Gson gson = new GsonBuilder().create();
        //System.out.println(wordList);
        // Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
        //return map;
    }


    public int dbSyncCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM ambiente_det where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM ambiente_det";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public List<RegistrosList> getEdicao(Long id){

        Context context = PrincipalActivity.getFlebContext();
        gerenciaBanco db = new gerenciaBanco(context);

        String selectQuery = "SELECT id_ambiente_det, numero_casa, (case id_situacao_imovel when 1 then 'T' when 2 then 'F' when 3 then 'R' else 'D' end) as sit " +
                "FROM ambiente_det where id_ambiente=" + id;

        Cursor cursor = db.getReadableDatabase().rawQuery(selectQuery, null);
        List<RegistrosList> lista = new ArrayList<RegistrosList>();

        if (cursor.moveToFirst()) {
            do {
                RegistrosList list = new RegistrosList(cursor.getString(1),cursor.getString(2),cursor.getInt(0));
                lista.add(list);
              //  Log.d("dentro ",cursor.getString(1));
            } while (cursor.moveToNext());
        } else {
           // Log.d("sql errada",selectQuery);
        }
        return lista;
    }

    public long getId_ambiente_det() {
        return id_ambiente_det;
    }

    public void setId_ambiente_det(long id_ambiente_det) {
        this.id_ambiente_det = id_ambiente_det;
    }

    public long getId_ambiente() {
        return id_ambiente;
    }

    public void setId_ambiente(long id_ambiente) {
        this.id_ambiente = id_ambiente;
    }

    public int getId_tipo_imovel() {
        return id_tipo_imovel;
    }

    public void setId_tipo_imovel(int id_tipo_imovel) {
        this.id_tipo_imovel = id_tipo_imovel;
    }

    public int getId_situacao_imovel() {
        return id_situacao_imovel;
    }

    public void setId_situacao_imovel(int id_situacao_imovel) {
        this.id_situacao_imovel = id_situacao_imovel;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getId_aux_pavimento() {
        return id_aux_pavimento;
    }

    public void setId_aux_pavimento(int id_aux_pavimento) {
        this.id_aux_pavimento = id_aux_pavimento;
    }

    public int getMat_organica() {
        return mat_organica;
    }

    public void setMat_organica(int mat_organica) {
        this.mat_organica = mat_organica;
    }

    public int getAbrigo_animal() {
        return abrigo_animal;
    }

    public void setAbrigo_animal(int abrigo_animal) {
        this.abrigo_animal = abrigo_animal;
    }

    public int getNum_humano() {
        return num_humano;
    }

    public void setNum_humano(int num_humano) {
        this.num_humano = num_humano;
    }

    public int getNum_cao() {
        return num_cao;
    }

    public void setNum_cao(int num_cao) {
        this.num_cao = num_cao;
    }

    public int getNum_gato() {
        return num_gato;
    }

    public void setNum_gato(int num_gato) {
        this.num_gato = num_gato;
    }

    public int getNum_ave() {
        return num_ave;
    }

    public void setNum_ave(int num_ave) {
        this.num_ave = num_ave;
    }

    public int getNum_outro() {
        return num_outro;
    }

    public void setNum_outro(int num_outro) {
        this.num_outro = num_outro;
    }

    public int getOrientacao() {
        return orientacao;
    }

    public void setOrientacao(int orientacao) {
        this.orientacao = orientacao;
    }

    public int getPoda() {
        return poda;
    }

    public void setPoda(int poda) {
        this.poda = poda;
    }

    public int getCapina() {
        return capina;
    }

    public void setCapina(int capina) {
        this.capina = capina;
    }

    public int getRecolha() {
        return recolha;
    }

    public void setRecolha(int recolha) {
        this.recolha = recolha;
    }

    public int getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(int protocolo) {
        this.protocolo = protocolo;
    }

    public int getRetorno() {
        return retorno;
    }

    public void setRetorno(int retorno) {
        this.retorno = retorno;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNumero_casa() {
        return numero_casa;
    }

    public void setNumero_casa(String numero_casa) {
        this.numero_casa = numero_casa;
    }
}

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
 * Created by Acer on 26/06/2016.
 */
public class Captura_det {
    long id_captura_det, id_captura;
    int id_metodo, id_local_captura, id_quarteirao, id_habito_alimentar_peri, id_aux_local_arm, id_habito_alimentar_intra, distancia, fleb, ordem;
    String hora_ini, hora_fim, numero_imovel, latitude, longitude, amostra, fant_quart;
    MyToast toast;
    Context context;

    public Captura_det(long id_captura_det) {
        this.id_captura_det = id_captura_det;
        context = PrincipalActivity.getFlebContext();
        if (id_captura_det>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_captura, hora_ini, hora_fim,  id_metodo,  id_local_captura, id_quarteirao, numero_imovel, " +
                "id_habito_alimentar_peri, id_aux_local_arm, id_habito_alimentar_intra, distancia, latitude, fleb, fant_quart, longitude, amostra, ordem " +
                "FROM captura_det t where id_captura_det=" + this.id_captura_det;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_captura 	            = cursor.getInt(0);
            this.hora_ini                   = cursor.getString(1);
            this.hora_fim 	                = cursor.getString(2);
            this.id_metodo                  = cursor.getInt(3);
            this.id_local_captura           = cursor.getInt(4);
            this.id_quarteirao     	        = cursor.getInt(5);
            this.numero_imovel 	            = cursor.getString(6);
            this.id_habito_alimentar_peri   = cursor.getInt(7);
            this.id_aux_local_arm 		    = cursor.getInt(8);
            this.id_habito_alimentar_intra 	= cursor.getInt(9);
            this.distancia 		            = cursor.getInt(10);
            this.latitude 		            = cursor.getString(11);
            this.fleb 		                = cursor.getInt(12);
            this.fant_quart                 = cursor.getString(13);
            this.longitude 		            = cursor.getString(14);
            this.amostra 		            = cursor.getString(15);
            this.ordem                      = cursor.getInt(16);
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
            valores.put("id_captura", this.id_captura);
            valores.put("hora_ini", this.hora_ini);
            valores.put("hora_fim", this.hora_fim);
            valores.put("id_metodo", this.id_metodo);
            valores.put("id_local_captura", this.id_local_captura);
            valores.put("id_quarteirao", this.id_quarteirao);
            valores.put("numero_imovel", this.numero_imovel);
            valores.put("id_habito_alimentar_peri", this.id_habito_alimentar_peri);
            valores.put("id_aux_local_arm", this.id_aux_local_arm);
            valores.put("id_habito_alimentar_intra", this.id_habito_alimentar_intra);
            valores.put("distancia", this.distancia);

            valores.put("amostra", this.amostra);
            valores.put("fleb", this.fleb);
            valores.put("fant_quart",this.fant_quart);
            valores.put("ordem",this.ordem);

            if (this.id_captura_det > 0) {
                String[] args = { Long.toString(this.id_captura_det) };
                result = db.getWritableDatabase().update("captura_det", valores, "id_captura_det=?", args);
                msg="Registro atualizado";
            } else {
                valores.put("latitude", this.latitude);
                valores.put("longitude", this.longitude);
                this.id_captura_det = db.getWritableDatabase().insert("captura_det", null, valores);
                result = (int) this.id_captura_det;
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
            String[] args = { Long.toString(this.id_captura_det) };

            db.getWritableDatabase().delete("captura_det", "id_captura_det=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAllcaptura_dets() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_captura_det as id, numero_imovel as texto FROM captura_det v";

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
     * @return
     */
    public String composeJSONfromSQLite(String id) {
        Context context = PrincipalActivity.flebContext;
        gerenciaBanco db = new gerenciaBanco(context);
        //String map = "[";
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT id_captura, hora_ini, hora_fim,  id_metodo,  id_local_captura, id_quarteirao, numero_imovel, " +
                "id_habito_alimentar_peri, id_aux_local_arm, id_habito_alimentar_intra, distancia, latitude, fleb, fant_quart, " +
                "longitude, amostra, ordem, id_captura_det  FROM captura_det where id_captura=" + id;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id_captura", cursor.getString(0));
                map.put("hora_ini", cursor.getString(1));
                map.put("hora_fim", cursor.getString(2));
                map.put("id_metodo", cursor.getString(3));
                map.put("id_local_captura", cursor.getString(4));
                map.put("id_quarteirao", cursor.getString(5));
                map.put("numero_imovel", cursor.getString(6));
                map.put("id_habito_alimentar_peri", cursor.getString(7));
                map.put("id_aux_local_arm", cursor.getString(8));
                map.put("id_habito_alimentar_intra", cursor.getString(9));
                map.put("distancia", cursor.getString(10));
                map.put("latitude", cursor.getString(11));
                map.put("fleb", cursor.getString(12));
                map.put("fant_quart", cursor.getString(13).trim());
                map.put("longitude", cursor.getString(14));
                map.put("amostra", cursor.getString(15));
                map.put("endereco", cursor.getString(16));//no lugar do endereÃ§o vai a ordem
                map.put("id_captura_det", cursor.getString(17));
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
        String selectQuery = "SELECT  * FROM captura_det where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM captura_det";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public List<RegistrosList> getEdicao(Long id){

        Context context = PrincipalActivity.getFlebContext();
        gerenciaBanco db = new gerenciaBanco(context);

        String selectQuery = "SELECT id_captura_det, fant_quart, numero_imovel FROM captura_det where id_captura=" + id;

        Cursor cursor = db.getReadableDatabase().rawQuery(selectQuery, null);
        List<RegistrosList> lista = new ArrayList<RegistrosList>();

        if (cursor.moveToFirst()) {
            do {
                RegistrosList list = new RegistrosList(cursor.getString(1),cursor.getString(2),cursor.getInt(0));
                lista.add(list);
                //Log.d("dentro ",cursor.getString(1));
            } while (cursor.moveToNext());
        } else {
           // Log.d("sql errada",selectQuery);
        }
        return lista;
    }

    public long getId_captura_det() {
        return id_captura_det;
    }

    public void setId_captura_det(long id_captura_det) {
        this.id_captura_det = id_captura_det;
    }

    public long getId_captura() {
        return id_captura;
    }

    public void setId_captura(long id_captura) {
        this.id_captura = id_captura;
    }

    public int getId_metodo() {
        return id_metodo;
    }

    public void setId_metodo(int id_metodo) {
        this.id_metodo = id_metodo;
    }

    public int getId_local_captura() {
        return id_local_captura;
    }

    public void setId_local_captura(int id_local_captura) {
        this.id_local_captura = id_local_captura;
    }

    public int getId_quarteirao() {
        return id_quarteirao;
    }

    public void setId_quarteirao(int id_quarteirao) {
        this.id_quarteirao = id_quarteirao;
    }

    public int getId_habito_alimentar_peri() {
        return id_habito_alimentar_peri;
    }

    public void setId_habito_alimentar_peri(int id_habito_alimentar_peri) {
        this.id_habito_alimentar_peri = id_habito_alimentar_peri;
    }

    public int getId_aux_local_arm() {
        return id_aux_local_arm;
    }

    public void setId_aux_local_arm(int id_aux_local_arm) {
        this.id_aux_local_arm = id_aux_local_arm;
    }

    public int getId_habito_alimentar_intra() {
        return id_habito_alimentar_intra;
    }

    public void setId_habito_alimentar_intra(int id_habito_alimentar_intra) {
        this.id_habito_alimentar_intra = id_habito_alimentar_intra;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public int getFleb() {
        return fleb;
    }

    public void setFleb(int fleb) {
        this.fleb = fleb;
    }

    public String getHora_ini() {
        return hora_ini;
    }

    public void setHora_ini(String hora_ini) {
        this.hora_ini = hora_ini;
    }

    public String getHora_fim() {
        return hora_fim;
    }

    public void setHora_fim(String hora_fim) {
        this.hora_fim = hora_fim;
    }

    public String getNumero_imovel() {
        return numero_imovel;
    }

    public void setNumero_imovel(String numero_imovel) {
        this.numero_imovel = numero_imovel;
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

    public String getAmostra() {
        return amostra;
    }

    public void setAmostra(String amostra) {
        this.amostra = amostra;
    }

    public String getFant_quart() {
        return fant_quart;
    }

    public void setFant_quart(String fant_quart) {
        this.fant_quart = fant_quart;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}

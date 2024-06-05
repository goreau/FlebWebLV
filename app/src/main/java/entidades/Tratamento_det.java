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
 * Created by Acer on 24/06/2016.
 *
 *
 *
 */
public class Tratamento_det {
    long id_tratamento_det, id_tratamento;
    int ordem, intra_comodo_existente, intra_comodo_borrifado, peri_abrigo_existente, peri_abrigo_borrifado, id_situacao;
    int nao_borrifado_acabamento, nao_borrifado_recusa, nao_borrifado_outros, peri_muro, peri_outros;
    float consumo_inseticida,  consumo_espalhante;
    MyToast toast;
    Context context;

    public Tratamento_det(long id_tratamento_det) {
        this.id_tratamento_det = id_tratamento_det;
        context = PrincipalActivity.getFlebContext();
        if (id_tratamento_det>0){
            popula();
        }
    }

    public void popula(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        String selectQuery = "SELECT id_tratamento, intra_comodo_existente, intra_comodo_borrifado, peri_abrigo_existente, peri_abrigo_borrifado, "
                + "id_situacao, nao_borrifado_acabamento, nao_borrifado_recusa, nao_borrifado_outros, peri_muro, peri_outros, "
                + "consumo_inseticida,  consumo_espalhante, ordem FROM tratamento_det t where id_tratamento_det=" + this.id_tratamento_det;

        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            this.id_tratamento 	            = cursor.getInt(0);
            this.intra_comodo_existente     = cursor.getInt(1);
            this.intra_comodo_borrifado 	= cursor.getInt(2);
            this.peri_abrigo_existente      = cursor.getInt(3);
            this.peri_abrigo_borrifado      = cursor.getInt(4);
            this.id_situacao     	        = cursor.getInt(5);
            this.nao_borrifado_acabamento 	= cursor.getInt(6);
            this.nao_borrifado_recusa 	    = cursor.getInt(7);
            this.nao_borrifado_outros 		= cursor.getInt(8);
            this.peri_muro 		            = cursor.getInt(9);
            this.peri_outros 		        = cursor.getInt(10);
            this.consumo_inseticida 		= cursor.getFloat(11);
            this.consumo_espalhante 		= cursor.getFloat(12);
            this.ordem                      = cursor.getInt(13);
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
            valores.put("id_tratamento", this.id_tratamento);
            valores.put("intra_comodo_existente", this.intra_comodo_existente);
            valores.put("intra_comodo_borrifado", this.intra_comodo_borrifado);
            valores.put("peri_abrigo_existente", this.peri_abrigo_existente);
            valores.put("peri_abrigo_borrifado", this.peri_abrigo_borrifado);
            valores.put("id_situacao", this.id_situacao);
            valores.put("nao_borrifado_acabamento", this.nao_borrifado_acabamento);
            valores.put("nao_borrifado_recusa", this.nao_borrifado_recusa);
            valores.put("nao_borrifado_outros", this.nao_borrifado_outros);
            valores.put("peri_muro", this.peri_muro);
            valores.put("peri_outros", this.peri_outros);
            valores.put("consumo_inseticida", this.consumo_inseticida);
            valores.put("consumo_espalhante", this.consumo_espalhante);
            valores.put("ordem",this.ordem);

            if (this.id_tratamento_det > 0) {
                String[] args = { Long.toString(this.id_tratamento_det) };
                db.getWritableDatabase().update("tratamento_det", valores, "id_tratamento_det=?", args);
                msg="Registro atualizado";
            } else {
                this.id_tratamento_det = db.getWritableDatabase().insert("tratamento_det", null,
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
            String[] args = { Long.toString(this.id_tratamento_det) };

            db.getWritableDatabase().delete("tratamento_det", "id_tratamento_det=?", args);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<HashMap<String, String>> getAlltratamento_dets() {
        gerenciaBanco db = new gerenciaBanco(this.context);
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT v.id_tratamento_det as id, ordem as texto FROM tratamento_det v";

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


    public int dbSyncCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM tratamento_det where status = 0";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int dbCount(){
        gerenciaBanco db = new gerenciaBanco(context);
        int count = 0;
        String selectQuery = "SELECT  * FROM tratamento_det";
        Cursor cursor = db.getWritableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public List<RegistrosList> getEdicao(Long id){

        Context context = PrincipalActivity.getFlebContext();
        gerenciaBanco db = new gerenciaBanco(context);

        String selectQuery = "SELECT id_tratamento_det, ordem, (case id_situacao when 1 then 'T' when 3 then 'R' when 2 then 'F' else 'D' end) as sit" +
                " FROM tratamento_det where id_tratamento=" + id;

        Cursor cursor = db.getReadableDatabase().rawQuery(selectQuery, null);
        List<RegistrosList> lista = new ArrayList<RegistrosList>();

        if (cursor.moveToFirst()) {
            do {
                RegistrosList list = new RegistrosList(cursor.getString(1),cursor.getString(2),cursor.getInt(0));
                lista.add(list);
                //Log.d("dentro ",cursor.getString(1));
            } while (cursor.moveToNext());
        } else {
            //Log.d("sql errada",selectQuery);
        }
        return lista;
    }

   /* public ArrayList getEdicao(Long id){
        ArrayList myList = new ArrayList();
        Context context = PrincipalActivity.getFlebContext();
        gerenciaBanco db = new gerenciaBanco(context);

        String selectQuery = "SELECT id_tratamento_det, ordem, (case id_situacao when 1 then 'T' when 2 then 'R' when 3 then 'F' else 'D' end) as sit" +
                " FROM tratamento_det where id_tratamento=" + id;

        Cursor cursor = db.getReadableDatabase().rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RegistrosList list = new RegistrosList();
                list.setId(cursor.getInt(0));
                list.setOrdem(cursor.getString(1));
                list.setSit(cursor.getString(2));
                myList.add(list);
            } while (cursor.moveToNext());
        } else {
            Log.d("sql errada",selectQuery);
        }
        return myList;
    }*/



    public long getId_tratamento_det() {
        return id_tratamento_det;
    }

    public void setId_tratamento_det(long id_tratamento_det) {
        this.id_tratamento_det = id_tratamento_det;
    }

    public long getId_tratamento() {
        return id_tratamento;
    }

    public void setId_tratamento(long id_tratamento) {
        this.id_tratamento = id_tratamento;
    }

    public int getIntra_comodo_existente() {
        return intra_comodo_existente;
    }

    public void setIntra_comodo_existente(int intra_comodo_existente) {
        this.intra_comodo_existente = intra_comodo_existente;
    }

    public int getIntra_comodo_borrifado() {
        return intra_comodo_borrifado;
    }

    public void setIntra_comodo_borrifado(int intra_comodo_borrifado) {
        this.intra_comodo_borrifado = intra_comodo_borrifado;
    }

    public int getPeri_abrigo_existente() {
        return peri_abrigo_existente;
    }

    public void setPeri_abrigo_existente(int peri_abrigo_existente) {
        this.peri_abrigo_existente = peri_abrigo_existente;
    }

    public int getPeri_abrigo_borrifado() {
        return peri_abrigo_borrifado;
    }

    public void setPeri_abrigo_borrifado(int peri_abrigo_borrifado) {
        this.peri_abrigo_borrifado = peri_abrigo_borrifado;
    }

    public int getId_situacao() {
        return id_situacao;
    }

    public void setId_situacao(int id_situacao) {
        this.id_situacao = id_situacao;
    }

    public int getNao_borrifado_acabamento() {
        return nao_borrifado_acabamento;
    }

    public void setNao_borrifado_acabamento(int nao_borrifado_acabamento) {
        this.nao_borrifado_acabamento = nao_borrifado_acabamento;
    }

    public int getNao_borrifado_recusa() {
        return nao_borrifado_recusa;
    }

    public void setNao_borrifado_recusa(int nao_borrifado_recusa) {
        this.nao_borrifado_recusa = nao_borrifado_recusa;
    }

    public int getNao_borrifado_outros() {
        return nao_borrifado_outros;
    }

    public void setNao_borrifado_outros(int nao_borrifado_outros) {
        this.nao_borrifado_outros = nao_borrifado_outros;
    }

    public int getPeri_muro() {
        return peri_muro;
    }

    public void setPeri_muro(int peri_muro) {
        this.peri_muro = peri_muro;
    }

    public int getPeri_outros() {
        return peri_outros;
    }

    public void setPeri_outros(int peri_outros) {
        this.peri_outros = peri_outros;
    }

    public float getConsumo_inseticida() {
        return consumo_inseticida;
    }

    public void setConsumo_inseticida(float consumo_inseticida) {
        this.consumo_inseticida = consumo_inseticida;
    }

    public float getConsumo_espalhante() {
        return consumo_espalhante;
    }

    public void setConsumo_espalhante(float consumo_espalhante) {
        this.consumo_espalhante = consumo_espalhante;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}

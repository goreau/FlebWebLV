package entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

import com.sucen.flebweblv.PrincipalActivity;

import java.util.ArrayList;
import java.util.List;

import util.MyToast;
import util.gerenciaBanco;

/**
 * Created by Acer on 26/06/2016.
 */
public class Auxiliares {
    private int id_auxiliares, tipo, sk_auxiliares;
    private String descricao;
    private Context context;
    public List<String> idAuxiliares;
    MyToast toast;

    public Auxiliares() {
        context = PrincipalActivity.getFlebContext();;
        //  toast = new MyToast(this.context, Toast.LENGTH_SHORT);
    }

    public int getId_auxiliares() {
        return id_auxiliares;
    }

    public void setId_auxiliares(int id_auxiliares) {
        this.id_auxiliares = id_auxiliares;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getSk_auxiliares() {
        return sk_auxiliares;
    }

    public void setSk_auxiliares(int sk_auxiliares) {
        this.sk_auxiliares = sk_auxiliares;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void limpar(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        try{
            db.getWritableDatabase().delete("auxiliares", null, null);
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
        }
    }

    public boolean insere(String[] campos, String[] val){
        gerenciaBanco db = new gerenciaBanco(this.context);

        try{
            ContentValues valores = new ContentValues();
            for (int i = 0; i<val.length;i++){
                valores.put(campos[i],val[i]);
            }
            db.getWritableDatabase().insert("auxiliares", null, valores);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    public List<String> combo(int tipo){
        List<String> mun = new ArrayList<String>();
        idAuxiliares = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT id_auxiliares, sk_auxiliares || ' - ' || descricao as texto FROM auxiliares where tipo="+tipo;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(1));
                idAuxiliares.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }
}

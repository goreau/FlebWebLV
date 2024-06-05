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
 * Created by Acer on 24/06/2016.
 */
public class Municipio {
    private int id_municipio;
    private String nome;
    private String codigo;
    private Context context;
    public List<String> idMun;
    MyToast toast;

    public Municipio() {
        context = PrincipalActivity.getFlebContext();;
        //  toast = new MyToast(this.context, Toast.LENGTH_SHORT);
    }

    public int getidMunicipio() {
        return id_municipio;
    }
    public void setidMunicipio(int idMunicipio) {
        this.id_municipio = idMunicipio;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void limpar(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        try{
            db.getWritableDatabase().delete("municipio", null, null);
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
            db.getWritableDatabase().insert("municipio", null, valores);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    public List<String> combo(){
        List<String> mun = new ArrayList<String>();
        idMun = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT id_municipio, nome FROM municipio";
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(1));
                idMun.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }
}

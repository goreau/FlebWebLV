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
public class Area {
    private int id_area, id_municipio;
    private String codigo;
    private Context context;
    public List<String> idArea;
    MyToast toast;

    public Area() {
        context = PrincipalActivity.getFlebContext();;
        //  toast = new MyToast(this.context, Toast.LENGTH_SHORT);
    }

    public int getId_area() {
        return id_area;
    }
    public void setId_area(int id_area) {
        this.id_area = id_area;
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
            db.getWritableDatabase().delete("area", null, null);
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
            db.getWritableDatabase().insert("area", null, valores);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    public List<String> combo(int id_mun){
        List<String> mun = new ArrayList<String>();
        idArea = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT id_area, codigo FROM area where id_municipio="+id_mun;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(1));
                idArea.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }
}

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
public class Quarteirao {
    private int id_quarteirao, id_area;
    private String numero_quarteirao, identificador;
    private Context context;
    public List<String> idQuarteirao;
    MyToast toast;

    public Quarteirao() {
        context = PrincipalActivity.getFlebContext();;
        //  toast = new MyToast(this.context, Toast.LENGTH_SHORT);
    }

    public int getId_quarteirao() {
        return id_quarteirao;
    }
    public void setId_quarteirao(int id_quarteirao) {
        this.id_quarteirao = id_quarteirao;
    }

    public void limpar(){
        gerenciaBanco db = new gerenciaBanco(this.context);
        try{
            db.getWritableDatabase().delete("quarteirao", null, null);
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
            db.getWritableDatabase().insert("quarteirao", null, valores);
            return true;
        } catch (SQLException e) {
            toast = new MyToast(this.context, Toast.LENGTH_SHORT);
            toast.show(e.getMessage());
            return false;
        } finally {
            db.close();
        }

    }

    public List<String> comboIdent(int area){
        List<String> mun = new ArrayList<String>();

        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT distinct identificador FROM quarteirao where id_area="+area;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }

    public List<String> combo(int area){
        List<String> mun = new ArrayList<String>();
        idQuarteirao = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT id_quarteirao, numero_quarteirao FROM quarteirao where id_area="+area+" order by numero_quarteirao*1, numero_quarteirao";
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(1));
                idQuarteirao.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }

    public List<String> comboByIdent(String ident, int area){
        List<String> mun = new ArrayList<String>();
        idQuarteirao = new ArrayList<String>();
        gerenciaBanco db = new gerenciaBanco(this.context);
        String sql = "SELECT id_quarteirao, numero_quarteirao FROM quarteirao where identificador='"+ident+"' and id_area="+area+" order by numero_quarteirao*1, numero_quarteirao";;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                mun.add(cursor.getString(1));
                idQuarteirao.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mun;
    }

    public static int retArea(int id_quarteirao, Context cont){
        int area = 0;
        gerenciaBanco db = new gerenciaBanco(cont);
        String sql = "SELECT id_area FROM quarteirao where id_quarteirao="+id_quarteirao;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            area = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return area;
    }

    public static String retIdent(int id_quarteirao, Context cont){
        String ident = "";
        gerenciaBanco db = new gerenciaBanco(cont);
        String sql = "SELECT identificador FROM quarteirao where id_quarteirao="+id_quarteirao;
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            ident = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return ident;
    }
}

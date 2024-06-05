package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


public class gerenciaBanco extends SQLiteOpenHelper {
    HashMap<String,ContentValues[]> registros = new HashMap<String,ContentValues[]>();
    private static final String NOME_BANCO = "flebweb.db";
    private static final int VERSAO_SCHEMA = 13;
    private static final String[] sql = {
            "CREATE TABLE municipio(id_municipio INTEGER, nome TEXT, codigo TEXT)",
            "CREATE TABLE area(id_area INTEGER, id_municipio INTEGER, codigo TEXT)",
            "CREATE TABLE quarteirao(id_quarteirao INTEGER, id_area INTEGER, identificador TEXT, numero_quarteirao TEXT)",
            "CREATE TABLE produto(id_produto INTEGER, codigo TEXT, nome TEXT, tipo INTEGER)",
            "CREATE TABLE auxiliares(id_auxiliares INTEGER, tipo INTEGER, descricao TEXT, sk_auxiliares INTEGER)",
            "CREATE TABLE tipo_imovel(id_tipo_imovel INTEGER, descricao TEXT)",
            "CREATE TABLE captura(id_captura INTEGER PRIMARY KEY AUTOINCREMENT, id_atividade INTEGER, id_municipio INTEGER, dt_cadastro TEXT, temp_ini REAL, temp_fim REAL, umid_ini INTEGER, umid_fim INTEGER, "
                    + "id_execucao INTEGER, vento integer, chuva INTEGER, id_area INTEGER, fant_area TEXT, id_usuario INTEGER, status INTEGER)",
            "CREATE TABLE captura_det(id_captura_det INTEGER PRIMARY KEY AUTOINCREMENT, id_captura INTEGER, hora_ini TEXT, hora_fim TEXT,  id_metodo INTEGER,  id_local_captura INTEGER, id_quarteirao INTEGER, numero_imovel TEXT, "
                    + "id_habito_alimentar_peri TEXT, id_aux_local_arm INTEGER, id_habito_alimentar_intra TEXT, distancia INTEGER, latitude TEXT, longitude TEXT, amostra TEXT, fleb INTEGER, fant_quart text, ordem INTEGER)",
            "CREATE TABLE ambiente(id_ambiente INTEGER PRIMARY KEY AUTOINCREMENT, id_municipio INTEGER, dt_cadastro TEXT, id_execucao INTEGER, id_quarteirao INTEGER, fant_quart TEXT, id_aux_atividade INTEGER, id_aux_tipo_manejo INTEGER, id_usuario INTEGER, status INTEGER)",
            "CREATE TABLE ambiente_det(id_ambiente_det INTEGER PRIMARY KEY AUTOINCREMENT, id_tipo_imovel INTEGER, id_situacao_imovel INTEGER, latitude TEXT, longitude TEXT, ordem INTEGER, numero_casa TEXT, id_aux_pavimento INTEGER, mat_organica INTEGER,"
                    +  "  abrigo_animal INTEGER, num_humano INTEGER, num_cao INTEGER, num_gato INTEGER, num_ave INTEGER, num_outro INTEGER, orientacao INTEGER, poda INTEGER,"
                    +  "  capina INTEGER, recolha INTEGER, protocolo INTEGER, retorno INTEGER, id_ambiente INTEGER)",
            "CREATE TABLE tratamento(id_tratamento INTEGER PRIMARY KEY AUTOINCREMENT, id_municipio INTEGER, id_execucao INTEGER, id_quarteirao INTEGER, id_inseticida INTEGER, id_espalhante INTEGER, "
                    +  "  dt_cadastro TEXT, id_usuario INTEGER, sinan_quart TEXT, fant_quart TEXT, status INTEGER)",
            "CREATE TABLE tratamento_det(id_tratamento_det INTEGER PRIMARY KEY AUTOINCREMENT, id_tratamento INTEGER, ordem INTEGER, intra_comodo_existente INTEGER, intra_comodo_borrifado INTEGER, "
                    +  "  peri_abrigo_existente INTEGER, peri_abrigo_borrifado INTEGER, consumo_inseticida REAL, consumo_espalhante REAL, id_situacao INTEGER, "
                    +  "  nao_borrifado_acabamento INTEGER, nao_borrifado_recusa INTEGER, nao_borrifado_outros INTEGER, peri_muro INTEGER, peri_outros INTEGER)",
            "CREATE TABLE det_inquerito(id_det_inquerito INTEGER PRIMARY KEY AUTOINCREMENT, tr_execucao INTEGER, tr_coleta INTEGER, tr_posit INTEGER, tr_negat INTEGER, elisa_nr INTEGER, elisa_posit INTEGER, "
                    +  "elisa_negat INTEGER, eutanasia INTEGER, id_municipio INTEGER, num_inquerito TEXT, id_tipo_inquerito INTEGER, id_area INTEGER, "
                    +  "id_quarteirao INTEGER, fant_area TEXT, fant_quart TEXT, mes_ano TEXT, id_usuario INTEGER, status INTEGER)"

    };
    private static final String[] tabelas = {"municipio","area","quarteirao","produto","auxiliares","tipo_imovel","captura", "captura_det","ambiente","ambiente_det","tratamento","tratamento_det","det_inquerito"};

    public gerenciaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO_SCHEMA);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < sql.length; i++){
            db.execSQL(sql[i]);
            //Log.w(gerenciaBanco.class.getName(), "Tabela " + tabelas[i] + " criada...");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        persiste(db);
        for (int i = 0; i < tabelas.length; i++){
            db.execSQL("DROP TABLE IF EXISTS " + tabelas[i]);
        }
        onCreate(db);
        recupera(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public void persiste(SQLiteDatabase db){
        //fornecer valor padrão para o campo alterado
        String[] sqlPersiste = {
                "SELECT id_municipio INTEGER, nome TEXT, codigo FROM municipio",
                "SELECT id_area, id_municipio, codigo FROM area",
                "SELECT id_quarteirao, id_area, identificador, numero_quarteirao FROM quarteirao",
                "SELECT id_produto, codigo, nome, tipo FROM produto",
                "SELECT id_auxiliares, tipo, descricao, sk_auxiliares FROM auxiliares",
                "SELECT id_tipo_imovel, descricao FROM tipo_imovel",
                "SELECT id_captura, id_atividade, id_municipio, dt_cadastro, temp_ini, temp_fim, umid_ini, umid_fim, id_execucao, vento, chuva, id_area, fant_area, id_usuario, status FROM captura",
                "SELECT id_captura_det, id_captura, hora_ini, hora_fim,  id_metodo, id_local_captura, id_quarteirao, numero_imovel, "
                        + "id_habito_alimentar_peri, id_aux_local_arm, id_habito_alimentar_intra, distancia, latitude, longitude, amostra, fleb, fant_quart, ordem FROM captura_det",
                "SELECT id_ambiente, id_municipio, dt_cadastro, id_execucao, id_quarteirao, fant_quart, id_aux_atividade, id_aux_tipo_manejo, id_usuario, status FROM ambiente",
                "SELECT id_ambiente_det, id_tipo_imovel, id_situacao_imovel, latitude, longitude, ordem, numero_casa, id_aux_pavimento, mat_organica,"
                        +  "  abrigo_animal, num_humano, num_cao, num_gato, num_ave, num_outro, orientacao, poda, capina, recolha, protocolo, retorno, id_ambiente FROM ambiente_det",
                "SELECT id_tratamento, id_municipio, id_execucao, id_quarteirao, id_inseticida, id_espalhante, dt_cadastro, id_usuario, sinan_quart, 0 as fant_quart, status FROM tratamento",
                "SELECT id_tratamento_det, id_tratamento, ordem, intra_comodo_existente, intra_comodo_borrifado, peri_abrigo_existente, peri_abrigo_borrifado, consumo_inseticida, consumo_espalhante, id_situacao, "
                        +  "  nao_borrifado_acabamento, nao_borrifado_recusa, nao_borrifado_outros, peri_muro, peri_outros FROM tratamento_det",
                "SELECT id_det_inquerito, tr_execucao, tr_coleta, tr_posit, tr_negat, elisa_nr, elisa_posit, elisa_negat, eutanasia, id_municipio, num_inquerito, id_tipo_inquerito, id_area, "
                        +  "id_quarteirao, fant_area, fant_quart, mes_ano, id_usuario, status FROM det_inquerito"};

        for (int i = 0; i < sqlPersiste.length; i++) {
            int x = 0;
            Cursor cursor = db.rawQuery(sqlPersiste[i], null);
            ContentValues[] total = new ContentValues[cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    ContentValues map = new ContentValues();
                    for (int j = 0; j < cursor.getColumnCount(); j++) {
                        map.put(cursor.getColumnName(j), cursor.getString(j));
                    }
                    total[x++] = map;
                } while (cursor.moveToNext());
                registros.put(tabelas[i], total);
            }
        }
    }



    public void recupera(SQLiteDatabase db){
        ContentValues[] dados;
        for (int x = 0; x < tabelas.length; x++) {
            dados = registros.get(tabelas[x]);
            try {
                for (int i = 0; i < dados.length; i++) {
                    ContentValues valores = dados[i];
                    db.insert(tabelas[x], null, valores);
                }
            } catch (SQLException e) {
                // Log.i("Exception",tabelas[x]);
                continue;
            } catch (NullPointerException nex){
                //  Log.i("Null",tabelas[x]);
                continue;
            }
        }
    }
}

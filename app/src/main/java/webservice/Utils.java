package webservice;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entidades.Ambiente;
import entidades.Area;
import entidades.Auxiliares;
import entidades.Captura;
import entidades.Det_inquerito;
import entidades.Municipio;
import entidades.Produto;
import entidades.Quarteirao;
import entidades.Tipo_imovel;
import entidades.Tratamento;


public class Utils {
    static Context contexto;

    public Utils(Context ctx) {
        contexto = ctx;
    }


    public String getInformacao(String end, String junta){
        String json;
        String retorno;
        json = NetworkUtils.getJSONFromAPI(end);
        //Log.i("Resultado", json);
        retorno = parseJson(json,junta);

        return retorno;
    }

    public String sendInformacao(String end, int tab){
        String json;
        String retorno;
        json = NetworkUtils.getJSONFromAPI(end);
       // Log.i("Resultado", json);

        retorno = parseRetorno(json,tab);

        return retorno;
    }

    private String parseJson(String json, String junta){
        int quant = 0;
        int linhas = 0;
        int inseridos = 0;
        String tabela = "";
        String resultado = "";
        try {
            if (json==""){
                resultado = "Erro recebendo os registros do servidor";
            } else {
                JSONObject dados = new JSONObject(json);
                //JSONObject dados = jsonObj.getJSONObject("dados");
                JSONArray tabelas = dados.names();
                quant = tabelas.length();
                //Log.w("tabelas",""+dados.names());
                for (int j = 0; j < quant; j++) {
                    tabela = tabelas.getString(j); //nome da tabela
                    if (tabela.equals("imovel") || tabela.equals("censitario") || tabela.equals("ovitrampa")) {
                        continue;
                    }
                    JSONArray objetos = dados.getJSONArray(tabela);//array da tabela do banco (municipio, area,..)
                    linhas = objetos.length(); //registros da tabela
                    //Log.w("Registros",""+linhas);
                    JSONArray names = objetos.getJSONObject(0).names(); //nomes dos campos
                    //Log.w("Campos",""+names);
                    int fields = names.length(); //quantidade de campos
                    String[] campos = new String[fields];
                    String[] valores = new String[fields];

                    for (int x = 0; x < linhas; x++) {
                        //Log.w("Linha:",""+x);
                        for (int i = 0; i < fields; i++) {
                            campos[i] = names.getString(i);
                            valores[i] = objetos.getJSONObject(x).getString(names.getString(i));
                        }
                        if (tabela.equals("municipio")) {
                            Municipio mun = new Municipio();
                            if (inseridos == 0 && junta == "0") mun.limpar();
                            if (mun.insere(campos, valores))
                                inseridos++;
                        } else if (tabela.equals("area")) {
                            Area area = new Area();
                            if (inseridos == 0 && junta == "0") area.limpar();
                            if (area.insere(campos, valores))
                                inseridos++;
                        } else if (tabela.equals("quarteirao")) {
                            Quarteirao prod = new Quarteirao();
                            if (inseridos == 0 && junta == "0") prod.limpar();
                            if (prod.insere(campos, valores))
                                inseridos++;
                        } else if (tabela.equals("produto")) {
                            Produto prod = new Produto();
                            if (inseridos == 0) prod.limpar();
                            if (prod.insere(campos, valores))
                                inseridos++;
                        } else if (tabela.equals("auxiliares")) {
                            Auxiliares prod = new Auxiliares();
                            if (inseridos == 0) prod.limpar();
                            if (prod.insere(campos, valores))
                                inseridos++;
                        } else if (tabela.equals("tipo_imovel")) {
                            Tipo_imovel prod = new Tipo_imovel();
                            if (inseridos == 0) prod.limpar();
                            if (prod.insere(campos, valores))
                                inseridos++;
                        }
                    }
                    resultado += "  -" + tabela + ": " + inseridos;
                    if (inseridos > 1)
                        resultado += " registros\n";
                    else
                        resultado += " registro\n";
                    inseridos = 0;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            inseridos = -1;
            resultado = e.getMessage();
        }
        return resultado;
    }

    //recebimento da resposta do envio (atualização do status)
    public String parseRetorno(String json, int tab){
        int quant = 0;
        int linhas = 0;
        int inseridos = 0;
        String tabela = "";
        String resultado = "";
        // Log.i("tabela Parse ret: " , "- "+tab);
        try {
            JSONArray registros = new JSONArray(json);
            quant = registros.length();

            for (int j=0;j<quant;j++) {
                JSONObject obj = registros.getJSONObject(j);//array da tabela do banco (municipio, area,..)
                switch (tab){
                    case 0:
                        Ambiente amb = new Ambiente(0);
                        tabela = "Manejo Ambiental ";
                        amb.atualizaStatus(obj.get("id").toString(), obj.get("status").toString());
                        break;
                    case 1:
                        Captura cap = new Captura(0);
                        tabela = "Captura de Flebotomíneos ";
                        cap.atualizaStatus(obj.get("id").toString(), obj.get("status").toString());
                        break;
                    case 2:
                        Tratamento trat = new Tratamento(0);
                        tabela = "Tratamento ";
                        trat.atualizaStatus(obj.get("id").toString(), obj.get("status").toString());
                        break;
                }

                inseridos += Integer.parseInt(obj.get("status").toString());
            }
            resultado += "  -" + tabela + ": " + inseridos;
            if (inseridos>1)
                resultado += " registros\n";
            else
                resultado += " registro\n";
        } catch (JSONException e) {
            e.printStackTrace();
            inseridos = -1;
            resultado = e.getMessage();
        }
        return resultado;
    }
}

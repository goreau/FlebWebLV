package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sucen.flebweblv.AmbienteDetFragment;
import com.sucen.flebweblv.PrincipalActivity;
import com.sucen.flebweblv.R;

import java.util.ArrayList;
import java.util.List;

import entidades.Ambiente_det;
import entidades.Captura_det;
import entidades.Tratamento_det;

public class EditaRegAdapter extends BaseAdapter {
    LayoutInflater mInflater;
	Context ctx = PrincipalActivity.getFlebContext();
	int rec = 0;
	String subtitulo;
	List<RegistrosList> lista;

	public EditaRegAdapter(Long id, int tabela) {
		super();
		this.mInflater = LayoutInflater.from(ctx);
		switch (tabela){
			case 1:
				Ambiente_det reg1 = new Ambiente_det(0);
				lista = reg1.getEdicao(id);
				break;
			case 2:
				Captura_det reg2 = new Captura_det(0);
				lista = reg2.getEdicao(id);
				break;
			case 3:

				break;
			default:
				Tratamento_det reg = new Tratamento_det(0);
				lista = reg.getEdicao(id);
				break;
		}
	}

	@Override
	public int getCount() {
		return lista.size();
	}

	@Override
	public Object getItem(int position) {
		return lista.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ed_registros,null);
        }
		
        final TextView txId = (TextView) convertView.findViewById(R.id.editId);
        txId.setText(String.valueOf(lista.get(position).getId()));
        final TextView txOrdem = (TextView) convertView.findViewById(R.id.editOrdem);
        txOrdem.setText(lista.get(position).getOrdem());
        final TextView txSit = (TextView) convertView.findViewById(R.id.editSit);
        txSit.setText(lista.get(position).getSit());
        return convertView;
	}

}

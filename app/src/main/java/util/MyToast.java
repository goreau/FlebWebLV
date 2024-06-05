package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.sucen.flebweblv.R;

public class MyToast extends Toast {
		private Context context;

		public MyToast(Context cont, int duration) {
			super(cont);
			context = cont;
			this.setDuration(duration);
		}

		public void show(CharSequence text) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = (View) li.inflate(R.layout.custom_toast, null);
			TextView tv = (TextView) v.findViewById(R.id.text_toast);
			this.setView(v);
			tv.setText(text);
			super.show();
		}
}

package util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.sucen.flebweblv.R;

/**
 * Created by henrique on 28/06/2016.
 */
public class CustomCheck extends LinearLayout{

      //  private final int ELEMENT_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    //    private final int ELEMENT_WIDTH = ELEMENT_HEIGHT; // you're all squares, yo

        private final int MINIMUM = 0;
        private final int MAXIMUM = 2;

        private final int TEXT_SIZE = 10;
        private final String[] valores = {"S","N","N/A"};
        private final int[] cores   = {0x99FF66,0xCC3300,0x0066FF};
        private final Drawable[] botoes = {getResources().getDrawable(R.drawable.bt_sim),getResources().getDrawable(R.drawable.bt_nao),getResources().getDrawable(R.drawable.bt_nana)};
        public Integer value = 2;

        Button check;

        /**
         * This little guy handles the auto part of the auto incrementing feature.
         * In doing so it instantiates itself. There has to be a pattern name for
         * that...
         *
         * @author Jeffrey F. Cole
         *
         */

        public CustomCheck(Context context, AttributeSet attributeSet ) {
            super(context, attributeSet);

           // this.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
         //   LayoutParams elementParams = new LinearLayout.LayoutParams( ELEMENT_HEIGHT, ELEMENT_WIDTH );
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.check_custom, this, true);


            // init the individual elements
            initCheckButton( context );

            // Can be configured to be vertical or horizontal
            // Thanks for the help, LinearLayout!
          //  if( getOrientation() == VERTICAL ){
           //     addView( check, elementParams );
          //  }
        }

        private void initCheckButton( Context context){
            check = (Button) getChildAt(0);
          //  check = new Button( context );
            check.setTextSize( TEXT_SIZE );
            check.setText( valores[2] );
          //  check.setBackgroundColor(cores[2]);
            check.setBackground(botoes[value]);
            // Increment once for a click
            check.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    increment();
                }
            });
        }


        public void increment(){
            if( value < MAXIMUM ){
                value = value + 1;
            } else {
                value = 0;
            }
            check.setText( valores[value] );
            check.setBackground(botoes[value]);
        }

        public void setValue( int value ){
            if( value > MAXIMUM ) value = MAXIMUM;
            if( value >= 0 ){
                this.value = value;
                check.setText( valores[value] );
                check.setBackground(botoes[value]);
            }
        }

        public int getValue(){
            return this.value;
        }
}

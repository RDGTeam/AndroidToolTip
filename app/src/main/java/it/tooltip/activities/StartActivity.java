package it.tooltip.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import it.tooltip.R;
import it.tooltip.RDG.RDGToolTip;
import it.tooltip.closeManager.ClosePolicy;
import it.tooltip.position.ToolTipPositionManager;

import static it.tooltip.R.id.b1;
import static it.tooltip.R.id.b2;


public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        LinearLayout centro = (LinearLayout) findViewById(R.id.centro);
        Log.d("centro", centro.getX() + " " + centro.getY());

        centro.setX(getResources().getDisplayMetrics().widthPixels / 2);
        centro.setY(getResources().getDisplayMetrics().heightPixels / 2);

        Log.d("centro", centro.getX() + " " + centro.getY());

        final Button dialog = (Button) findViewById(b1);
        final Button tool = (Button) findViewById(b2);


        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                View view = getLayoutInflater().inflate(R.layout.prova_dialog_layout, null);
                final Button button1 = (Button) view.findViewById(R.id.button1);
                final Button button2 = (Button) view.findViewById(R.id.button2);
                final Button button3 = (Button) view.findViewById(R.id.button3);
                final View button4 = view.findViewById(R.id.button4);

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RDGToolTip tt = new RDGToolTip(StartActivity.this);
                        tt.setTooltipPosition(button1, ToolTipPositionManager.TOP);
                        tt.setClosePolicy(ClosePolicy.CLOSE_ON_TAP);
                        tt.setMessage("prova di messaggio ");
                        tt.show();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RDGToolTip tt = new RDGToolTip(StartActivity.this);
                        tt.setTooltipPosition(button2, ToolTipPositionManager.RIGHT);
                        tt.setClosePolicy(ClosePolicy.CLOSE_OUTSIDE_TAP);
                        tt.setMessage("prova di messaggio ");
                        tt.show();
                    }
                });
                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RDGToolTip tt = new RDGToolTip(StartActivity.this);
                        tt.setTooltipPosition(button3, ToolTipPositionManager.BOTTOM);
                        tt.setMessage("prova di messaggio ");
                        tt.setClosePolicy(ClosePolicy.NO_CLOSE);
                        tt.show();
                    }
                });
                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RDGToolTip tt = new RDGToolTip(StartActivity.this);
                        tt.setTooltipPosition(button4, ToolTipPositionManager.LEFT);
                        tt.show();
                    }
                });

                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RDGToolTip tt = new RDGToolTip(StartActivity.this);
                tt.setTooltipPosition(tool, ToolTipPositionManager.NONE);
                tt.show();
            }
        });
    }
}

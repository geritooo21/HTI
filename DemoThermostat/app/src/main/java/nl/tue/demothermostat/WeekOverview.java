package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.thermostatapp.util.*;

/**
 * Created by s168239
 */

public class WeekOverview extends AppCompatActivity {

    private Button[] day = new Button[7];
    private ImageView reset;
    private LinearLayout activityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_overview);

        activityLayout = (LinearLayout) findViewById(R.id.week_overview);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        for (int i = 0; i < day.length; i++) {
            String ID = "day" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            day[i] = (Button) findViewById(resID);
        }

        for (int i = 0; i < day.length; i++) {
            final int j = i;
            day[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startDayOverview = new Intent(view.getContext(), DayOverview.class);
                    DayOverview.dayNumber = j;
                    startActivity(startDayOverview);
                }
            });
        }

        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder menuBuilder = new AlertDialog.Builder(WeekOverview.this);
                View menuView = getLayoutInflater().inflate(R.layout.reset, null);

                final Button cancel = (Button) menuView.findViewById(R.id.cancel);
                final Button reset = (Button) menuView.findViewById(R.id.reset);

                menuBuilder.setView(menuView);
                final AlertDialog dialog = menuBuilder.create();
                dialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(WeekOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    WeekProgram wpg = HeatingSystem.getWeekProgram();
                                    wpg.setDefault();
                                    HeatingSystem.setWeekProgram(wpg);

                                    activityLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(WeekOverview.this, R.string.reset, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    activityLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(WeekOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();

                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
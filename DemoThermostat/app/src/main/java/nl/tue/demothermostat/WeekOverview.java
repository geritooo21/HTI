package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.*;

import java.util.ArrayList;

/**
 * Created by s168239
 */

public class WeekOverview extends AppCompatActivity {

    private LinearLayout[] day = new LinearLayout[7];
    private ImageView reset;
    private LinearLayout activityLayout;

    private ImageView[] delete = new ImageView[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_overview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.homeicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ThermostatActivity.class));
            }
        });

        activityLayout = (LinearLayout) findViewById(R.id.week_overview);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        for (int i = 0; i < day.length; i++) {
            final int j = i;
            String ID = "day" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            day[i] = (LinearLayout) findViewById(resID);
            day[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startDayOverview = new Intent(view.getContext(), DayOverview.class);
                    DayOverview.dayNumber = j;
                    startActivity(startDayOverview);
                }
            });
        }

        for (int i = 0; i < delete.length; i++) {
            final int j = i;
            String ID = "delete" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            delete[i] = (ImageView) findViewById(resID);
            delete[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder menuBuilder = new AlertDialog.Builder(WeekOverview.this);
                    View menuView = getLayoutInflater().inflate(R.layout.reset, null);

                    final TextView text = (TextView) menuView.findViewById(R.id.text);
                    final Button cancel = (Button) menuView.findViewById(R.id.cancel);
                    final Button reset = (Button) menuView.findViewById(R.id.reset);

                    text.setText("Reset \n" + WeekProgram.valid_days[j] + " program?");

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

                                        wpg.data.put(wpg.valid_days[j], new ArrayList<Switch>());
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("night", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("night", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("night", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("night", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("night", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("day", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("day", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("day", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("day", false, "00:00"));
                                        wpg.data.get(wpg.valid_days[j]).add(new Switch("day", false, "00:00"));

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thermostat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.weekOverview:
                return true;
            case R.id.setDayNight:
                intent = new Intent(this, SetDayNight.class);
                startActivity(intent);
                break;
            case R.id.activityThermostat:
                intent = new Intent(this, ThermostatActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
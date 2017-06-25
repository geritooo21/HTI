package nl.tue.demothermostat;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.thermostatapp.util.*;

/**
 * Created by s168239
 */

public class SecretSettings extends AppCompatActivity {

    private LinearLayout activityLayout;
    private Handler handler = new Handler();
    private boolean thread;
    private Spinner spinner;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_secret);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.homeiconsecret);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //thread = false;
                startActivity(new Intent(v.getContext(), ThermostatActivity.class));
            }
        });

        activityLayout = (LinearLayout) findViewById(R.id.settings_secret);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        time = (TextView) findViewById(R.id.time);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.week_days, R.layout.simple_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        update();

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                final int editHour = Integer.parseInt(time.getText().toString().substring(0, 2));
                final int editMinute = Integer.parseInt(time.getText().toString().substring(3, 5));

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(SecretSettings.this, R.style.DialogThemeSecret, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selection = selectedHour + ":" + selectedMinute;
                        if (selectedMinute < 10 && selectedHour < 10) {
                            selection = "0" + selectedHour + ":0" + selectedMinute;
                        } else if (selectedMinute < 10) {
                            selection = selectedHour + ":0" + selectedMinute;
                        } else if (selectedHour < 10) {
                            selection = "0" + selectedHour + ":" + selectedMinute;
                        }
                        time.setText(selection);
                        uploadTimeOnServer();
                    }
                }, editHour, editMinute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                uploadDayOnServer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onResume() {
        updater();
        super.onResume();
    }

    @Override
    public void onPause() {
        thread = false;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thermostat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setDayNight:
                //thread = false;
                startActivity(new Intent(this, SetDayNight.class));
                break;
            case R.id.weekOverview:
                //thread = false;
                startActivity(new Intent(this, WeekOverview.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String currentTime = HeatingSystem.get("time");
                    String day = HeatingSystem.get("day");
                    int index = 0;
                    for (int i = 0; i < WeekProgram.valid_days.length; i++) {
                        if (WeekProgram.valid_days[i].equals(day)) {
                            index = i;
                        }
                    }
                    final int dayIndex = index;

                    activityLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            time.setText(currentTime);
                            spinner.setSelection(dayIndex);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void updater() {
        thread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (thread) {
                    try {
                        Thread.sleep(2000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                update();
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    public void uploadDayOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("day", spinner.getSelectedItem().toString());
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void uploadTimeOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("time", time.getText().toString());
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}

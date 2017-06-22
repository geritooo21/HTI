package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;

import org.thermostatapp.util.*;

/**
 * Created by s168945
 */

public class ThermostatActivity extends AppCompatActivity {

    private LinearLayout activityLayout;
    private TextView currTemp, day, time, targetTemp;
    private SeekBar seekBar;
    private Switch vacMode;
    private int target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityLayout = (LinearLayout) findViewById(R.id.activity_thermostat);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        RelativeLayout circleLayout = (RelativeLayout) findViewById(R.id.circleLayout);
        currTemp = (TextView) findViewById(R.id.currTemp);
        day = (TextView) findViewById(R.id.day);
        time = (TextView) findViewById(R.id.time);
        ImageView bPlus = (ImageView) findViewById(R.id.bPlus);
        targetTemp = (TextView) findViewById(R.id.targetTemp);
        ImageView bMinus = (ImageView) findViewById(R.id.bMinus);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        vacMode = (Switch) findViewById(R.id.vacMode);

        updateOverview();

        circleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCircle();
            }
        });

        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (target < 300) {
                    target = target + 1;
                    setTargetText();
                    seekBar.setProgress(target-50);
                    uploadTargetOnServer();
                }
            }
        });

        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (target >50) {
                    target = target - 1;
                    setTargetText();
                    seekBar.setProgress(target-50);
                    uploadTargetOnServer();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                target = progress + 50;
                setTargetText();
             }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uploadTargetOnServer();
            }
        });

        vacMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.put("weekProgramState", "off");
                            } catch (Exception e) {
                                System.err.println("Error from getdata " + e);
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.put("weekProgramState", "on");
                            } catch (Exception e) {
                                System.err.println("Error from getdata " + e);
                            }
                        }
                    }).start();
                }
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
        switch (item.getItemId()) {
            case R.id.activityThermostat:
                return true;
            case R.id.weekOverview:
                Intent intent = new Intent(this, WeekOverview.class);
                startActivity(intent);
                break;
            case R.id.setDayNight:
                Intent intent1 = new Intent(this, SetDayNight.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateOverview() {
        updateCircle();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Load all data form server
                    final String targetTempString = HeatingSystem.get("targetTemperature");
                    try {
                        target = Integer.parseInt(targetTempString.substring(0, 2) + targetTempString.substring(3, 4));
                    } catch (Exception e) {
                        target = Integer.parseInt(targetTempString.substring(0, 1) + targetTempString.substring(2, 3));
                    }
                    final String weekProgStateString = HeatingSystem.get("weekProgramState");

                    //Update the "View"
                    activityLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            targetTemp.setText(targetTempString + "\u2103");

                            seekBar.setProgress(target-50);

                            if (weekProgStateString.equals("on")) {
                                vacMode.setChecked(false);
                            }
                            if (weekProgStateString.equals("off")) {
                                vacMode.setChecked(true);
                            }
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void updateCircle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Load all data form server
                    final String currTempString = HeatingSystem.get("currentTemperature");
                    final String dayString = HeatingSystem.get("day");
                    final String timeString = HeatingSystem.get("time");

                    //Update the "View"
                    activityLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            currTemp.setText(currTempString + "\u2103");
                            day.setText(dayString);
                            time.setText(timeString);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void setTargetText() {
        if (target < 100) {
            targetTemp.setText(Integer.toString(target).substring(0,1) + "." + Integer.toString(target).substring(1,2) + "\u2103");
        } else {
            targetTemp.setText(Integer.toString(target).substring(0,2) + "." + Integer.toString(target).substring(2,3) + "\u2103");
        }
    }

    public void uploadTargetOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (target < 100) {
                        HeatingSystem.put("targetTemperature", Integer.toString(target).substring(0,1) + "." + Integer.toString(target).substring(1,2));
                    } else {
                        HeatingSystem.put("targetTemperature", Integer.toString(target).substring(0,2) + "." + Integer.toString(target).substring(2,3));
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}
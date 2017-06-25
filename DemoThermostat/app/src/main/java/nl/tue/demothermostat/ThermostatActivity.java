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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;
import android.os.Handler;
import android.view.View.OnLongClickListener;

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
    private Handler handler = new Handler();
    private boolean thread = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityLayout = (LinearLayout) findViewById(R.id.activity_thermostat);
        ImageView circle = (ImageView) findViewById(R.id.circle);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        currTemp = (TextView) findViewById(R.id.currTemp);
        day = (TextView) findViewById(R.id.day);
        time = (TextView) findViewById(R.id.time);
        ImageView bPlus = (ImageView) findViewById(R.id.bPlus);
        targetTemp = (TextView) findViewById(R.id.targetTemp);
        ImageView bMinus = (ImageView) findViewById(R.id.bMinus);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        vacMode = (Switch) findViewById(R.id.vacMode);

        update();

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        circle.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //thread = false;
                startActivity(new Intent(view.getContext(), SecretSettings.class));
                return true;
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
                if (target > 50) {
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
            case R.id.weekOverview:
                //thread = false;
                startActivity(new Intent(this, WeekOverview.class));
                break;
            case R.id.setDayNight:
                //thread = false;
                startActivity(new Intent(this, SetDayNight.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Load all data form server
                    final String currTempString = HeatingSystem.get("currentTemperature");
                    final String dayString = HeatingSystem.get("day");
                    final String timeString = HeatingSystem.get("time");
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
                            currTemp.setText(currTempString + "\u2103");
                            day.setText(dayString);
                            time.setText(timeString);
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
                    } catch (Exception e) {}
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
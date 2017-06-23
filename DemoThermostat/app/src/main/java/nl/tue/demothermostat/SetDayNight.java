package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.thermostatapp.util.*;

/**
 * Created by s168945
 */

public class SetDayNight extends AppCompatActivity {

    private LinearLayout activityLayout;
    private TextView dayTemp, nightTemp;
    private SeekBar daySeekbar, nightSeekbar;
    private int day, night;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daynight_set);

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

        activityLayout = (LinearLayout) findViewById(R.id.daynight_set);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        ImageView dayPlus = (ImageView) findViewById(R.id.plusDay);
        ImageView dayMinus = (ImageView) findViewById(R.id.minusDay);
        ImageView nightPlus = (ImageView) findViewById(R.id.plusNight);
        ImageView nightMinus = (ImageView) findViewById(R.id.minusNight);
        dayTemp = (TextView) findViewById(R.id.dayTemp);
        nightTemp = (TextView) findViewById(R.id.nightTemp);
        daySeekbar = (SeekBar) findViewById(R.id.daySeekbar);
        nightSeekbar = (SeekBar) findViewById(R.id.nightSeekbar);

        updateOverview();

        dayPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (day < 300) {
                    day++;
                    setDayText();
                    daySeekbar.setProgress(day-50);
                    uploadDayOnServer();
                }
            }
        });

        dayMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (day >50) {
                    day--;
                    setDayText();
                    daySeekbar.setProgress(day-50);
                    uploadDayOnServer();
                }
            }
        });

        daySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                day = progress + 50;
                setDayText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uploadDayOnServer();
            }
        });

        nightPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (night < 300) {
                    night++;
                    setNightText();
                    nightSeekbar.setProgress(night-50);
                    uploadNightOnServer();
                }
            }
        });

        nightMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (night > 50) {
                    night--;
                    setNightText();
                    nightSeekbar.setProgress(night-50);
                    uploadNightOnServer();
                }
            }
        });

        nightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                night = progress + 50;
                setNightText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uploadNightOnServer();
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
            case R.id.setDayNight:
                return true;
            case R.id.weekOverview:
                Intent intent = new Intent(this, WeekOverview.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateOverview() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String dayTempString = HeatingSystem.get("dayTemperature");
                    try {
                        day = Integer.parseInt(dayTempString.substring(0, 2) + dayTempString.substring(3, 4));
                    } catch (Exception e) {
                        day = Integer.parseInt(dayTempString.substring(0, 1) + dayTempString.substring(2, 3));
                    }

                    final String nightTempString = HeatingSystem.get("nightTemperature");
                    try {
                        night = Integer.parseInt(nightTempString.substring(0, 2) + nightTempString.substring(3, 4));
                    } catch (Exception e) {
                        night = Integer.parseInt(nightTempString.substring(0, 1) + nightTempString.substring(2, 3));
                    }

                    activityLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            dayTemp.setText(dayTempString + "\u2103");
                            daySeekbar.setProgress(day - 50);
                            nightTemp.setText(nightTempString + "\u2103");
                            nightSeekbar.setProgress(night - 50);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void setDayText() {
        if (day < 100) {
            dayTemp.setText(Integer.toString(day).substring(0,1) + "." + Integer.toString(day).substring(1,2) + "\u2103");
        } else {
            dayTemp.setText(Integer.toString(day).substring(0,2) + "." + Integer.toString(day).substring(2,3) + "\u2103");
        }
    }

    public void setNightText() {
        if (night < 100) {
            nightTemp.setText(Integer.toString(night).substring(0,1) + "." + Integer.toString(night).substring(1,2) + "\u2103");
        } else {
            nightTemp.setText(Integer.toString(night).substring(0,2) + "." + Integer.toString(night).substring(2,3) + "\u2103");
        }
    }

    public void uploadDayOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (day < 100) {
                        HeatingSystem.put("dayTemperature", Integer.toString(day).substring(0, 1) + "." + Integer.toString(day).substring(1, 2));
                    } else {
                        HeatingSystem.put("dayTemperature", Integer.toString(day).substring(0, 2) + "." + Integer.toString(day).substring(2, 3));
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void uploadNightOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (night < 100) {
                        HeatingSystem.put("nightTemperature", Integer.toString(night).substring(0, 1) + "." + Integer.toString(night).substring(1, 2));
                    } else {
                        HeatingSystem.put("nightTemperature", Integer.toString(night).substring(0, 2) + "." + Integer.toString(night).substring(2, 3));
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}

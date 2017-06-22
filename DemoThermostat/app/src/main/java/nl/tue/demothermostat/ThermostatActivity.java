package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;

import static nl.tue.demothermostat.R.id.data2;

public class ThermostatActivity extends AppCompatActivity {

    double vTemp;
    int vTempProg;
    TextView targetTemp, day, time, currTemp;
    SeekBar seekBar;
    Button setDayTemp, setNightTemp;
    String dayTempString, nightTempString, targetTempString, dayString, timeString, currTempString,weekProgStateString;
    EditText nightTemp, dayTemp;
    Switch vacMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        targetTemp = (TextView) findViewById(R.id.targetTemp);
        day = (TextView) findViewById(R.id.day);
        time = (TextView) findViewById(R.id.time);
        currTemp = (TextView) findViewById(R.id.currTemp);
        ImageView bPlus = (ImageView) findViewById(R.id.bPlus);
        ImageView bMinus = (ImageView) findViewById(R.id.bMinus);
        vacMode = (Switch) findViewById(R.id.vacMode);

        vacMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                weekProgStateString = "off";
                                HeatingSystem.put("weekProgramState", weekProgStateString);
                                WeekProgram wpg = HeatingSystem.getWeekProgram();
                                wpg.setDefault();
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
                                weekProgStateString = "on";
                                HeatingSystem.put("weekProgramState", weekProgStateString);
                                WeekProgram wpg = HeatingSystem.getWeekProgram();
                                wpg.setDefault();
                            } catch (Exception e) {
                                System.err.println("Error from getdata " + e);
                            }
                        }
                    }).start();

                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                weekProgStateString = "";
                try {
                    weekProgStateString = HeatingSystem.get("weekProgramState");
                    if (weekProgStateString.equals("on")) {
                        vacMode.setChecked(false);
                    }
                    if (weekProgStateString.equals("off")) {
                        vacMode.setChecked(true);
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                targetTempString = "";
                try {
                    targetTempString = HeatingSystem.get("targetTemperature");
                            /*
									HeatingSystem.get("day");
									HeatingSystem.get("time");
									HeatingSystem.get("targetTemperature");
									HeatingSystem.get("dayTemperature");
									HeatingSystem.get("nightTemperature");
									HeatingSystem.get("weekProgramState");
							*/
                    targetTemp.post(new Runnable() {
                        @Override
                        public void run() {
                            vTemp = Double.parseDouble(targetTempString);
                            targetTemp.setText(vTemp + "\u2103");
                            vTempProg = (int)Math.round(vTemp*10);
                            seekBar.setProgress(vTempProg);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dayString = "";
                try {
                    dayString = HeatingSystem.get("day");
                            /*
									HeatingSystem.get("day");
									HeatingSystem.get("time");
									HeatingSystem.get("targetTemperature");
									HeatingSystem.get("dayTemperature");
									HeatingSystem.get("nightTemperature");
									HeatingSystem.get("weekProgramState");
							*/
                    day.post(new Runnable() {
                        @Override
                        public void run() {
                            day.setText(dayString);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                timeString = "";
                try {
                    timeString = HeatingSystem.get("time");
                            /*
									HeatingSystem.get("day");
									HeatingSystem.get("time");
									HeatingSystem.get("targetTemperature");
									HeatingSystem.get("dayTemperature");
									HeatingSystem.get("nightTemperature");
									HeatingSystem.get("weekProgramState");
							*/
                    time.post(new Runnable() {
                        @Override
                        public void run() {
                            time.setText(timeString);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                currTempString = "";
                try {
                    currTempString = HeatingSystem.get("currentTemperature");
                            /*
									HeatingSystem.get("day");
									HeatingSystem.get("time");
									HeatingSystem.get("targetTemperature");
									HeatingSystem.get("dayTemperature");
									HeatingSystem.get("nightTemperature");
									HeatingSystem.get("weekProgramState");
							*/
                    currTemp.post(new Runnable() {
                        @Override
                        public void run() {
                            currTemp.setText(currTempString + "\u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();



        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vTemp = Math.round(vTemp * 10.0 + 1) / 10.0;
                vTempProg = (int)Math.round(vTemp*10);
                if(vTempProg<=50) {
                    vTempProg = 50;
                    vTemp = 5;
                } else {
                    if(vTempProg>=300) {
                        vTempProg = 300;
                        vTemp = 30;
                    }
                }
                targetTemp.setText(vTemp + "\u2103");
                seekBar.setProgress(vTempProg);
                uploadTargetOnServer();
            }
        });

        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vTemp = Math.round((vTemp * 10.0) - 1) / 10.0;
                vTempProg = (int)Math.round(vTemp*10);
                if(vTempProg<=50) {
                    vTempProg = 50;
                    vTemp = 5;
                } else {
                    if(vTempProg>=300) {
                        vTempProg = 300;
                        vTemp = 30;
                    }
                }
                targetTemp.setText(vTemp + "\u2103");
                seekBar.setProgress(vTempProg);
                uploadTargetOnServer();
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(vTempProg);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vTemp = progress/10.0;
                vTempProg = (int)Math.round(vTemp*10);
                if(vTempProg<=50) {
                    vTempProg = 50;
                    vTemp = 5;
                } else {
                    if(vTempProg>=300) {
                        vTempProg = 300;
                        vTemp = 30;
                    }
                }
                targetTemp.setText(vTemp + "\u2103");
                uploadTargetOnServer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thermostat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    public void uploadTargetOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    targetTempString = String.valueOf(vTemp);
                    HeatingSystem.put("targetTemperature", targetTempString);


                            /* Uncomment the following parts to see how to work with the properties of the week program */
                    // Get the week program
                    WeekProgram wpg = HeatingSystem.getWeekProgram();
                    // Set the week program to default
                    wpg.setDefault();
                            /*
                            wpg.data.get("Monday").set(5, new Switch("day", true, "07:30"));
                            wpg.data.get("Monday").set(1, new Switch("night", true, "08:30"));
                            wpg.data.get("Monday").set(6, new Switch("day", true, "18:00"));
                            wpg.data.get("Monday").set(7, new Switch("day", true, "12:00"));
                            wpg.data.get("Monday").set(8, new Switch("day", true, "18:00"));
                            boolean duplicates = wpg.duplicates(wpg.data.get("Monday"));
                            System.out.println("Duplicates found "+duplicates);
                            */
                    //Upload the updated program
                    //HeatingSystem.setWeekProgram(wpg);

//                            data2.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    data2.setText(oldv);
//                                }
//                            });
//                            data3.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    data3.setText(newv);
//                                }
//                            });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}

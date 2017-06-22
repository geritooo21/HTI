package nl.tue.demothermostat;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;

import static nl.tue.demothermostat.R.id.bPlus;
import static nl.tue.demothermostat.R.id.data2;
import static nl.tue.demothermostat.R.id.seekBar;
import static nl.tue.demothermostat.R.id.targetTemp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by s168945 on 21-6-2017.
 */

public class SetDayNight extends AppCompatActivity {

    ImageView dayPlus, dayMinus, nightPlus, nightMinus;
    String dayTempString, nightTempString;
    double dayvTemp, nightvTemp;
    int dayTempProg, nightTempProg;
    TextView dayTemp, nightTemp;
    SeekBar daySeekbar, nightSeekbar;
    Intent home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daynight_set);
        Toolbar toolbar3 = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar3);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar3.setNavigationIcon(R.drawable.homeicon);

        toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"your icon was clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(v.getContext(), ThermostatActivity.class));
            }
        });


        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        dayPlus = (ImageView) findViewById(R.id.plusDay);
        dayMinus = (ImageView) findViewById(R.id.minusDay);
        nightPlus = (ImageView) findViewById(R.id.plusNight);
        nightMinus = (ImageView) findViewById(R.id.minusNight);
        dayTemp = (TextView) findViewById(R.id.dayTemp);
        nightTemp = (TextView) findViewById(R.id.nightTemp);
        daySeekbar = (SeekBar) findViewById(R.id.daySeekbar);
        nightSeekbar = (SeekBar) findViewById(R.id.nightSeekbar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                dayTempString = "";
                try {
                    dayTempString = HeatingSystem.get("dayTemperature");
                    dayTemp.post(new Runnable() {
                        @Override
                        public void run() {
                            dayvTemp = Double.parseDouble(dayTempString);
                            dayTemp.setText(dayvTemp + "\u2103");
                            dayTempProg = (int)Math.round(dayvTemp*10);
                            daySeekbar.setProgress(dayTempProg);
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
                nightTempString = "";
                try {
                    nightTempString = HeatingSystem.get("nightTemperature");
                    nightTemp.post(new Runnable() {
                        @Override
                        public void run() {
                            nightvTemp = Double.parseDouble(nightTempString);
                            nightTemp.setText(nightvTemp + "\u2103");
                            nightTempProg = (int)Math.round(nightvTemp*10);
                            nightSeekbar.setProgress(nightTempProg);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();


    dayPlus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dayvTemp = Math.round(dayvTemp * 10.0 + 1) / 10.0;
            dayTempProg = (int)Math.round(dayvTemp*10);
            if(dayTempProg<=50) {
                dayTempProg = 50;
                dayvTemp = 5;
            } else {
                if(dayTempProg>=300) {
                    dayTempProg = 300;
                    dayvTemp = 30;
                }
            }
            dayTemp.setText(dayvTemp + "\u2103");
            daySeekbar.setProgress(dayTempProg);
            uploadDayTempOnServer();
        }
    });

        dayMinus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dayvTemp = Math.round((dayvTemp * 10.0) - 1) / 10.0;
            dayTempProg = (int)Math.round(dayvTemp*10);
            if(dayTempProg<=50) {
                dayTempProg = 50;
                dayvTemp = 5;
            } else {
                if(dayTempProg>=300) {
                    dayTempProg = 300;
                    dayvTemp = 30;
                }
            }
            dayTemp.setText(dayvTemp + "\u2103");
            daySeekbar.setProgress(dayTempProg);
            uploadDayTempOnServer();
        }
    });

        daySeekbar.setProgress(dayTempProg);
        daySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            dayvTemp = progress/10.0;
            dayTempProg = (int)Math.round(dayvTemp*10);
            if(dayTempProg<=50) {
                dayTempProg = 50;
                dayvTemp = 5;
            } else {
                if(dayTempProg>=300) {
                    dayTempProg = 300;
                    dayvTemp = 30;
                }
            }
            dayTemp.setText(dayvTemp + "\u2103");
            uploadDayTempOnServer();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    });


        nightPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nightvTemp = Math.round(nightvTemp * 10.0 + 1) / 10.0;
                nightTempProg = (int)Math.round(nightvTemp*10);
                if(nightTempProg<=50) {
                    nightTempProg = 50;
                    nightvTemp = 5;
                } else {
                    if(nightTempProg>=300) {
                        nightTempProg = 300;
                        nightvTemp = 30;
                    }
                }
                nightTemp.setText(nightvTemp + "\u2103");
                nightSeekbar.setProgress(nightTempProg);
                uploadNightTempOnServer();
            }
        });

        nightMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nightvTemp = Math.round((nightvTemp * 10.0) - 1) / 10.0;
                nightTempProg = (int)Math.round(nightvTemp*10);
                if(nightTempProg<=50) {
                    nightTempProg = 50;
                    nightvTemp = 5;
                } else {
                    if(nightTempProg>=300) {
                        nightTempProg = 300;
                        nightvTemp = 30;
                    }
                }
                nightTemp.setText(nightvTemp + "\u2103");
                nightSeekbar.setProgress(nightTempProg);
                uploadNightTempOnServer();
            }
        });

        nightSeekbar.setProgress(nightTempProg);
        nightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                nightvTemp = progress/10.0;
                nightTempProg = (int)Math.round(nightvTemp*10);
                if(nightTempProg<=50) {
                    nightTempProg = 50;
                    nightvTemp = 5;
                } else {
                    if(nightTempProg>=300) {
                        nightTempProg = 300;
                        nightvTemp = 30;
                    }
                }
                nightTemp.setText(nightvTemp + "\u2103");
                uploadNightTempOnServer();
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
            case R.id.setDayNight:
                return true;
            case R.id.weekOverview:
                Intent intent = new Intent(this, WeekOverview.class);
                startActivity(intent);
                break;
            case R.id.activityThermostat:
                Intent intent1 = new Intent(this, ThermostatActivity.class);
                startActivity(intent1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void uploadDayTempOnServer() {
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {
                  dayTempString = String.valueOf(dayvTemp);
                  HeatingSystem.put("dayTemperature", dayTempString);

                  WeekProgram wpg = HeatingSystem.getWeekProgram();
                  wpg.setDefault();
              } catch (Exception e) {
                  System.err.println("Error from getdata " + e);
              }
          }
      }).start();
  }

  public void uploadNightTempOnServer() {
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {
                  nightTempString = String.valueOf(nightvTemp);
                  HeatingSystem.put("nightTemperature", nightTempString);

                  WeekProgram wpg = HeatingSystem.getWeekProgram();
                  wpg.setDefault();
              } catch (Exception e) {
                  System.err.println("Error from getdata " + e);
              }
          }
      }).start();
  }
}

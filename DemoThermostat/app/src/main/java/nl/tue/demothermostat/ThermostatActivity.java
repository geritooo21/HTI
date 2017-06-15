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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;

public class ThermostatActivity extends AppCompatActivity {

    int vTemp = 21;
    TextView temp, dayTemp;
    SeekBar seekBar;
    Button getDayTemp;
    String dayTempString;
    EditText nightTempText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDayTemp =(Button)findViewById(R.id.getDayTemp);
        temp = (TextView) findViewById(R.id.temp);

        ImageView bPlus = (ImageView) findViewById(R.id.bPlus);
        ImageView bMinus = (ImageView) findViewById(R.id.bMinus);
        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vTemp++;
                temp.setText(vTemp + "\u2103");
                seekBar.setProgress(vTemp);
            }
        });
        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vTemp--;
                temp.setText(vTemp + "\u2103");
                seekBar.setProgress(vTemp);
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(vTemp);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vTemp = progress;
                temp.setText(progress + "\u2103");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dayTemp = (TextView)findViewById(R.id.dayTemp);
        nightTempText = (EditText)findViewById(R.id.nightTemp);

        getDayTemp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dayTempString = "";
                        try {
                            dayTempString = HeatingSystem.get("dayTemperature");
                            /*
									HeatingSystem.get("day");
									HeatingSystem.get("time");
									HeatingSystem.get("targetTemperature");
									HeatingSystem.get("dayTemperature");
									HeatingSystem.get("nightTemperature");
									HeatingSystem.get("weekProgramState");
							*/
                            dayTemp.post(new Runnable() {
                                @Override
                                public void run() {
                                    dayTemp.setText(dayTempString);
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("Error from getdata " + e);
                        }
                    }
                }).start();
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
        }

        return super.onOptionsItemSelected(item);
    }
}

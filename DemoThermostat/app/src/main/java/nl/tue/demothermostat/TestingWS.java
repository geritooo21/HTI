package nl.tue.demothermostat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.thermostatapp.util.*;

/**
 * Created by s168945 on 12-6-2017.
 */

public class TestingWS extends AppCompatActivity {
    Button getdata, putdata;
    TextView data1, data2, data3;
    String getParam, oldv, newv, dayTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_ws);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        getdata = (Button) findViewById(R.id.getdata);
        putdata = (Button) findViewById(R.id.putdata);
        data1 = (TextView) findViewById(R.id.data1);
        data2 = (TextView) findViewById(R.id.data2);
        data3 = (TextView) findViewById(R.id.data3);

        getdata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getParam = "";
                        try {
                            getParam = HeatingSystem.get("dayTemperature");
                            /*
									HeatingSystem.get("day");
									HeatingSystem.get("time");
									HeatingSystem.get("targetTemperature");
									HeatingSystem.get("dayTemperature");
									HeatingSystem.get("nightTemperature");
									HeatingSystem.get("weekProgramState");
							*/
                            data1.post(new Runnable() {
                                @Override
                                public void run() {
                                    data1.setText(getParam);
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("Error from getdata " + e);
                        }
                    }
                }).start();
            }
        });


        putdata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            oldv = HeatingSystem.get("time");
                            HeatingSystem.put("time", "15:42");
                            newv = HeatingSystem.get("time");

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

                            data2.post(new Runnable() {
                                @Override
                                public void run() {
                                    data2.setText(oldv);
                                }
                            });
                            data3.post(new Runnable() {
                                @Override
                                public void run() {
                                    data3.setText(newv);
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
}
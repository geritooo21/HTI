package nl.tue.demothermostat;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;

import org.thermostatapp.util.*;

/**
 * Created by s168239
 */

public class DayOverview extends AppCompatActivity {

    private WeekProgram wpg;
    public static int dayNumber = 0;
    private LinearLayout activityLayout;
    ArrayList<Switch> switches = new ArrayList<>();

    private LinearLayout[] layout = new LinearLayout[10];
    private ImageView[] icon = new ImageView[10];
    private TextView[] time = new TextView[10];

    private ImageView[] edit = new ImageView[10];
    private ImageView[] delete = new ImageView[10];

    private boolean editType, plusType; //day = true, night = false
    private int editHour, editMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_overview);
        setTitle(WeekProgram.valid_days[dayNumber] + " Overview");

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

        activityLayout = (LinearLayout) findViewById(R.id.day_overview);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/50";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        for (int i = 0; i < layout.length; i++) {
            String ID = "layout" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            layout[i] = (LinearLayout) findViewById(resID);
        }
        for (int i = 0; i < icon.length; i++) {
            String ID = "icon" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            icon[i] = (ImageView) findViewById(resID);
        }
        for (int i = 0; i < time.length; i++) {
            String ID = "time" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            time[i] = (TextView) findViewById(resID);
        }

        update();
        createDayOverview();

        for (int i = 0; i < edit.length; i++) {
            final int j = i;
            String ID = "edit" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            edit[i] = (ImageView) findViewById(resID);
            edit[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        editType = true;

                        AlertDialog.Builder menuBuilder = new AlertDialog.Builder(DayOverview.this);
                        View menuView = getLayoutInflater().inflate(R.layout.edit, null);

                        final ImageView type = (ImageView) menuView.findViewById(R.id.type);
                        final TextView time = (TextView) menuView.findViewById(R.id.time);
                        final Button cancel = (Button) menuView.findViewById(R.id.cancel);
                        final Button edit = (Button) menuView.findViewById(R.id.edit);

                        menuBuilder.setView(menuView);
                        final AlertDialog dialog = menuBuilder.create();
                        dialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    WeekProgram wpg = HeatingSystem.getWeekProgram();

                                    final String currentType = wpg.data.get(wpg.valid_days[dayNumber]).get(j).getType();
                                    final String currentTime = wpg.data.get(wpg.valid_days[dayNumber]).get(j).getTime();

                                    editHour = Integer.parseInt(currentTime.substring(0, 2));
                                    editMinute = Integer.parseInt(currentTime.substring(3, 5));

                                    activityLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (currentType.equals("day")) {
                                                type.setImageResource(R.drawable.day);
                                                editType = true;
                                            } else if (currentType.equals("night")) {
                                                type.setImageResource(R.drawable.night);
                                                editType = false;
                                            }
                                            time.setText(currentTime);
                                        }
                                    });
                                } catch (Exception e) {
                                    activityLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(DayOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }).start();

                        type.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (editType) {
                                    type.setImageResource(R.drawable.night);
                                    editType = false;
                                } else {
                                    type.setImageResource(R.drawable.day);
                                    editType = true;
                                }
                            }
                        });

                        time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TimePickerDialog timePicker;
                                timePicker = new TimePickerDialog(DayOverview.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
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
                                    }
                                }, editHour, editMinute, true);
                                timePicker.setTitle("Select Time");
                                timePicker.show();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(DayOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String type = "night";
                                if (editType) {
                                    type = "day";
                                }

                                final Switch plus = new Switch(type, true, time.getText().toString());

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            WeekProgram wpg = HeatingSystem.getWeekProgram();

                                            wpg.data.get(wpg.valid_days[dayNumber]).set(j, new Switch(wpg.data.get(wpg.valid_days[dayNumber]).get(j).getType(), false, "00:00"));

                                            if (editType) {
                                                for (int i = 0; i < wpg.data.get(wpg.valid_days[dayNumber]).size(); i++) {
                                                    if (!wpg.data.get(wpg.valid_days[dayNumber]).get(i).getState() && wpg.data.get(wpg.valid_days[dayNumber]).get(i).getType().equals("day")) {
                                                        wpg.data.get(wpg.valid_days[dayNumber]).set(i, plus);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                for (int i = 0; i < wpg.data.get(wpg.valid_days[dayNumber]).size(); i++) {
                                                    if (!wpg.data.get(wpg.valid_days[dayNumber]).get(i).getState() && wpg.data.get(wpg.valid_days[dayNumber]).get(i).getType().equals("night")) {
                                                        wpg.data.get(wpg.valid_days[dayNumber]).set(i, plus);
                                                        break;
                                                    }
                                                }
                                            }

                                            HeatingSystem.setWeekProgram(wpg);

                                            createDayOverview();

                                            activityLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(DayOverview.this, R.string.edit, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } catch (Exception e) {
                                            activityLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(DayOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
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

        for (int i = 0; i < delete.length; i++) {
            final int j = i;
            String ID = "delete" + i;
            int resID = getResources().getIdentifier(ID, "id", getPackageName());
            delete[i] = (ImageView) findViewById(resID);
            delete[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                WeekProgram wpg = HeatingSystem.getWeekProgram();

                                wpg.data.get(wpg.valid_days[dayNumber]).set(j, new Switch(wpg.data.get(wpg.valid_days[dayNumber]).get(j).getType(), false, "00:00"));

                                HeatingSystem.setWeekProgram(wpg);

                                createDayOverview();

                                activityLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DayOverview.this, R.string.delete, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                activityLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DayOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });
        }

        LinearLayout plus = (LinearLayout) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    plusType = true;

                    AlertDialog.Builder menuBuilder = new AlertDialog.Builder(DayOverview.this);
                    View menuView = getLayoutInflater().inflate(R.layout.plus, null);

                    final ImageView type = (ImageView) menuView.findViewById(R.id.type);
                    final TextView time = (TextView) menuView.findViewById(R.id.time);
                    final Button cancel = (Button) menuView.findViewById(R.id.cancel);
                    final Button add = (Button) menuView.findViewById(R.id.add);

                    menuBuilder.setView(menuView);
                    final AlertDialog dialog = menuBuilder.create();
                    dialog.show();

                    type.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (plusType) {
                                type.setImageResource(R.drawable.night);
                                plusType = false;
                            } else {
                                type.setImageResource(R.drawable.day);
                                plusType = true;
                            }
                        }
                    });

                    time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePicker;
                            timePicker = new TimePickerDialog(DayOverview.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
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
                                }
                            }, 00, 00, true);
                            timePicker.setTitle("Select Time");
                            timePicker.show();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(DayOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String type = "night";
                            if (plusType) {
                                type = "day";
                            }

                            final Switch plus = new Switch(type, true, time.getText().toString());

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        WeekProgram wpg = HeatingSystem.getWeekProgram();

                                        if (plusType) {
                                            for (int i = 0; i < wpg.data.get(wpg.valid_days[dayNumber]).size(); i++) {
                                                if (!wpg.data.get(wpg.valid_days[dayNumber]).get(i).getState() && wpg.data.get(wpg.valid_days[dayNumber]).get(i).getType().equals("day")) {
                                                    wpg.data.get(wpg.valid_days[dayNumber]).set(i, plus);
                                                    break;
                                                }
                                            }
                                        } else {
                                            for (int i = 0; i < wpg.data.get(wpg.valid_days[dayNumber]).size(); i++) {
                                                if (!wpg.data.get(wpg.valid_days[dayNumber]).get(i).getState() && wpg.data.get(wpg.valid_days[dayNumber]).get(i).getType().equals("night")) {
                                                    wpg.data.get(wpg.valid_days[dayNumber]).set(i, plus);
                                                    break;
                                                }
                                            }
                                        }

                                        HeatingSystem.setWeekProgram(wpg);

                                        createDayOverview();

                                        activityLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(DayOverview.this, R.string.add, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (Exception e) {
                                        activityLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(DayOverview.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
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
                intent = new Intent(this, WeekOverview.class);
                startActivity(intent);
                break;
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

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wpg = HeatingSystem.getWeekProgram();
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void createDayOverview() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WeekProgram wpg = HeatingSystem.getWeekProgram();

                    switches = wpg.data.get(wpg.valid_days[dayNumber]);

                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
                activityLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < layout.length; i++) {
                            if (switches.get(i).getState() && switches.get(i) != null ) {
                                layout[i].setVisibility(View.VISIBLE);
                                if (switches.get(i).getType().equals("day")) {
                                    icon[i].setImageResource(R.drawable.day);
                                } else {
                                    icon[i].setImageResource(R.drawable.night);
                                }
                                time[i].setText(switches.get(i).getTime());
                            } else {
                                layout[i].setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }).start();
    }
}
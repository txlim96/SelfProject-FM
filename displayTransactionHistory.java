package com.example.nelsonlim.financialmanagement;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class displayTransactionHistory extends AppCompatActivity {

    private int transSpinnerPos;
    private ArrayList<Float> net = new ArrayList<>();
    private ArrayList<ArrayList<String>> compiled = new ArrayList<>();
    private Spinner transSpinnerID;
    private TableLayout tableLayoutID;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_transaction_history);

        SpinnerSettings();

        SimpleDateFormat df = new SimpleDateFormat("MM");
        String currentMonth = df.format(calendar.getTime());

        tableLayoutID = (TableLayout) findViewById(R.id.tableLayoutID);
        if(tableLayoutID != null)
            tableLayoutID.removeAllViews();

        net.clear();
        for(int i = 0; i < 3; i++)
            net.add(getIntent().getFloatExtra("amount" + i, 0.0f));

        int row, counter = 0;
        row = getIntent().getIntExtra("size", 0);

        for(int i = 0; i < row; i++){
            ArrayList<String> name = new ArrayList<>();
            int column = getIntent().getIntExtra("col" + i, 0);
            for(int j = 0; j < column; j++){
                name.add(getIntent().getStringExtra("compiled" + i + j));
            }

            String[] prevMonth = String.valueOf(name.get(column-1)).split("/");
            if(Integer.valueOf(currentMonth) - Integer.valueOf(prevMonth[1]) < 3) {
                Log.i("act2", currentMonth);
                Log.i("act2", prevMonth[1]);
                compiled.add(counter, name);
            }
        }

        transSpinnerEvent();
    }

    @Override
    protected void onPause(){
        super.onPause();

        saveTransactions();
    }

    protected void saveTransactions(){
        SharedPreferences pref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.clear().apply();
        editor.putInt("size", compiled.size());
        for(int i = 0; i < compiled.size(); i++){
            editor.putInt("col" + i, compiled.get(i).size());
            for(int j = 0; j < compiled.get(i).size(); j++){
                editor.putString("savedCompiled" + i + j, String.valueOf(compiled.get(i).get(j)));
            }
        }

        for(int i = 0; i < 3; i++)
            editor.putFloat("prefsName" + i, net.get(i));
        editor.apply();
    }

    protected void SpinnerSettings(){
        transSpinnerID = (Spinner) findViewById(R.id.transSpinnerID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transSpinnerID.setAdapter(adapter);
    }

    protected void transSpinnerEvent(){
        transSpinnerID.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        transSpinnerPos = transSpinnerID.getSelectedItemPosition();
                        if(tableLayoutID != null){
                            tableLayoutID.removeAllViews();
                        }
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                        getNames();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    protected void getNames(){
        ArrayList<Integer> contents = new ArrayList<>();

        for(int i = 0; i < compiled.size(); i++){
            if(Integer.valueOf(compiled.get(i).get(0)) == transSpinnerPos){
                contents.add(i);    //sequence of selected items in compiled
            }
        }

        displayTransactionDetails(contents);
    }

    @SuppressLint("NewApi")
    protected void displayTransactionDetails(ArrayList<Integer> count){
        tableLayoutID.removeAllViews();
        for(int i = 0; i < count.size(); i++){
            int x = 0;
            int position = count.get(i);    //retrieve the sequence to apply in compiled
            String detailDescription = "";

            if(transSpinnerPos == 3){
                x = 1;
                if(Integer.valueOf(compiled.get(position).get(1)) == 0 || Integer.valueOf(compiled.get(position).get(1)) == 1)
                    detailDescription = getResources().getStringArray(R.array.secondary_category_array)[Integer.valueOf(compiled.get(position).get(1))]
                            + " - ";
            }

            TextView date = new TextView(this);
            TextView description = new TextView(this);
            TextView amount = new TextView(this);
            final TableRow tableRow = new TableRow(displayTransactionHistory.this);

            String finalString = detailDescription + String.valueOf(compiled.get(position).get(x+1));
            date.setText(String.valueOf(compiled.get(position).get(x+3)));
            description.setText(finalString);
            amount.setText(String.valueOf(compiled.get(position).get(x+2)));

            date.setTextSize(15);
            date.setGravity(Gravity.LEFT);
            date.setTextColor(Color.WHITE);

            description.setMinWidth(400);
            description.setMaxWidth(400);
            description.setPadding(70, 5, 0, 5);
            description.setTextSize(15);
            description.setTextColor(Color.WHITE);

            amount.setTextSize(18);
            amount.setPadding(50, 5, 0, 5);
            amount.setGravity(Gravity.RIGHT);
            amount.setTextColor(Color.WHITE);

            tableRow.addView(date);
            tableRow.addView(description);
            tableRow.addView(amount);
            tableRow.setId(position);

            tableLayoutID.addView(tableRow);

            tableRow.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch(event.getAction()){
                                case MotionEvent.ACTION_DOWN:
                                    tableRow.setBackgroundColor(getResources().getColor(R.color.tableOnClickColor));
                                    break;

                                case MotionEvent.ACTION_UP:
                                    tableRow.setBackgroundColor(getResources().getColor(R.color.windowBackground));
                                    break;
                            }
                            return false;
                        }
                    }
            );

            tableRow.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteDialogFragment(tableRow);
                            return false;
                        }
                    }
            );
        }
    }

    protected void deleteDialogFragment(final TableRow tableRow){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(R.string.question)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeTransaction(tableRow);
                                Toast.makeText(displayTransactionHistory.this,
                                        R.string.confirmed, Toast.LENGTH_LONG).show();
                            }
                        })

                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void removeTransaction(TableRow tableRow){
        tableRow.removeAllViews();

        int amountLocation = 2;
        int tempLocation = Integer.valueOf(compiled.get(tableRow.getId()).get(0));
        float[] temp = new float[3];
        for(int i = 0; i < 3; i++)
            temp[i] = 0;

        if(tempLocation == 3){
            amountLocation += 1;
            tempLocation = Integer.valueOf(compiled.get(tableRow.getId()).get(1));
            if(tempLocation != 0 && tempLocation != 1)
                tempLocation = 2;
        }
        temp[tempLocation] = Float.valueOf(compiled.get(tableRow.getId()).get(amountLocation));
        temp[2] = temp[tempLocation];

        for(int i = 0; i < 3; i++){
            temp[i] = net.get(i) - temp[i];
        }

        net.clear();
        for(int i = 0; i < 3; i++)
            net.add(temp[i]);
        compiled.remove(tableRow.getId());

        getNames();
        saveTransactions();
    }
}
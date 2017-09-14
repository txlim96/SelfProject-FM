package com.example.nelsonlim.financialmanagement;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class displayTransactionHistory extends AppCompatActivity {

    private int transSpinnerPos;
    private static int row;
    private ArrayList<ArrayList<String>> compiled = new ArrayList<>();
    private Spinner transSpinnerID;
    private TableLayout tableLayoutID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_transaction_history);

        SpinnerSettings();

        tableLayoutID = (TableLayout) findViewById(R.id.tableLayoutID);
        if(tableLayoutID != null)
            tableLayoutID.removeAllViews();
        screenEvents();

        row = getIntent().getIntExtra("size", 0);
        transSpinnerEvent();
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
                        getNames();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    protected void getNames(){
        ArrayList<Integer> contents = new ArrayList<>();
        for(int i = 0; i < row; i++){
            ArrayList<String> name = new ArrayList<>();
            int column = getIntent().getIntExtra("col" + i, 0);
            for(int j = 0; j < column; j++){
                name.add(getIntent().getStringExtra("compiled" + i + j));
            }
            compiled.add(i, name);
        }

        //Log.i("savedAmount", String.valueOf(compiled));

        for(int i = 0; i < row; i++){
            if(Integer.valueOf(compiled.get(i).get(0)) == transSpinnerPos){
                contents.add(i);    //sequence of selected items in compiled
            }
        }
        displayTransactionDetails(contents);
    }

    @SuppressLint("NewApi")
    protected void displayTransactionDetails(ArrayList<Integer> count){

        for(int i = 0; i < count.size(); i++){
            int position = count.get(i);    //retrieve the sequence to apply in compiled
            String detailDescription = "";
            //Log.i("savedAmount", String.valueOf(position));
            int x = 0;
            if(transSpinnerPos == 3){
                x = 1;
                if(Integer.valueOf(compiled.get(position).get(1)) == 0 || Integer.valueOf(compiled.get(position).get(1)) == 1)
                    detailDescription = getResources().getStringArray(R.array.secondary_category_array)[Integer.valueOf(compiled.get(position).get(1))]
                            + " - ";
            }

            TextView date = new TextView(this);
            TextView description = new TextView(this);
            TextView amount = new TextView(this);
            TableRow tableRow = new TableRow(displayTransactionHistory.this);

            String finalString = detailDescription + String.valueOf(compiled.get(position).get(x+1));
            date.setText(String.valueOf(compiled.get(position).get(x+3)));
            description.setText(finalString);
            amount.setText(String.valueOf(compiled.get(position).get(x+2)));

            date.setTextSize(15);
            date.setGravity(Gravity.LEFT);

            description.setMinWidth(500);
            description.setMaxWidth(500);
            description.setPadding(70, 5, 0, 5);
            description.setTextSize(15);

            amount.setTextSize(18);
            amount.setGravity(Gravity.RIGHT);

            tableRow.addView(date);
            tableRow.addView(description);
            tableRow.addView(amount);

            tableLayoutID.addView(tableRow);
        }
    }

    protected void screenEvents(){
        tableLayoutID.setOnLongClickListener(
                new GridLayout.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //Log.i("savedAmount", "false");
                        return false;
                    }
                }
        );
    }
}
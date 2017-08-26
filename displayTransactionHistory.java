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
            Log.i("saved", String.valueOf(compiled.get(i)));
        }

        for(int i = 0; i < row; i++){
            if(Integer.valueOf(compiled.get(i).get(0)) == transSpinnerPos){
                contents.add(i);
            }
        }
        displayTransactionDetails(contents);
    }

    @SuppressLint("NewApi")
    protected void displayTransactionDetails(ArrayList<Integer> count){
        int x;
        if(transSpinnerPos == 3) x = 1;
        else x = 0;

        for(int i = 0; i < count.size(); i++){
            int position = count.get(i);

            TextView description = new TextView(this);
            TextView amount = new TextView(this);
            TableRow tableRow = new TableRow(displayTransactionHistory.this);

            description.setText(String.valueOf(compiled.get(position).get(x+1)));
            amount.setText(String.valueOf(compiled.get(position).get(x+2)));

            description.setPadding(150, 0, 220, 0);
            description.setTextSize(18);
            amount.setTextSize(18);
            amount.setGravity(Gravity.RIGHT);

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
                        Log.i("savedAmount", "false");
                        return false;
                    }
                }
        );
    }
}
package com.example.nelsonlim.financialmanagement;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class displayTransactionHistory extends AppCompatActivity {

    private int transSpinnerPos;
    private static int row;
    private ArrayList<ArrayList<String>> compiled = new ArrayList<>();
    private Spinner transSpinnerID;
    private GridLayout displayGridID;
    private TextView displayTextID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_transaction_history);

        SpinnerSettings();

        displayGridID = (GridLayout) findViewById(R.id.displayGridID);
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
                        if(displayGridID != null){
                            displayGridID.removeAllViews();
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
        for(int i = 0; i < row; i++){
            if(Integer.valueOf(compiled.get(i).get(0)) == transSpinnerPos){
                contents.add(i);
            }
        }
        displayTransactionDetails(contents);
    }

    @SuppressLint("NewApi")
    protected void displayTransactionDetails(ArrayList<Integer> count){
        for(int i = 0; i < count.size(); i++){
            int position = count.get(i);
            for(int j = 0; j < compiled.get(count.get(i)).size(); j++){
                displayTextID = new TextView(this);
                displayTextID.setLayoutParams(new ActionBar.LayoutParams(110, 100));
                displayTextID.setTextSize(15);
                displayTextID.setMinWidth(200);
                displayTextID.setText(String.valueOf(compiled.get(position).get(j)));

                GridLayout.Spec col = GridLayout.spec(j+1, 1);
                GridLayout.Spec row = GridLayout.spec(i, 1);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, col);

                displayGridID.addView(displayTextID, params);
            }
        }
    }
}
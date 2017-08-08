package com.example.nelsonlim.financialmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static int row = 0;
    private int spinnerPosition;
    private int secondarySpinnerPosition;
    private float[] amount = new float[4];
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<ArrayList<String>> compiled = new ArrayList<>();
    private Button buttonID;
    private Button transButtonID;
    private EditText inputID;
    private EditText inputName;
    private GridLayout gridViewID;
    private Intent intent;
    private TextView[] amountID = new TextView[3];
    private TextView displayID;
    private Spinner spinnerID;
    private Spinner secondarySpinnerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings();
        SpinnerEvent();
        buttonEvents();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        row = pref.getInt("size", 0);

        for(int i = 0; i < 3; i++) {
            amount[i] = pref.getFloat(String.valueOf("prefsName" + i), 0.0f);
        }

        for(int i = 0; i < row; i++){
            name = new ArrayList<>();
            int column = pref.getInt("col" + i, 0);
            for(int j = 0; j < column; j++){
                name.add(pref.getString("savedCompiled" + i + j, null));
            }
            compiled.add(i, name);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("prefsName", 3);

        for(int i = 0; i < 3; i++) {
            editor.putFloat(String.valueOf("prefsName" + i), amount[i]);
        }

        editor.clear();
        editor.putInt("size", row);
        for(int i = 0; i < row; i++){
            editor.putInt("col" + i, compiled.get(i).size());
            for(int j = 0; j < compiled.get(i).size(); j++){
                editor.putString("savedCompiled" + i + j, String.valueOf(compiled.get(i).get(j)));
            }
        }
        editor.commit();
    }

    protected void Settings(){

        inputName = new EditText(this);
        inputName.setSingleLine();
        secondarySpinnerID = new Spinner(this);
        buttonID = (Button) findViewById(R.id.buttonID);
        transButtonID = (Button) findViewById(R.id.transButtonID);
        inputID = (EditText) findViewById(R.id.inputID);
        spinnerID = (Spinner) findViewById(R.id.spinnerID);
        intent = new Intent(MainActivity.this, displayTransactionHistory.class);

        ///////////////////Research//////////////////////
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerID.setAdapter(adapter);
        ////////////////Don't understand/////////////////

        for(int i = 0; i < 3; i++){
            String textID = "amount" + i + "ID";
            int resID = getResources().getIdentifier(textID, "id", getPackageName());
            amountID[i] = (TextView) findViewById(resID);
        }
    }

    @SuppressLint("NewApi")
    protected void secondaryInputSetup(int spinnerPosition){
        displayID = new TextView(this);
        gridViewID = (GridLayout) findViewById(R.id.gridViewID);
        if (gridViewID != null) {
            gridViewID.removeAllViews();
        }
        displayID.setLayoutParams(new ActionBar.LayoutParams(110, 110));
        displayID.setTextSize(18);

        GridLayout.Spec col = GridLayout.spec(GridLayout.UNDEFINED, 1);
        GridLayout.Spec row = GridLayout.spec(GridLayout.UNDEFINED, 1);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, col);
        GridLayout.LayoutParams params1 = new GridLayout.LayoutParams(row, col);
        gridViewID.addView(displayID, params);

        if(spinnerPosition == 0 || spinnerPosition == 1){
            inputName.setLayoutParams(new ActionBar.LayoutParams(100, 20));
            gridViewID.addView(inputName, params1);
        }
        else if(spinnerPosition >= 3 && spinnerPosition <= 5){
            ArrayAdapter<CharSequence> secondaryAdapter = ArrayAdapter.createFromResource(this, R.array.secondary_category_array,
                    android.R.layout.simple_spinner_item);
            secondaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            secondarySpinnerID.setAdapter(secondaryAdapter);
            gridViewID.addView(secondarySpinnerID, params1);
        }
    }

    @SuppressLint("NewApi")
    protected void tertiaryInputSetup(){
        if(gridViewID != null){
            gridViewID.removeView(inputName);
        }
        GridLayout.Spec col1 = GridLayout.spec(GridLayout.HORIZONTAL, 1);
        GridLayout.Spec row1 = GridLayout.spec(GridLayout.VERTICAL, 2);

        GridLayout.LayoutParams params2 = new GridLayout.LayoutParams(row1, col1);
        inputName.setLayoutParams(new ActionBar.LayoutParams(100, 20));
        gridViewID.addView(inputName, params2);
    }

    protected void SpinnerEvent(){
        spinnerID.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerPosition = spinnerID.getSelectedItemPosition();
                        switch(spinnerPosition){
                            case 0:
                                secondaryInputSetup(spinnerPosition);
                                displayID.setText(getString(R.string.description1_res));
                                break;

                            case 1:
                                secondaryInputSetup(spinnerPosition);
                                displayID.setText(getString(R.string.description2_res));
                                break;

                            case 2:
                                gridViewID.removeAllViews();
                                break;

                            case 3:
                                secondaryInputSetup(spinnerPosition);
                                displayID.setText(getString(R.string.description3_res));
                                break;

                            default:
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        secondarySpinnerID.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        secondarySpinnerPosition = secondarySpinnerID.getSelectedItemPosition();
                        switch(secondarySpinnerPosition){
                            case 0:
                            case 1:
                                tertiaryInputSetup();
                                break;
                            default:
                                ((ViewGroup) inputName.getParent()).removeView(inputName);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    protected void buttonEvents(){
        assert buttonID != null;
        assert transButtonID != null;
        buttonID.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        name = new ArrayList<>();
                        spinnerPosition = spinnerID.getSelectedItemPosition();

                        name.add(String.valueOf(spinnerPosition));

                        if(inputID.getText().toString().matches("")){
                            amount[spinnerPosition] += (float) 0.00;
                        }
                        else{
                            switch (spinnerPosition){
                                case 0:
                                case 1:
                                    name.add(inputName.getText().toString());
                                    name.add(String.valueOf(Float.valueOf(inputID.getText().toString())));

                                    amount[spinnerPosition] += Float.valueOf(inputID.getText().toString());
                                    if (spinnerPosition == 0)
                                        amount[2] += Float.valueOf(inputID.getText().toString());
                                    else if(spinnerPosition == 1)
                                        amount[2] -= Float.valueOf(inputID.getText().toString());
                                    break;

                                case 2:
                                    name.add(getResources().getStringArray(R.array.category_array)[spinnerPosition]);
                                    name.add(String.valueOf(Float.valueOf(inputID.getText().toString())));
                                    amount[2] += Float.valueOf(inputID.getText().toString());
                                    break;

                                case 3:
                                    secondarySpinnerPosition = secondarySpinnerID.getSelectedItemPosition();
                                    name.add(String.valueOf(secondarySpinnerPosition));
                                    amount[3] = Float.valueOf(inputID.getText().toString());

                                    if(secondarySpinnerPosition == 0){
                                        name.add(inputName.getText().toString());
                                        amount[secondarySpinnerPosition] -= amount[3];
                                        amount[2] -= amount[3];
                                    }
                                    else if(secondarySpinnerPosition == 1){
                                        name.add(inputName.getText().toString());
                                        amount[secondarySpinnerPosition] -= amount[3];
                                        amount[2] += amount[3];
                                    }
                                    else{
                                        name.add(getResources().getStringArray(R.array.secondary_category_array)
                                                [secondarySpinnerPosition]);
                                        amount[2] -= amount[3];
                                    }
                                    name.add(String.valueOf(amount[3]));
                                    break;
                            }
                        }
                        compiled.add(row, name);
                        row++;

                        for(int i = 0; i < 3; i++){
                            amountID[i].setText(String.valueOf(amount[i]));
                        }
                        inputID.setText("");
                        inputName.setText("");
                    }
                }
        );

        transButtonID.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        if(compiled.isEmpty()){
                            compiled = null;
                        }
                        else{
                            for(int i = 0; i < row; i++){
                                for(int j = 0; j < compiled.get(i).size(); j++){
                                    intent.putExtra("col" + i, compiled.get(i).size());
                                    intent.putExtra("compiled" + i + j, String.valueOf(compiled.get(i).get(j)));
                                }
                            }
                        }
                        intent.putExtra("size", row);
                        startActivity(intent);
                    }
                }
        );
    }
}
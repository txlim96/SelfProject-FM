package com.example.nelsonlim.financialmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        setContentView(R.layout.activity_main);

        Settings();
        SpinnerEvent();
        buttonEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences("myPrefs", MODE_PRIVATE);

        compiled.clear();
        row = pref.getInt("size", 0);

        for(int i = 0; i < 3; i++) {
            amount[i] = pref.getFloat("prefsName" + i, 0.0f);
        }

        for(int i = 0; i < row; i++){
            name = new ArrayList<>();
            int column = pref.getInt("col" + i, 0);
            for(int j = 0; j < column; j++){
                name.add(pref.getString("savedCompiled" + i + j, null));
            }
            compiled.add(i, name);
        }

        for(int i = 0; i < 3; i++){
            amountID[i].setText(String.valueOf(amount[i]));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences pref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("prefsName", 3);
        editor.clear().apply();

        for(int i = 0; i < 3; i++) {
            editor.putFloat("prefsName" + i, amount[i]);
        }

        editor.putInt("size", row);
        for(int i = 0; i < row; i++){
            editor.putInt("col" + i, compiled.get(i).size());
            for(int j = 0; j < compiled.get(i).size(); j++){
                editor.putString("savedCompiled" + i + j, String.valueOf(compiled.get(i).get(j)));
            }
        }
        editor.apply();
    }

    protected void Settings(){

        inputName = new EditText(this);
        inputName.setSingleLine();
        inputName.setTextColor(Color.WHITE);
        secondarySpinnerID = new Spinner(this);
        secondarySpinnerID.getContext().setTheme(R.style.SpinnerTheme);
        secondarySpinnerID.setPadding(15, 20, 0, 0);
        buttonID = (Button) findViewById(R.id.buttonID);
        transButtonID = (Button) findViewById(R.id.transButtonID);
        inputID = (EditText) findViewById(R.id.inputID);
        inputID.setTextColor(Color.WHITE);
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
    protected void secondaryInputSetup(){
        gridViewID = (GridLayout) findViewById(R.id.gridViewID);
        if (gridViewID != null) {
            gridViewID.removeAllViews();
        }

        displayID = new TextView(this);

        displayID.setTextSize(18);
        displayID.setPadding(33, 20, 0, 0);

        if(spinnerPosition == 3){
            GridLayout.Spec col2 = GridLayout.spec(0, 1);
            GridLayout.Spec row2 = GridLayout.spec(0, 1);
            GridLayout.LayoutParams params2 = new GridLayout.LayoutParams(row2, col2);
            gridViewID.addView(secondarySpinnerID, params2);
        }

        GridLayout.Spec col = GridLayout.spec(0, 1);
        GridLayout.Spec row = GridLayout.spec(1, 1);
        GridLayout.Spec col1 = GridLayout.spec(1, 1);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, col);
        gridViewID.addView(displayID, params);

        inputName.setLayoutParams(new ActionBar.LayoutParams(110, 20));
        inputName.setMinWidth(200);

        GridLayout.LayoutParams params1 = new GridLayout.LayoutParams(row, col1);
        gridViewID.addView(inputName, params1);

    }

    protected void SpinnerEvent(){
        spinnerID.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerPosition = spinnerID.getSelectedItemPosition();
                        switch(spinnerPosition){
                            case 0:
                                secondaryInputSetup();
                                displayID.setText(getString(R.string.description1_res));
                                break;

                            case 1:
                                secondaryInputSetup();
                                displayID.setText(getString(R.string.description2_res));
                                break;

                            case 2:
                                gridViewID.removeAllViews();
                                break;

                            case 3:
                                ArrayAdapter<CharSequence> secondaryAdapter = ArrayAdapter.createFromResource(MainActivity.this,
                                        R.array.secondary_category_array, android.R.layout.simple_spinner_item);
                                secondaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                secondarySpinnerID.setAdapter(secondaryAdapter);
                                secondaryInputSetup();
                                break;

                            default:
                        }
                        //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                        displayID.setTextColor(Color.WHITE);
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
                                secondaryInputSetup();
                                displayID.setText(R.string.description2_res);
                                break;
                            case 1:
                                secondaryInputSetup();
                                displayID.setText(R.string.description1_res);
                                break;
                            default:
                                if(inputName.getParent() != null) {
                                    ((ViewGroup) inputName.getParent()).removeView(inputName);
                                    ((ViewGroup) displayID.getParent()).removeView(displayID);
                                }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    protected void addDate(){
        if(inputName.getText().toString().length() > 0 || inputID.getText().toString().length() > 0){
            SimpleDateFormat df = new SimpleDateFormat("dd/MM");
            String date = df.format(calendar.getTime());
            name.add(String.valueOf(date));
            compiled.add(row, name);
            row++;
        }
    }

    protected void buttonEvents(){
        assert buttonID != null;
        assert transButtonID != null;
        buttonID.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        int sign = 1;
                        name = new ArrayList<>();
                        spinnerPosition = spinnerID.getSelectedItemPosition();

                        if(inputID.getText().toString().matches("")){
                            amount[spinnerPosition] += (float) 0.00;
                        }
                        else{
                            switch (spinnerPosition){
                                case 0:
                                case 1:
                                    if(inputName.getText().toString().length() == 0 || inputID.getText().toString().length() == 0){
                                        Toast.makeText(MainActivity.this, R.string.input_text, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    if(spinnerPosition == 1 && amount[2] < Float.valueOf(inputID.getText().toString())){
                                        Toast.makeText(MainActivity.this, R.string.insufficient_credit, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    name.add(String.valueOf(spinnerPosition));
                                    name.add(inputName.getText().toString());

                                    if(spinnerPosition == 1)
                                        sign = -1;
                                    amount[spinnerPosition] += sign * Float.valueOf(inputID.getText().toString());
                                    name.add(String.valueOf(sign * Float.valueOf(inputID.getText().toString())));
                                    amount[2] += sign * Float.valueOf(inputID.getText().toString());
                                    addDate();
                                    break;

                                case 2:
                                    if(inputID.getText().toString().length() == 0){
                                        Toast.makeText(MainActivity.this, R.string.input_text, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    name.add(String.valueOf(spinnerPosition));
                                    name.add(getResources().getStringArray(R.array.category_array)[spinnerPosition]);
                                    name.add(String.valueOf(Float.valueOf(inputID.getText().toString())));
                                    amount[2] += Float.valueOf(inputID.getText().toString());
                                    addDate();
                                    break;

                                case 3:
                                    secondarySpinnerPosition = secondarySpinnerID.getSelectedItemPosition();
                                    if((inputName.getText().toString().length() == 0 || inputID.getText().toString().length() == 0)
                                            && (secondarySpinnerPosition == 1 || secondarySpinnerPosition == 0)){
                                        Toast.makeText(MainActivity.this, R.string.input_text, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    if(amount[2] < Float.valueOf(inputID.getText().toString())){
                                        Toast.makeText(MainActivity.this, R.string.insufficient_credit, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    name.add(String.valueOf(spinnerPosition));
                                    name.add(String.valueOf(secondarySpinnerPosition));

                                    if(secondarySpinnerPosition == 0){
                                        name.add(inputName.getText().toString());
                                        sign = -1;
                                        amount[3] = sign * Float.valueOf(inputID.getText().toString());
                                        amount[secondarySpinnerPosition] += amount[3];
                                    }
                                    else if(secondarySpinnerPosition == 1){
                                        name.add(inputName.getText().toString());
                                        amount[3] = sign * Float.valueOf(inputID.getText().toString());
                                        amount[secondarySpinnerPosition] += amount[3];
                                    }
                                    else{
                                        name.add(getResources().getStringArray(R.array.secondary_category_array)
                                                [secondarySpinnerPosition]);
                                        sign = -1;
                                        amount[3] = sign * Float.valueOf(inputID.getText().toString());
                                    }
                                    amount[2] += amount[3];
                                    name.add(String.valueOf(amount[3]));
                                    addDate();
                                    break;
                            }
                        }

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
                        for(int i = 0; i < 3; i++) {
                            intent.putExtra("amount" + i, amount[i]);
                        }
                        if(compiled.isEmpty()){
                            compiled.clear();
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
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

        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
        String date = df.format(calendar.getTime());
        Log.i("act2", date);

        /*String[] separated = date.split("/");
        Log.i("act2", separated[0]);
        Log.i("act2", separated[1]);*/

        tableLayoutID = (TableLayout) findViewById(R.id.tableLayoutID);
        if(tableLayoutID != null)
            tableLayoutID.removeAllViews();

        net.clear();
        for(int i = 0; i < 3; i++)
            net.add(getIntent().getFloatExtra("amount" + i, 0.0f));

        int row;
        row = getIntent().getIntExtra("size", 0);

        for(int i = 0; i < row; i++){
            ArrayList<String> name = new ArrayList<>();
            int column = getIntent().getIntExtra("col" + i, 0);
            for(int j = 0; j < column; j++){
                name.add(getIntent().getStringExtra("compiled" + i + j));
            }
            compiled.add(i, name);
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

            description.setMinWidth(480);
            description.setMaxWidth(480);
            description.setPadding(0, 5, 0, 5);
            description.setTextSize(15);
            description.setTextColor(Color.WHITE);

            amount.setTextSize(18);
            amount.setGravity(Gravity.RIGHT);
            amount.setTextColor(Color.WHITE);

            tableRow.addView(date);
            tableRow.addView(description);
            tableRow.addView(amount);
            tableRow.setId(position);

            tableLayoutID.addView(tableRow);

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

        int sign = 1, amountLocation = 2, tempLocation = 1;
        float[] temp = new float[3];
        for(int i = 0; i < 3; i++)
            temp[i] = 0;

        ArrayList<String> toBeRemoved = compiled.get(tableRow.getId());
        if(Integer.valueOf(toBeRemoved.get(0)) != 1){
            tempLocation = Integer.valueOf(toBeRemoved.get(0));
            sign = -1;
            if(Integer.valueOf(toBeRemoved.get(0)) == 3){
                tempLocation = Integer.valueOf(toBeRemoved.get(1));
                amountLocation += 1;
                if(Integer.valueOf(toBeRemoved.get(1)) != 1)
                    sign = 1;
            }
        }
        temp[2] = sign * Float.valueOf(toBeRemoved.get(amountLocation));
        temp[tempLocation] = temp[2];
        for(int i = 0; i < 3; i++){
            temp[i] = temp[i] + net.get(i);
            Log.i("act2", String.valueOf(temp[i]));
        }
        net.clear();
        for(int i = 0; i < 3; i++)
            net.add(temp[i]);
        compiled.remove(tableRow.getId());

        /*if(compiled.size() > 0){
            for(int i = 0; i < compiled.size(); i++){
                int sign = 1;
                String loc = compiled.get(i).get(0);
                if(Integer.valueOf(loc) == 3){
                    loc = compiled.get(i).get(1);
                    sign = -1;
                }
                temp[Integer.valueOf(loc)] += Float.valueOf(compiled.get(i).get(compiled.get(i).size()-2)) * sign;
            }
        }
        net.clear();
        for(int i = 0; i < 2; i++)
            net.add(temp[i]);
        net.add(temp[0] - temp[1] + temp[2] - temp[3]);
*/
        getNames();
        saveTransactions();
    }
}
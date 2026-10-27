package com.resultbrowser.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_SERVER_NAME = "extra_server_name";
    public static final String EXTRA_BOARD = "extra_board";
    public static final String EXTRA_ROLL = "extra_roll";
    public static final String EXTRA_REG = "extra_reg";

    private static final String PREFS_NAME = "HSC26Prefs";
    private static final String KEY_BOARD = "board";
    private static final String KEY_ROLL = "roll";
    private static final String KEY_REG = "reg";

    private Spinner spinnerBoard;
    private EditText etRoll;
    private EditText etReg;

    // Board list (value that goes into the website)
    private final String[] boardValues = {
            "dinajpur", "barisal", "chittagong", "comilla", "dhaka",
            "jessore", "madrasah", "mymensingh", "rajshahi", "sylhet", "technical"
    };

    private final String[] boardLabels = {
            "Dinajpur", "Barisal", "Chittagong", "Comilla", "Dhaka",
            "Jessore", "Madrasah", "Mymensingh", "Rajshahi", "Sylhet", "Technical"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerBoard = findViewById(R.id.spinnerBoard);
        etRoll = findViewById(R.id.etRoll);
        etReg = findViewById(R.id.etReg);

        // Setup board spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, boardLabels);
        spinnerBoard.setAdapter(adapter);

        // Load saved values
        loadSavedData();

        Button btnServer1 = findViewById(R.id.btnServer1);
        Button btnServer2 = findViewById(R.id.btnServer2);

        btnServer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView(
                        "https://eboardresults.com/v2/home",
                        "Server 1 - eboardresults.com"
                );
            }
        });

        btnServer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView(
                        "https://www.educationboardresults.gov.bd/v2/home",
                        "Server 2 - educationboardresults.gov.bd"
                );
            }
        });
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String savedBoard = prefs.getString(KEY_BOARD, "dinajpur");
        String savedRoll = prefs.getString(KEY_ROLL, "");
        String savedReg = prefs.getString(KEY_REG, "");

        // Set board
        for (int i = 0; i < boardValues.length; i++) {
            if (boardValues[i].equalsIgnoreCase(savedBoard)) {
                spinnerBoard.setSelection(i);
                break;
            }
        }

        etRoll.setText(savedRoll);
        etReg.setText(savedReg);
    }

    private void saveData(String board, String roll, String reg) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BOARD, board);
        editor.putString(KEY_ROLL, roll);
        editor.putString(KEY_REG, reg);
        editor.apply();
    }

    private void openWebView(String url, String serverName) {
        String roll = etRoll.getText().toString().trim();
        String reg = etReg.getText().toString().trim();
        int boardIndex = spinnerBoard.getSelectedItemPosition();
        String board = boardValues[boardIndex];

        if (roll.isEmpty() || reg.isEmpty()) {
            Toast.makeText(this, "রোল ও রেজিস্ট্রেশন নম্বর দিন", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save for next time
        saveData(board, roll, reg);

        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_SERVER_NAME, serverName);
        intent.putExtra(EXTRA_BOARD, board);
        intent.putExtra(EXTRA_ROLL, roll);
        intent.putExtra(EXTRA_REG, reg);
        startActivity(intent);
    }
}

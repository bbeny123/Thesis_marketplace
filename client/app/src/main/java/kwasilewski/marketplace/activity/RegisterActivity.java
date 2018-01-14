package kwasilewski.marketplace.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import kwasilewski.marketplace.R;

public class RegisterActivity extends Activity {

    private Spinner province;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String[] arraySpinner = new String[] {
                "Province"
        };
        province = findViewById(R.id.register_province);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province.setAdapter(adapter);

    }
}

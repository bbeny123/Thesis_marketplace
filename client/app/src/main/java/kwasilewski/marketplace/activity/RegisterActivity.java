package kwasilewski.marketplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.util.AutoCompleteDropDown;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteDropDown province;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String[] arraySpinner = new String[] {
                "City"
        };
        province = findViewById(R.id.register_province);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, arraySpinner);
        province.setAdapter(adapter);

    }
}

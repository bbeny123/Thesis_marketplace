package kwasilewski.marketplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.util.MRKUtil;

public class NetErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_error);

        Button refreshButton = findViewById(R.id.net_error_refresh_button);
        refreshButton.setOnClickListener(view -> refresh());
    }

    private void refresh() {
        if (MRKUtil.isNetworkAvailable(this)) {
            finish();
        } else {
            MRKUtil.connectionProblem(this);
        }
    }
}

package kwasilewski.marketplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.util.MRKUtil;

public class NetErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_error);

        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

    }

    private void refresh() {
        if (MRKUtil.isNetworkAvailable(this)) {
            finish();
        } else {
            MRKUtil.connectionProblem(this);
        }
    }
}

package kwasilewski.marketplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.ad.AdMinimalData;
import kwasilewski.marketplace.retrofit.RetrofitSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button xdButton;
    private TextView text;
    private Set<AdMinimalData> ads = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.xdText);
        final Set<AdMinimalData> ads = new HashSet<>();
        xdButton = findViewById(R.id.xdbutt);

        xdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getAds();
            }
        });
    }

    private void getAds() {
        RetrofitSingleton.getInstance().getAdService().getAds(new HashMap<String, String>()).enqueue(new Callback<List<AdMinimalData>>() {
            @Override
            public void onResponse(Call<List<AdMinimalData>> call, Response<List<AdMinimalData>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ads.addAll(response.body());
                    text.setText(ads.size() + "");
                }
            }

            @Override
            public void onFailure(Call<List<AdMinimalData>> call, Throwable t) {

            }
        });
    }
}

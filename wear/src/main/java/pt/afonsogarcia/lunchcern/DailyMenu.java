package pt.afonsogarcia.lunchcern;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.Calendar;

public class DailyMenu extends Activity {

    private TextView mTextView;
    private Integer dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_menu);

        Calendar cal = Calendar.getInstance();
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                generateDailyMenu();
            }
        });
    }

    private void generateDailyMenu() {
        switch(dayOfWeek) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }
}

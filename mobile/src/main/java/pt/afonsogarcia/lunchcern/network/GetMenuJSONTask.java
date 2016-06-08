package pt.afonsogarcia.lunchcern.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.afonsogarcia.lunchcern.WeeklyMenu;

public class GetMenuJSONTask extends AsyncTask<Void, Integer, String> {
    WeeklyMenu menuActivity;
    String urlString = "http://46.101.191.246:8080/menu.json";

    public GetMenuJSONTask(WeeklyMenu menuActivity) {
        super();
        this.menuActivity = menuActivity;
    }

    @Override
    protected String doInBackground(Void... params) {
        String content = "";

        try {
            URL menuURL = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) menuURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            InputStream in = conn.getInputStream();
            String encoding = conn.getContentEncoding();
            encoding = (encoding == null ? "UTF-8" : encoding);
            content = IOUtils.toString(in, encoding);

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    protected void onPostExecute(String content) {
        menuActivity.receiveMenuJSON(content);
    }
}

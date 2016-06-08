package pt.afonsogarcia.lunchcern.network;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import pt.afonsogarcia.lunchcern.WeeklyMenu;
import pt.afonsogarcia.lunchcern.domain.DayMenuItem;
import pt.afonsogarcia.lunchcern.domain.WeekDay;

public class BuildMenuTask extends AsyncTask<String, Integer, HashMap<Integer, WeekDay>> {
    WeeklyMenu menuActivity;

    public BuildMenuTask(WeeklyMenu menuActivity) {
        super();
        this.menuActivity = menuActivity;
    }

    @Override
    protected HashMap<Integer, WeekDay> doInBackground(String... params) {
        HashMap<Integer, WeekDay> weekMenu = new HashMap<>();

        try {
            JSONObject menu = new JSONObject(params[0]);
            Iterator<String> keys = menu.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                JSONArray menuArray = menu.getJSONArray(key);
                String day = key.replaceAll("[\\t\\n\\r]"," ");

                WeekDay weekDay = new WeekDay(day);

                for(int i = 0; i < menuArray.length(); i++) {
                    JSONObject menuItemJSON = menuArray.getJSONObject(i);

                    String type = menuItemJSON.getString("type").replaceAll("[\\t\\n\\r]"," ");
                    String desc = menuItemJSON.getString("description").replaceAll("[\\t\\n\\r]"," ");
                    String price = menuItemJSON.getString("price").replaceAll("[\\t\\n\\r]"," ");

                    if(!desc.equals("N/A")) {
                        DayMenuItem menuItem = new DayMenuItem(type, desc, price);
                        weekDay.addDayMenuItem(menuItem);
                    }
                }

                weekMenu.put(weekDay.getWeekDay(), weekDay);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return weekMenu;
    }

    @Override
    protected void onPostExecute(HashMap<Integer, WeekDay> content) {
        menuActivity.receiveMenu(content);
    }
}

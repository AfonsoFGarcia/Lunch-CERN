package pt.afonsogarcia.lunchcern.domain;

import java.util.ArrayList;
import java.util.List;

public class WeekDay {
    private String weekDayName;
    private Integer weekDay;
    private List<DayMenuItem> dayMenuItems;

    public WeekDay(String weekDayName) {
        this.weekDayName = weekDayName;
        this.dayMenuItems = new ArrayList<>();
        parseWeekDay();
    }

    private void parseWeekDay() {
        String[] split = weekDayName.split(" ", 2);
        switch(split[0]) {
            case "Lundi":
                weekDay = 1;
                break;
            case "Mardi":
                weekDay = 2;
                break;
            case "Mercredi":
                weekDay = 3;
                break;
            case "Jeudi":
                weekDay = 4;
                break;
            case "Vendredi":
                weekDay = 5;
                break;
            default:
                weekDay = 0;
        }
    }

    public void addDayMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.add(dayMenuItem);
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public Integer getDayMenuItemsLength() {
        return dayMenuItems.size();
    }

    public DayMenuItem getDayMenuItem(int position) {
        return dayMenuItems.get(position);
    }

    public String getWeekDayName() {
        return weekDayName;
    }
}

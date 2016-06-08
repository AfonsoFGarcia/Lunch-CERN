package pt.afonsogarcia.lunchcern.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.afonsogarcia.lunchcern.R;
import pt.afonsogarcia.lunchcern.domain.DayMenuItem;
import pt.afonsogarcia.lunchcern.domain.WeekDay;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private WeekDay data;

    public  MenuAdapter(WeekDay data) {
        super();
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DayMenuItem item = data.getDayMenuItem(position);
        holder.desc.setText(item.getDescription());
        holder.type.setText(item.getType());
        holder.price.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return data.getDayMenuItemsLength();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView desc;
        public TextView type;
        public TextView price;

        public ViewHolder(View v) {
            super(v);
            desc = (TextView) v.findViewById(R.id.menu_desc);
            type = (TextView) v.findViewById(R.id.menu_type);
            price = (TextView) v.findViewById(R.id.menu_price);
        }
    }
}

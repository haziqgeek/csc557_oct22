package com.example.csc557_oct22.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.csc557_oct22.model.User;

import java.util.List;

public class LecturerSpinnerAdapter extends ArrayAdapter<User> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private List<User> lecturers;

    public LecturerSpinnerAdapter(Context context, int textViewResourceId, List<User> lecturers) {
        super(context, textViewResourceId, lecturers);
        this.context = context;
        this.lecturers = lecturers;
    }

    @Override
    public int getCount() { return lecturers.size(); }

    @Override
    public User getItem(int position) { return lecturers.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(lecturers.get(position).getUsername());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(lecturers.get(position).getUsername());

        return label;
    }
}

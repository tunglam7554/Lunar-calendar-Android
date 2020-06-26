package com.tulasoft.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;


public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView txtDay, txtDate, txtMonth;
    LunarCalendar lunar;
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDay = (TextView) findViewById(R.id.txtDay);
        txtMonth = (TextView) findViewById(R.id.txtMonth);
        lunar = new LunarCalendar();
        c = Calendar.getInstance();
        getLunar(c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);
                getLunar(c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                getLunar(dayOfMonth, month + 1, year);
            }
        });

    }
    protected void getLunar(int dayOfMonth, int month, int year){
        int[] amlich = lunar.convertSolar2Lunar(dayOfMonth, month, year, 7);
        String nam = lunar.getYear(year);
        String ngaythang = "Âm lịch: " + amlich[0] + "/" + amlich[1] + "/" + amlich[2] + " (" + nam + ")";
        txtDate.setText(ngaythang);
        String ngay = "Ngày " + lunar.getDate(dayOfMonth, month, year);
        txtDay.setText(ngay);
        String thang = "Tháng " + lunar.getMonth(amlich[0], amlich[1], amlich[2]);
        txtMonth.setText(thang);
    }
}

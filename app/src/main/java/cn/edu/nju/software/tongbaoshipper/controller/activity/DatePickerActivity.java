package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.nju.software.tongbaoshipper.R;

public class DatePickerActivity extends AppCompatActivity implements View.OnClickListener{


    private TimePicker timePicker;
    private LinearLayout btn_ok;
    private LinearLayout btn_back;
    private MaterialCalendarView calendarView;
    final int RESULT_CODE_DATE=101;
    final int RESULT_CODE_START=102;
    final int RESULT_CODE_ARRIVE=103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        initView();
    }

    /**
     * init view
     */
    private void initView() {


        timePicker=(TimePicker)findViewById(R.id.timePicker);
        calendarView=(MaterialCalendarView) findViewById(R.id.calendarView);
        btn_ok=(LinearLayout)findViewById(R.id.date_pick_btn_ok);
        btn_back=(LinearLayout)findViewById(R.id.date_pick_btn_back);
        timePicker.setIs24HourView(true);

        calendarView.setSelectedDate(new CalendarDay());
        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_pick_btn_back:
                Log.d(DatePickerActivity.class.getName(), "back");
                finish();
                break;
            case R.id.date_pick_btn_ok:
                Log.d(DatePickerActivity.class.getName(), "ok");
                Intent intent=new Intent();

                Date date=calendarView.getSelectedDate().getDate();
                date.setHours(timePicker.getCurrentHour());
                date.setMinutes(timePicker.getCurrentMinute());
                date.setSeconds(0);

                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(df.format(date));
                intent.putExtra("date", df.format(date));
                setResult(RESULT_CODE_DATE, intent);
                finish();
                break;
            default:
                Log.e(DatePickerActivity.class.getName(), "Unknown id");
                break;
        }
    }
}

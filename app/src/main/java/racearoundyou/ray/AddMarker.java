package racearoundyou.ray;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.RelativeDateTimeFormatter;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;


public  class AddMarker extends BottomSheetDialogFragment{
    private RelativeLayout main;
    private TextView date;
    private TextView time;
    private HashMap<String, Integer> interests;
    private Event mEvent;
    private int counter;
    private EditText name;
    private CheckBox compete;
    private CheckBox exhibition;
    private CheckBox games;
    private CheckBox tourism;
    private LatLng CurrLatLng;
    OnDataSetListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        CurrLatLng = getArguments().getParcelable("CurrLatLng");
        return inflater.inflate(R.layout.add_marker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        interests = new HashMap<>();
        mEvent = new Event();
        counter = 0;
        main = (RelativeLayout) view;
        name = (EditText) main.findViewById(R.id.eName);
        compete = (CheckBox) main.findViewById(R.id.compete);
        exhibition = (CheckBox) main.findViewById(R.id.exhib);
        games = (CheckBox) main.findViewById(R.id.game);
        tourism = (CheckBox) main.findViewById(R.id.tour);
        date = (TextView) main.findViewById(R.id.date);
        time = (TextView) main.findViewById(R.id.time);
        interests.put(compete.getText().toString(), 0);
        interests.put(exhibition.getText().toString(), 0);
        interests.put(games.getText().toString(), 0);
        interests.put(tourism.getText().toString(), 0);
        //
        compete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                counter++;
                interests.put(compete.getText().toString(), 5 - counter);
            }
        });

        exhibition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                counter++;
                interests.put(exhibition.getText().toString(),5 - counter);
            }
        });

        games.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                counter++;
                interests.put(games.getText().toString(),5 - counter);
            }
        });

        tourism.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                counter++;
                interests.put(tourism.getText().toString(),5 - counter);
            }
        });
        //
        Calendar mCalendar = Calendar.getInstance(TimeZone.getDefault());
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.MyDatePickerStyle, datedlg,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("Выберите дату");

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               datePickerDialog.show();
            }
        });

        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.AppTheme, timedlg,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE), true);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        Button btn = (Button) main.findViewById(R.id.add_event);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEvent.setInterests(interests);
                mEvent.setName(name.getText().toString());
                mEvent.setStartdate(date.getText().toString());
                mEvent.setStarttime(time.getText().toString());
                mEvent.setLatitude(CurrLatLng.latitude);
                mEvent.setLongtitude(CurrLatLng.longitude);
                mCallback.onEventSet(mEvent);
                AddMarker.this.dismiss();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener datedlg = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String year1 = String.valueOf(year);
            String month1 = String.valueOf(month + 1);
            String day1 = String.valueOf(dayOfMonth);
            date.setText(day1 + "/" + month1 + "/" + year1);
        }
    };

    private TimePickerDialog.OnTimeSetListener timedlg = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour = String.valueOf(hourOfDay);
            String min = String.valueOf(minute);
            time.setText(hour + ":" + min);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDataSetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnDataSetListener{
         void onEventSet(Event mEvent);
    }

}

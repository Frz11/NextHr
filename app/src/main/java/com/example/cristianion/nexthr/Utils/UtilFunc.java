package com.example.cristianion.nexthr.Utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public abstract class UtilFunc {
    public  static void showError(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    public static int numOfDaysInMonth(String year,String month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year),Integer.parseInt(month),1);

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    }
}

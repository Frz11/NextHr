package com.example.cristianion.nexthr.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
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
    public static void showSnackbarError(View view, String message){
        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
    }
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }
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
    /**
     * Shows the progress UI and hides the form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final View view, final ProgressBar progressBar) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            view.setVisibility(show ? View.GONE : View.VISIBLE);
            view.animate().alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            view.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

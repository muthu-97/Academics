package com.project.muthuraman.academics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by muthuraman on 29/4/17.
 */

public class AttendanceDate {
     int otp;
     String exptime;
     long currTime ;
     List<Student> students;

    public AttendanceDate(int otp, String exp, List st) {
        this.otp = otp;
        this.exptime = exp;

        // Initialize to current time
        currTime = new Date().getTime();

        students = st;
    }

    public AttendanceDate() {

    }

}

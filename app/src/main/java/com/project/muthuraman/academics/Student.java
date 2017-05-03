package com.project.muthuraman.academics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by muthuraman on 29/4/17.
 */

public class Student {
    String name;
    String id;
    int late;

    public Student( String exp, String st,int late) {
        this.name = exp;
        this.id = st;

        this.late=late;
    }

    public Student() {

    }
}

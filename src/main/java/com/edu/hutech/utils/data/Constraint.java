package com.edu.hutech.utils.data;

import java.util.ArrayList;
import java.util.List;


public class Constraint {

    public static final List<TypeAttendance> TYPE_ATTENDANCE = new ArrayList<>();

    static {
        TYPE_ATTENDANCE.add(TypeAttendance.A);
        TYPE_ATTENDANCE.add(TypeAttendance.An);
        TYPE_ATTENDANCE.add(TypeAttendance.E);
        TYPE_ATTENDANCE.add(TypeAttendance.En);
        TYPE_ATTENDANCE.add(TypeAttendance.L);
        TYPE_ATTENDANCE.add(TypeAttendance.Ln);
        TYPE_ATTENDANCE.add(TypeAttendance.P);
    }

}

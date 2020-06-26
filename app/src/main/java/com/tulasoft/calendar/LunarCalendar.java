package com.tulasoft.calendar;

public class LunarCalendar {
    String[] can = {"Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"};
    String[] chi = {"Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"};
    public String getYear(int year){
        return can[(year + 6) % 10] + " " + chi[(year + 8) % 12];
    };
    protected int Floor(int a){
        return (int)Math.floor(a);
    }
    public int jdFromDate(int dd, int mm, int yy)
    {
        int a = (int)(14 - mm) / 12;
        int y = yy + 4800 - a;
        int m = mm + 12 * a - 3;
        int jd = dd
                + (int)((153 * m + 2) / 5.0f)
                + (365 * y)
                + (int)(y / 4.0f) - (int)(y / 100.0f) + (int)(y / 400.0f) - 32045;
        if (jd < 2299161)
        {
            jd = dd + (int)((153 * m + 2) / 5.0f) + 365 * y + (int)(y / 4.0f) - 32083;
        }
        return jd;
    }
    //chuyen lich duong sang lich am
    public int[] convertSolar2Lunar(int dd, int mm, int yy, int timeZone)
    {
        int dayNumber = jdFromDate(dd, mm, yy);
        int k = (int)((dayNumber - 2415021.076998695) / 29.530588853);
        int monthStart = getNewMoonDay(k + 1, timeZone);
        if (monthStart > dayNumber)
        {
            monthStart = getNewMoonDay(k, timeZone);
        }
        int a11 = getLunarMonth11(yy, timeZone);
        int b11 = a11;
        int lunarYear;
        if (a11 >= monthStart)
        {
            lunarYear = yy;
            a11 = getLunarMonth11(yy - 1, timeZone);
        }
        else
        {
            lunarYear = yy + 1;
            b11 = getLunarMonth11(yy + 1, timeZone);
        }
        int lunarDay = (dayNumber - monthStart + 1);
        int diff = (int)((monthStart - a11) / 29);
        int lunarLeap = 0;
        int lunarMonth = diff + 11;
        if (b11 - a11 > 365)
        {
            int leapMonthDiff = getLeapMonthOffset(a11, timeZone);
            if (diff >= leapMonthDiff)
            {
                lunarMonth = diff + 10;
                if (diff == leapMonthDiff)
                {
                    lunarLeap = 1;
                }
            }
        }
        if (lunarMonth > 12)
        {
            lunarMonth = lunarMonth - 12;
        }
        if (lunarMonth >= 11 && diff < 4)
        {
            lunarYear -= 1;
        }
        return new int[] { lunarDay, lunarMonth, lunarYear };
    }
    // Tinh ngay Soc
    public int getNewMoonDay(int k, int timeZone)
    {
        // T, T2, T3, dr, Jd1, M, Mpr, F, C1, deltat, JdNew;
        double T = k / 1236.85f; // Time in Julian centuries from 1900 January 0.5
        double T2 = T * T;
        double T3 = T2 * T;
        double dr = Math.PI / 180.0f;
        double Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
        Jd1 = Jd1 + 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr); // Mean new moon
        double M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3; // Sun's mean anomaly
        double Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3; // Moon's mean anomaly
        double F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3; // Moon's argument of latitude
        double C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2 * dr * M);
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr);
        C1 = C1 - 0.0004 * Math.sin(dr * 3 * Mpr);
        C1 = C1 + 0.0104 * Math.sin(dr * 2 * F) - 0.0051 * Math.sin(dr * (M + Mpr));
        C1 = C1 - 0.0004 * Math.sin(dr * (2 * F - M)) - 0.0006 * Math.sin(dr * (2 * F + Mpr));
        C1 = C1 + 0.0010 * Math.sin(dr * (2 * F - Mpr)) + 0.0005 * Math.sin(dr * (2 * Mpr + M));
        C1 = C1 - 0.0074 * Math.sin(dr * (M - Mpr)) + 0.0004 * Math.sin(dr * (2 * F + M));
        double deltat;
        if (T < -11)
        {
            deltat = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
        }
        else
        {
            deltat = -0.000278 + 0.000265 * T + 0.000262 * T2;
        };
        double JdNew = Jd1 + C1 - deltat;
        return (int)(JdNew + 0.5 + timeZone / 24.0f);
    }
    // Tim ngay bat dau thang 11 am lich
    public int getLunarMonth11(int yy, int timeZone)
    {
        int off = jdFromDate(31, 12, yy) - 2415021;  // truoc 31/12/yy
        int k = (int)(off / 29.530588853);
        int nm = getNewMoonDay(k, timeZone); // tim ngay soc truoc 31/12/yy
        int sunLong = getSunLongitude(nm, timeZone); // sun longitude at local midnight
        if (sunLong >= 9) // Neu thang bat dau vau ngay soc do khong co dong chi,
        {
            nm = getNewMoonDay(k - 1, timeZone); // thi lui 1 thang va tinh lai ngay soc
        }
        return nm;
    }
    // Trung khi
    public int getSunLongitude(double jdn, int timeZone)
    {
        //double T, T2, dr, M, L0, DL, L;
        double T = (jdn - 2451545.5 - timeZone / 24) / 36525; // Time in Julian centuries from 2000-01-01 12:00:00 GMT
        double T2 = T * T;
        double dr = Math.PI / 180; // degree to radian
        double M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2; // mean anomaly, degree
        double L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2; // mean longitude, degree
        double DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M);
        DL = DL + (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + 0.000290 * Math.sin(dr * 3 * M);
        double L = L0 + DL; // true longitude, degree
        L = L * dr;
        L = L - Math.PI * 2 * ((int)(L / (Math.PI * 2))); // Normalize to (0, 2*PI)
        return (int)(L / Math.PI * 6);
    }
    // Xac dinh thang nhuan
    public int getLeapMonthOffset(long a11, int timeZone)
    {
        int k, last, arc, i;
        k = (int)((a11 - 2415021.076998695) / 29.530588853 + 0.5);
        last = 0;
        i = 1; // We start with the month following lunar month 11
        arc = getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone);
        do
        {
            last = arc;
            i++;
            arc = getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone);
        } while (arc != last && i < 14);
        return i - 2;
    }
    public String getDate(int dd, int mm, int yy)
    {
        int jd = jdFromDate(dd, mm, yy);
        return can[(jd+9)%10] + " " + chi[(jd+1)%12];
    }
    public String getMonth(int dd, int mm, int yy){
        int jd = jdFromDate(dd, mm, yy);
        return can[(yy*12+mm+3) % 10] + " " + chi[(mm+1)%12];
    }

}

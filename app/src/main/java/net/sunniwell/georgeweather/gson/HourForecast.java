package net.sunniwell.georgeweather.gson;

/**
 * Created by admin on 2017/10/10.
 */

public class HourForecast {
        public Cond cond;
        public String date;
        public String hum;
        public String pop;
        public String pres;
        public String tmp;
        public Wind wind;
        public class Cond {
            public String code;
            public String txt;
        }
        public class Wind {
            public String deg;
            public String dir;
            public String sc;
            public String spd;
        }
}

package net.iblankdigital.auto.utils;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateTimeUtils {
//    http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html


    private static final Locale LOCALE = Locale.ENGLISH;

    public static String formatLong(long timeStamp, String outFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(outFormat, LOCALE);
        try {
            return dateFormat.format(new Date(timeStamp));
        } catch (Exception e) {
            MLog.e(e);
            return dateFormat.format(new Date());
        }
    }

    public static String formatString(String strDate, String inFormat, String outFormat) {
        SimpleDateFormat from = new SimpleDateFormat(inFormat, LOCALE);
        SimpleDateFormat to = new SimpleDateFormat(outFormat, LOCALE);
        try {
            return to.format(from.parse(strDate));
        } catch (Exception e) {
            MLog.e(e);
            return to.format(new Date());
        }
    }


    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    public static final long YEAR_MILLIS = 365 * DAY_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            MLog.e("getTimeAgo ERROR");
            return "";
        }
        long diff = now - time;
        if (diff < 5 * SECOND_MILLIS) {
            return "Just now";
        } else if (diff < MINUTE_MILLIS) {
            return diff / SECOND_MILLIS + " second ago";
        } else if (diff < HOUR_MILLIS) {
            return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < DAY_MILLIS) {
            return diff / HOUR_MILLIS + " hour ago";
        } else if (diff < MONTH_MILLIS) {
            return diff / DAY_MILLIS + " day ago";
        } else if (diff < YEAR_MILLIS) {
            return diff / MONTH_MILLIS + " month ago";
        } else if (diff > YEAR_MILLIS) {
            return diff / YEAR_MILLIS + " year ago";
        }
        return "error";
    }

    public static String getTimeAgoVN(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            MLog.e("getTimeAgo ERROR");
            return "";
        }
        long diff = now - time;
        if (diff < 5 * SECOND_MILLIS) {
            return "vừa xong";
        } else if (diff < MINUTE_MILLIS) {
            return diff / SECOND_MILLIS + " giây";
        } else if (diff < HOUR_MILLIS) {
            return diff / MINUTE_MILLIS + " phút";
        } else if (diff < DAY_MILLIS) {
            return diff / HOUR_MILLIS + " giờ";
        } else if (diff < MONTH_MILLIS) {
            return diff / DAY_MILLIS + " ngày";
        } else if (diff < YEAR_MILLIS) {
            return diff / MONTH_MILLIS + " tháng";
        } else if (diff > YEAR_MILLIS) {
            return diff / YEAR_MILLIS + " năm";
        }
        return "error";
    }

    private static final String[] DAY_EN = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static final String[] DAY_VN = new String[]{"Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};

    public static String diffDay(long start, long end) {
        try {
            Calendar cStart = Calendar.getInstance();
            cStart.setTimeInMillis(start);
            cStart.set(Calendar.HOUR_OF_DAY, 0);
            cStart.set(Calendar.MINUTE, 0);
            cStart.set(Calendar.SECOND, 0);

            Calendar cEnd = Calendar.getInstance();
            cEnd.setTimeInMillis(end);
            cEnd.set(Calendar.HOUR_OF_DAY, 0);
            cEnd.set(Calendar.MINUTE, 0);
            cEnd.set(Calendar.SECOND, 0);

            long diff = Math.abs(cStart.getTimeInMillis() - cEnd.getTimeInMillis());
            return String.valueOf((diff / DAY_MILLIS) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public enum LANGUAGE {
        VN,
        EN
    }

    public static String getNameDay(long time, LANGUAGE language) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int pos = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        if (language == LANGUAGE.VN) {
            return DAY_VN[pos];

        } else if (language == LANGUAGE.EN) {
            return DAY_EN[pos];

        } else {
            return DAY_EN[pos];
        }
    }

    public static String getTodayYesterday(long timeIn) {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timeIn);
        time.get(Calendar.DAY_OF_YEAR);

        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == time.get(Calendar.YEAR)) {
            int diffDay = now.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return "Today";
            }
            if (diffDay == 1) {
                return "Yesterday";
            }
            if (diffDay == -1) {
                return "Tomorrow";
            }
        }
        MLog.e("getTodayYesterday ERROR ");
        return getNameDay(timeIn, LANGUAGE.EN);
    }

    public static String getTime(long time) {
        long m = time / 60;
        long s = time % 60;
        return (m < 10 ? ("0" + m) : m) + ":"
                + (s < 10 ? ("0" + s) : s);
    }

    public static String getTimeMinus(long time) {
        long h = time / 3600;
        long m = (time % 3600) / 60;
        long s = time % 60;
        return (h == 0 ? "" : ((h < 10 ? ("0" + h) : h) + ":"))
                + (m < 10 ? ("0" + m) : m) + ":"
                + (s < 10 ? ("0" + s) : s);
    }


    public static String getToDay() {
        long time = Calendar.getInstance().getTimeInMillis();
        return formatLong(time, "dd/MM/yyyy");
    }

    public interface IOnTimeSever {
        void onTimeSeverProgress();

        void onTimeSeverSuccess(long time);

        void onTimeSeverError();

    }

    public static void getTimeSever(IOnTimeSever iOnTimeSever) {
        iOnTimeSever.onTimeSeverProgress();
//
//        String url = "https://kanoteam.com/tools/timestemp.php";
//        HashMap<String, String> param = new HashMap<>();
//        BaseDataManager.requestServer(url, param, BaseDataManager.POST, response -> {
//            try {
//                switch (response.status) {
//                    case success:
//                        JSONObject object = new JSONObject(response.json);
//                        long timeNow = object.getLong("timestamp");
//                        iOnTimeSever.onTimeSeverSuccess(timeNow);
//                        break;
//                    case error:
//                        iOnTimeSever.onTimeSeverError();
//                        break;
//                    case cancel:
//                        iOnTimeSever.onTimeSeverError();
//                        break;
//                }
//            } catch (Exception e) {
//                try {
//                    iOnTimeSever.onTimeSeverError();
//                } catch (Exception e1) {
//                    MLog.e(e1);
//                }
//                MLog.e(e);
//            }
//        });

//        Call<TimeResponse> call = BaseRetrofit.createService(Api.class).getTime();
//        call.enqueue(new Callback<TimeResponse>() {
//            @Override
//            public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
//                try {
//                    if (response.isSuccessful() && response.body() != null) {
//                        TimeResponse timeResponse = response.body();
//                        String dateString = timeResponse.getFormatted();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//                        Date date = sdf.parse(dateString);
//                        long timeNow = date.getTime() - 7 * DateTimeUtils.HOUR_MILLIS;
//                        iOnTimeSever.onTimeSeverSuccess(timeNow);
//                        return;
//                    }
//
//                } catch (Exception e) {
//                    MLog.e(e);
//                }
//                iOnTimeSever.onTimeSeverError();
//            }
//
//            @Override
//            public void onFailure(Call<TimeResponse> call, Throwable t) {
//                try {
//                    iOnTimeSever.onTimeSeverError();
//                } catch (Exception e) {
//                    MLog.e(e);
//                }
//            }
//        });
    }
}

package com.kano.auto.utils;

import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {


    public static ArrayList<String> regex(String str, String regex, int subStart, int subEnd) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        ArrayList<String> arrData = new ArrayList<>();

        while (matcher.find()) {

            int start = matcher.start();
            int end = matcher.end();

            if (subStart > 0) {
                start += subStart;
            }
            if (subStart > 0) {
                end -= subEnd;
            }

            arrData.add(str.substring(start, end));
        }

        return arrData;
    }

    public static ArrayList<String> regex(String str, String regex) {
        return regex(str, regex, -1, -1);
    }


    public static String encode2shit(String str) {
        try {
            StringBuilder key = new StringBuilder();
            char[] arr = str.toCharArray();
            for (int i : arr) {
                String shit2 = Integer.toBinaryString(i);
                key.append("3").append(shit2);
            }
            return key.toString().substring(1);
        } catch (Exception e) {
            MLog.e(e);
        }
        return "";
    }


    public static String decode2shit(String binaryCode) {
        try {
            String[] code = binaryCode.split("3");
            String word = "";
            for (int i = 0; i < code.length; i++) {
                word += (char) Integer.parseInt(code[i], 2);
            }
            return word;
        } catch (Exception e) {
            MLog.e(e);
        }
        return "";
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isGoodField(String input) {
        if (input == null || input.isEmpty() || input.trim().length() < 6) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String getAction(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }

    public static boolean isValidateUsername(String userName) {
        String USERNAME_PATTERN = "^[A-Za-z0-9_-]{1,33}$";
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }

    /**
     * @param input
     * @return chuỗi ko có dấu, chữ tiếng việt
     */
    public static String removeAccent(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toUpperCase().replaceAll("Đ", "D");
    }


    public static String formatPrice(long price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(price);
    }

    public static String formatPrice(String price) {
        try {
            DecimalFormat formatter = new DecimalFormat("###,###,###");
            return formatter.format(Long.valueOf(price));
        } catch (NumberFormatException e) {
            MLog.e(e);
        }
        return price;
    }

    public static String formatView(long view) {
        try {
            if (view <= 1000) {
                return (String.valueOf(view));
            }
            if (view <= 10000) {
                long vi = (view % 1000) / 100;
                return (view / 1000 + (vi == 0 ? "" : ("." + vi)) + "K");
            }
            if (view <= 1000000) {
                return (view / 1000 + "K");
            }
            if (view <= 1000000000) {
                long vi = (view % 1000000) / 100000;
                return (view / 1000000 + (vi == 0 ? "" : ("." + vi)) + "M");
            }
        } catch (Exception e) {
            MLog.e(e);
        }
        return String.valueOf(view);
    }

    public static String formatView(String viewStr) {
        try {
            long view = Long.valueOf(viewStr);
            return formatView(view);
        } catch (Exception e) {
//            MLog.e(e);
        }
        return String.valueOf(viewStr);
    }

    @Deprecated
    public static void setHtml(TextView textView, String html) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
            } else {
                textView.setText(Html.fromHtml(html));
            }
        } catch (Exception e) {
            MLog.e(e);
        }

    }

    public static String formatFile(long size) {
        try {
            if (size <= 0) {
                return "0";
            }
            String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        } catch (Exception e) {
            MLog.e(e);
        }
        return "0";
    }

    public static String formatKm(double km) {
        if (km < 1) {
            return ((int) (km * 1000)) + " m";
        }
        NumberFormat formatter = new DecimalFormat("#0.0");
        return formatter.format(km) + " km";
    }

    public static String formatMet(double met) {
        if (met >= 1000) {
            NumberFormat formatter = new DecimalFormat("#0.0");
            return (formatter.format((double) (met / 1000))) + " km";
        }
        NumberFormat formatter = new DecimalFormat("#0");
        return formatter.format(met) + " m";
    }

    public static StringBuffer removeUTFCharacters(String data) {
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        return buf;
    }

    public static double roundToHalf(float d) {
        return Math.round(d * 2) / 2.0;
    }


    public static String encodeUrl(String link) {
        String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        return Uri.encode(link, ALLOWED_URI_CHARS);
    }


    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

    //Method to remove the html tags contained in a String variable
    public static String removeTag(String string) {
        //validate that at least one value contains the string
        if (string == null || string.length() == 0) {
            return string;
        }
        //Function to find the matches within the chain and the pattern
        Matcher m = REMOVE_TAGS.matcher(string);
        //replace <> element with ""
        return m.replaceAll("");
    }
}

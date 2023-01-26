package antifraud.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String IPV4_PATTERN =
            "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
    private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);
    public static void checkIpFormat(String ip) {
        Matcher matcher = pattern.matcher(ip);
        if (!matcher.matches()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public  static void checkCardNumber(String number){
        int nDigits = number.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {
            int d = number.charAt(i) - '0';
            if (isSecond == true)
                d = d * 2;

            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
      if (nSum % 10 != 0){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
    }

}

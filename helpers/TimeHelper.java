package helpers;

import pojos.FixedWindow;
import pojos.TokenBucket;

public class TimeHelper {


    public static int findFloorValue(FixedWindow fixedWindow) {
        switch (fixedWindow.getTimeUnit()) {
            case SECONDS: return fixedWindow.getNumber();
            case MINUTES: return fixedWindow.getNumber()*60;
            case HOURS: return fixedWindow.getNumber()*60*60;
        }
        return 0;
    }
}

package impl;

import interfaces.RateLimiter;
import pojos.FixedWindow;
import pojos.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In a fixed window rate limiter, there will be a limit on no of requests per time frame.
 * Let's say 100 req/min, then we divide the time into fixed windows
 * for example, 10:00:27 will be in window 10:00:00(floor value) and what ever requests comes at that time
 * we assign it to 10:00:00 window.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private ConcurrentHashMap<String, Integer> noOfRequests = new ConcurrentHashMap<>();
    private Map<String, Object> lockMap = new HashMap<>();
    private Integer THRESHOLD;
    private FixedWindow fixedWindow;

    public FixedWindowRateLimiter(FixedWindow fixedWindow, Integer THRESHOLD) {
        this.fixedWindow = fixedWindow;
        this.THRESHOLD = THRESHOLD;
    }

    private static long floorSeconds(long currentTimeMillis, int customSeconds) {
        return currentTimeMillis - (currentTimeMillis % (customSeconds*1000));
    }

    @Override
    public boolean allowRequest(Request request) {
        long seconds = floorSeconds(request.getTimestamp(), findFloorValue());
        String key = request.getUserId() + ":" + seconds;
        lockMap.putIfAbsent(key, new Object());
        synchronized (lockMap.get(key)) {
            Integer presentRequests = noOfRequests.getOrDefault(key, 0);
            if (presentRequests >= THRESHOLD) {
                return false;
            }
            noOfRequests.put(key, presentRequests+1);
            return true;
        }
    }

    private int findFloorValue() {
        switch (fixedWindow.getTimeUnit()) {
            case SECONDS: return fixedWindow.getNumber();
            case MINUTES: return fixedWindow.getNumber()*60;
            case HOURS: return fixedWindow.getNumber()*60*60;
        }
        return 0;
    }

}

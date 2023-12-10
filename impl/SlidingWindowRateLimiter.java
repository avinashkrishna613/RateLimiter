package impl;

import interfaces.RateLimiter;
import pojos.FixedWindow;
import pojos.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In a sliding window, the window will move forward based on the request timings.
 * Ex: if a request comes at 9:55:55 and if we are supporting window is of 100 req/min, then we o back to
 * 9:54:55 and check within this range, like how many requests have come for the given key.
 * If they exceed the limit we discard the request or we allow the request.
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    private FixedWindow fixedWindow;
    private Integer THRESHOLD;
    private final ConcurrentHashMap<String, Map<Long, Integer>> userIdToRequestTimeToCountMap;
    private final ConcurrentHashMap<String, Object> lockMap;
    public SlidingWindowRateLimiter(FixedWindow fixedWindow, Integer threshold) {
        this.fixedWindow = fixedWindow;
        this.THRESHOLD = threshold;
        this.userIdToRequestTimeToCountMap = new ConcurrentHashMap<>();
        this.lockMap  = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean allowRequest(Request request) {
        long timestamp = request.getTimestamp();
        lockMap.computeIfAbsent(request.getUserId(), k -> new Object());
//        lockMap.putIfAbsent(request.getUserId(), new Object());
        synchronized (lockMap.get(request.getUserId())) {
            System.out.println("Thread : " + Thread.currentThread().getName() + " aquired lock at " + System.currentTimeMillis());
            // find the start window for the current request
            long value = findFloorValue()*1000;
            long startWindow = timestamp - value;

            // for the request user id how many requests reside from startWindow to timeStamp
            Map<Long, Integer> timeStampToReqCount = userIdToRequestTimeToCountMap.getOrDefault(request.getUserId(), new ConcurrentHashMap<>());
            if (timeStampToReqCount.size() > 0) {
                Long windowCount = timeStampToReqCount.keySet().stream().filter(stamp -> stamp >= startWindow).reduce(0L, (a, b) -> a + timeStampToReqCount.get(b));
                if (windowCount < THRESHOLD) {
                    Integer currentTimeStampCount = timeStampToReqCount.getOrDefault(timestamp, 0);
                    timeStampToReqCount.put(timestamp, currentTimeStampCount+1);
                    userIdToRequestTimeToCountMap.put(request.getUserId(), timeStampToReqCount);
                    return true;
                }
            } else {
                timeStampToReqCount.put(timestamp, 1);
                userIdToRequestTimeToCountMap.put(request.getUserId(), timeStampToReqCount);
                return true;
            }
        }
        return false;
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

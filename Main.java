import impl.FixedWindowRateLimiter;
import impl.SlidingWindowRateLimiter;
import interfaces.RateLimiter;
import pojos.FixedWindow;
import pojos.Request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        FixedWindow fixedWindow = new FixedWindow(5, TimeUnit.SECONDS);
        final Integer THRESHOLD = 4;
        RateLimiter rateLimiter = new SlidingWindowRateLimiter(fixedWindow, THRESHOLD);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                Request request = new Request("8abcded");
                System.out.println(request + " " + rateLimiter.allowRequest(request));
            });
        }
        executorService.shutdown();
    }
}

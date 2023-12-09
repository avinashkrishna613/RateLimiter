import impl.FixedWindowRateLimiter;
import interfaces.RateLimiter;
import pojos.FixedWindow;
import pojos.Request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        FixedWindow fixedWindow = new FixedWindow(5, TimeUnit.SECONDS);
        final Integer THRESHOLD = 5;
        RateLimiter rateLimiter = new FixedWindowRateLimiter(fixedWindow, THRESHOLD);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Request request = new Request("8abcded");
                        System.out.println(rateLimiter.allowRequest(request));
                    });
        }
    }
}

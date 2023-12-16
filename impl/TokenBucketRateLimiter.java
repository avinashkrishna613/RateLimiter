package impl;

import interfaces.RateLimiter;
import pojos.FixedWindow;
import pojos.Request;
import pojos.TokenBucket;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static helpers.TimeHelper.findFloorValue;

/**
 * Say we have a bucket whose capacity is defined as the number of tokens that it can hold. Whenever a consumer wants to access an API endpoint, it must get a token from the bucket. We remove a token from the bucket
 * if it’s available and accept the request. Conversely, we reject a request if the bucket doesn’t have any tokens.
 * As requests are consuming tokens, we’re also replenishing them at some fixed rate,
 * such that we never exceed the capacity of the bucket.
 * Let’s consider an API that has a rate limit of 100 requests per minute.
 * We can create a bucket with a capacity of 100, and a refill rate of 100 tokens per minute.
 * If we receive 70 requests, which is fewer than the available tokens in a given minute,
 * we would add only 30 more tokens at the start of the next minute to bring the bucket up to capacity.
 * On the other hand, if we exhaust all the tokens in 40 seconds, we would wait for 20 seconds to refill the bucket.
 */
// https://www.baeldung.com/spring-bucket4j
public class TokenBucketRateLimiter implements RateLimiter {

    private final ReentrantLock reentrantLock = new ReentrantLock();
    // usually in token bucket, the bucket is per user/ip address
    private final ConcurrentHashMap<String, TokenBucket> mapUserToBucket =
            new ConcurrentHashMap<>();
    private final FixedWindow fixedWindow;
    private final int requestLimit;
    private static final ExecutorService executorservice = Executors.newCachedThreadPool();

    public TokenBucketRateLimiter(FixedWindow fixedWindow, int requestLimit) {
        this.fixedWindow = fixedWindow;
        this.requestLimit = requestLimit;
        // we need to schedule in such a way, that when a user bucket lastfilledtime and currentime difference is window time
        // then we need to refill the bucket with tokens
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("Executing schedule thread.");
               mapUserToBucket.values().stream().filter(bucket -> (System.currentTimeMillis() - bucket.getLastFilledAt() >= findFloorValue(bucket.getFixedWindow())*1000) && bucket.getTokens() < requestLimit
               ).forEach(tokenBucket -> {
                   executorservice.submit(() -> {
                       tokenBucket.resetTokensToThreshold();
                   });
               });
            executorservice.shutdown();
        }, fixedWindow.getNumber(), fixedWindow.getNumber(), fixedWindow.getTimeUnit());
    }

    @Override
    public boolean allowRequest(Request request) {
        // we need a thread which receives the request and then check if we can accept or not
        // if we have enough tokens we accept
        // if we don't have enough tokens we don't accept.
        reentrantLock.lock();
        String userId = request.getUserId();
        TokenBucket tokenBucket = mapUserToBucket.getOrDefault(userId, new TokenBucket(this.requestLimit, this.fixedWindow, this.requestLimit));
        mapUserToBucket.put(userId, tokenBucket);
        int tokens = tokenBucket.getTokens();
        if (tokens > 0) {
            tokenBucket.setTokens(tokens-1);
            reentrantLock.unlock();
            return true;
        }
        reentrantLock.unlock();
        return false;
    }
}
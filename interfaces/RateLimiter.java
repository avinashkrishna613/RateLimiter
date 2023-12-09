package interfaces;

import pojos.Request;

@FunctionalInterface
public interface RateLimiter {
    boolean allowRequest(Request request);
}

package pojos;

public class TokenBucket {
    private int tokens;
    private FixedWindow fixedWindow;
    private int THRESHOLD;
    private long lastFilledAt;

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public FixedWindow getFixedWindow() {
        return fixedWindow;
    }

    public void setFixedWindow(FixedWindow fixedWindow) {
        this.fixedWindow = fixedWindow;
    }

    public int getTHRESHOLD() {
        return THRESHOLD;
    }

    public void setTHRESHOLD(int THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
    }

    public TokenBucket(int tokens, FixedWindow fixedWindow, int THRESHOLD) {
        this.tokens = tokens;
        this.fixedWindow = fixedWindow;
        this.THRESHOLD = THRESHOLD;
        this.lastFilledAt = System.currentTimeMillis();
    }

    public long getLastFilledAt() {
        return lastFilledAt;
    }

    public void setLastFilledAt(long lastFilledAt) {
        this.lastFilledAt = lastFilledAt;
    }

    public void resetTokensToThreshold() {
        System.out.println("REFILL CALLED AT : " + System.currentTimeMillis());
        tokens = THRESHOLD;
        this.lastFilledAt = System.currentTimeMillis();
    }
}

package pojos;

public class TokenBucket {
    private int tokens;
    private FixedWindow fixedWindow;
    private int THRESHOLD;

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
    }

    public void resetTokensToThreshold() {
        System.out.println("REFILL CALLED AT : " + System.currentTimeMillis());
        tokens = THRESHOLD;
    }
}

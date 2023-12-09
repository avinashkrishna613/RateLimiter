package pojos;

import java.util.concurrent.TimeUnit;

public class FixedWindow {
    private int number;
    private TimeUnit timeUnit;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public FixedWindow(int number, TimeUnit timeUnit) {
        this.number = number;
        this.timeUnit = timeUnit;
    }
}

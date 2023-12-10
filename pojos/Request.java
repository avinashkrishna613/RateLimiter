package pojos;

public class Request {
    private String userId;
    private String ip;
    private String location;
    private long timestamp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Request(String userId) {
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Request{" +
                "userId='" + userId + '\'' +
                ", ip='" + ip + '\'' +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

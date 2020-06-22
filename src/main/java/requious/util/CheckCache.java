package requious.util;

public class CheckCache {
    boolean result;
    long checkTime;

    public CheckCache() {
    }

    public CheckCache(boolean result, long checkTime) {
        this.result = result;
        this.checkTime = checkTime;
    }

    public void setResult(boolean result, long time) {
        this.result = result;
        this.checkTime = time;
    }

    public boolean getResult() {
        return result;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public long getTicksSinceLastCheck(long time) {
        return time - checkTime;
    }
}

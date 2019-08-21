package requious.util;

public class IOParameters {
    public int slot;
    public int size;
    public boolean active;

    public IOParameters(int size, int slot) {
        this.slot = slot;
        this.size = size;
        this.active = true;
    }

    public IOParameters(int size) {
        this.slot = -1;
        this.size = size;
        this.active = true;
    }

    public IOParameters() {
        this.active = false;
    }
}

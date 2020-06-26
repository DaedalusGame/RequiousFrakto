package requious.util;

public class Fill {
    double amount;
    double capacity;

    public Fill(float amount, float capacity) {
        this.amount = amount;
        this.capacity = capacity;
    }

    public double getRatio(boolean inverse) {
        if(capacity <= 0)
            return 0;
        if(amount >= capacity)
            return 1;
        if(amount <= 0)
            return 0;
        double ratio = amount / capacity;
        if (inverse)
            ratio = 1 - ratio;
        return ratio;
    }

    public int getFill(int size, boolean inverse) {
        int fill = (int) (getRatio(inverse) * size);
        if (amount > 0 && fill <= 0)
            fill = 1;
        return fill;
    }
}

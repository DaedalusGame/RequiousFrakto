package requious.util;

public class RatioConversion implements IConversion {
    int valueOfBase;
    int valueOfUnit;

    public RatioConversion(int valueOfBase, int valueOfUnit) {
        this.valueOfBase = valueOfBase;
        this.valueOfUnit = valueOfUnit;
    }

    @Override
    public int getUnit(int base) {
        return (base * valueOfUnit) / valueOfBase;
    }

    @Override
    public int getBase(int unit) {
        return (unit * valueOfBase) / valueOfUnit;
    }
}

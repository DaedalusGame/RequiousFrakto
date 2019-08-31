package requious.util.battery;

import net.minecraft.item.ItemStack;

public class BatteryAccessEmpty implements IBatteryAccess {
    public static final IBatteryAccess INSTANCE = new BatteryAccessEmpty();

    private BatteryAccessEmpty() {
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }
}

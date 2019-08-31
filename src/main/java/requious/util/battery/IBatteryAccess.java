package requious.util.battery;

import net.minecraft.item.ItemStack;

public interface IBatteryAccess {
    int getMaxEnergyStored();

    int getEnergyStored();

    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    ItemStack getStack();
}

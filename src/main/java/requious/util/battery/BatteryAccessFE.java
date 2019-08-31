package requious.util.battery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class BatteryAccessFE implements IBatteryAccess {
    ItemStack battery;
    IEnergyStorage storage;

    public BatteryAccessFE(ItemStack battery, IEnergyStorage storage) {
        this.battery = battery;
        this.storage = storage;
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive,simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract,simulate);
    }

    @Override
    public ItemStack getStack() {
        return battery;
    }
}

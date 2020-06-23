package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import requious.data.AssemblyProcessor;
import requious.tile.TileEntityAssembly;
import requious.util.ComponentFace;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.Slot")
public abstract class ComponentBase {
    public ComponentFace face;
    public HashSet<String> groups = new HashSet<>();
    public int x,y;
    public boolean hidden;

    public ComponentBase(ComponentFace face) {
        this.face = face;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract Slot createSlot();

    @ReturnsSelf
    @ZenMethod
    public ComponentBase setGroup(String group) {
        groups.add(group);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentBase setHidden() {
        hidden = true;
        return this;
    }


    public static abstract class Slot<T extends ComponentBase> implements INBTSerializable<NBTTagCompound> {
        protected final T component;
        private boolean dirty;

        public Slot(T component) {
            this.component = component;
        }

        public ComponentFace getFace() {
            return component.face;
        }

        public abstract void addCollectors(List<Collector> collectors);

        public abstract net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y);

        public abstract void update();

        public abstract void machineBroken(World world, Vec3d position);

        public boolean isGroup(String group) {
            if(group == null)
                return true;
            return component.groups.contains(group);
        }

        public boolean isHidden() {
            return component.hidden;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void markDirty() {
            dirty = true;
        }

        public void markClean() { dirty = false; }

        public boolean canShift() {
            return false;
        }
    }

    public static abstract class Collector {
        TileEntity tile;

        public abstract boolean accept(Slot slot);

        public boolean hasCapability() {
            return false;
        }

        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            return false;
        }

        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            return null;
        }

        public abstract void update();

        @Override
        public boolean equals(Object obj) {
            return obj.getClass().equals(getClass());
        }

        public void setTile(TileEntity tile) {
            this.tile = tile;
        }

        public TileEntity getTile() {
            return tile;
        }

        public EnumFacing getTileFacing() {
            if(tile instanceof TileEntityAssembly) //TODO: better
                return ((TileEntityAssembly) tile).getFacing();
            return EnumFacing.UP;
        }
    }
}

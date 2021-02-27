package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import requious.data.AssemblyProcessor;
import requious.util.ComponentFace;
import requious.util.IngredientAny;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.MechanicalSlot")
public class ComponentMechanical extends ComponentBase {
    boolean attachGear;
    Ingredient gearFilter = new IngredientAny();

    public ComponentMechanical(ComponentFace face) {
        super(face);
    }

    @ZenMethod
    public ComponentMechanical setAttachGear(IIngredient filter) {
        attachGear = true;
        gearFilter = CraftTweakerMC.getIngredient(filter);
        return this;
    }

    @Override
    public Slot createSlot() {
        return new Slot(this);
    }

    public static class Slot extends ComponentBase.Slot<ComponentMechanical> {
        public Slot(ComponentMechanical component) {
            super(component);
        }

        public boolean canAttachGear() {
            return false;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            ComponentMechanical.Collector mechanical = new ComponentMechanical.Collector(getFace());

            if (!collectors.contains(mechanical))
                collectors.add(mechanical);
        }

        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return null;
        }

        @Override
        public void update() {

        }

        @Override
        public void machineBroken(World world, Vec3d position) {

        }

        @Override
        public NBTTagCompound serializeNBT() {
            return null;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {

        }
    }

    public static class Collector extends ComponentBase.Collector {
        ComponentFace face;
        List<ComponentMechanical.Slot> slots = new ArrayList<>();

        public Collector(ComponentFace face) {
            this.face = face;
        }

        public ComponentFace getFace() {
            return face;
        }

        private void addSlot(ComponentMechanical.Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot.getFace() == face && slot instanceof ComponentMechanical.Slot) {
                addSlot((ComponentMechanical.Slot) slot);
                return true;
            }
            return false;
        }

        @Override
        public boolean hasCapability() {
            return true;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            return super.hasCapability(capability, localSide, globalSide);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            return super.getCapability(capability, localSide, globalSide);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ComponentItem.Collector)
                return face.equals(((ComponentItem.Collector) obj).face);
            return false;
        }
    }
}

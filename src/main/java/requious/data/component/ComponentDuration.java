package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import requious.Requious;
import requious.compat.crafttweaker.GaugeDirectionCT;
import requious.gui.GaugeDirection;
import requious.gui.slot.DurationSlot;
import requious.gui.slot.EnergySlot;
import requious.recipe.AssemblyRecipe;
import requious.recipe.RequirementDuration;
import requious.util.ComponentFace;
import requious.util.ItemComponentHelper;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.DurationSlot")
public class ComponentDuration extends ComponentBase {
    public ResourceLocation tex = new ResourceLocation(Requious.MODID, "textures/gui/assembly_gauges.png");
    public int texX, texY;
    public GaugeDirection texDirection;

    public ComponentDuration() {
        super(ComponentFace.None);
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentDuration setTexture(int x, int y, GaugeDirectionCT direction) {
        texX = x;
        texY = y;
        texDirection = direction.get();
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentDuration setTexture(String resource, int x, int y, GaugeDirectionCT direction) {
        tex = new ResourceLocation(resource);
        setTexture(x,y,direction);
        return this;
    }

    public static class Slot extends ComponentBase.Slot<ComponentDuration> {
        RequirementDuration currentRecipe;
        int time, duration;
        boolean active;

        public Slot(ComponentDuration component) {
            super(component);
        }

        public RequirementDuration getCurrentRecipe() {
            return currentRecipe;
        }

        public void setCurrentRecipe(RequirementDuration recipe) {
            if (currentRecipe != null && currentRecipe != recipe) {
                time = 0;
            }
            active = true;
            currentRecipe = recipe;
            duration = recipe.getDuration();
        }

        public int getTime() {
            return time;
        }

        public int getDuration() {
            return duration;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            //NOOP
        }

        @Override
        public net.minecraft.inventory.Slot createGui(int x, int y) {
            return new DurationSlot(this, x, y);
        }

        @Override
        public void update() {
            if(active) {
                time++;
                if (time > duration)
                    time = duration;
            } else {
                reset();
            }
            active = false;
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            //NOOP
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("active",active);
            compound.setInteger("time", time);
            compound.setInteger("duration", duration);
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            active = compound.getBoolean("active");
            time = compound.getInteger("time");
            duration = compound.getInteger("duration");
        }

        public ResourceLocation getTexture() {
            return component.tex;
        }

        public int getTextureX() {
            return component.texX;
        }

        public int getTextureY() {
            return component.texY;
        }

        public GaugeDirection getTextureDirection() {
            return component.texDirection;
        }

        public void reset() {
            currentRecipe = null;
            time = 0;
            duration = 0;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
        }

        public boolean isDone() {
            return time >= duration;
        }
    }
}

package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.data.AssemblyProcessor;
import requious.gui.slot.DecorationSlot;
import requious.util.ComponentFace;
import requious.util.SlotVisual;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.List;

@ZenRegister
@ZenClass("mods.requious.DecorationSlot")
public class ComponentDecoration extends ComponentBase {
    SlotVisual visual;

    public ComponentDecoration(SlotVisual visual) {
        super(ComponentFace.None);
        this.visual = visual;
    }

    @Override
    public Slot createSlot() {
        return new Slot(this);
    }

    public static class Slot extends ComponentBase.Slot<ComponentDecoration> {
        public Slot(ComponentDecoration component) {
            super(component);
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            //NOOP
        }

        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new DecorationSlot(assembly,this,x,y);
        }

        @Override
        public void update() {
            //NOOP
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            //NOOP
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return new NBTTagCompound();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            //NOOP
        }

        public SlotVisual getVisual() {
            return component.visual;
        }
    }
}

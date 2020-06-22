package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.gui.slot.TextSlot;
import requious.util.ComponentFace;
import requious.util.SlotVisual;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.TextSlot")
public class ComponentText extends ComponentBase {
    public static class TextPart {
        String text;
        String[] variables;

        public TextPart(String text, String[] variables) {
            this.text = text;
            this.variables = variables;
        }

        public String getText() {
            return text;
        }

        public String[] getVariables() {
            return variables;
        }
    }

    TextPart renderText;
    String variable;
    List<TextPart> tooltip = new ArrayList<>();
    SlotVisual visual;

    public ComponentText() {
        super(ComponentFace.None);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentText setRenderText(String text, @Optional String[] variables) {
        if(variables == null)
            variables = new String[0];
        renderText = new TextPart(text,variables);
        return this;
    }


    @ReturnsSelf
    @ZenMethod
    public ComponentText addPart(String text, @Optional String[] variables) {
        if(variables == null)
            variables = new String[0];
        tooltip.add(new TextPart(text,variables));
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentText setForeground(SlotVisualCT visual) {
        this.visual = SlotVisualCT.unpack(visual);
        return this;
    }

    @Override
    public Slot createSlot() {
        return new Slot(this);
    }

    public static class Slot extends ComponentBase.Slot<ComponentText> {
        public Slot(ComponentText component) {
            super(component);
        }

        @Override
        public void addCollectors(List<Collector> collectors) {
            //NOOP
        }

        @Override
        public net.minecraft.inventory.Slot createGui(int x, int y) {
            return new TextSlot(this, x, y);
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

        public TextPart getRenderText() {
            return component.renderText;
        }

        public String getVariable() {
            return component.variable;
        }

        public boolean hasToolTip() {
            return !component.tooltip.isEmpty();
        }

        public Iterable<TextPart> getToolTip() {
            return component.tooltip;
        }
    }
}

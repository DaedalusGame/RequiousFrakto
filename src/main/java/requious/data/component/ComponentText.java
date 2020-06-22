package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.data.AssemblyProcessor;
import requious.gui.slot.TextSlot;
import requious.util.ComponentFace;
import requious.util.Fill;
import requious.util.SlotVisual;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.TextSlot")
public class ComponentText extends ComponentBase {
    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT,
    }

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
    String variableAmount, variableCapacity;
    List<TextPart> tooltip = new ArrayList<>();
    SlotVisual visual;
    Alignment alignment = Alignment.LEFT;

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
    public ComponentText setVisual(SlotVisualCT visual, @Optional String variableAmount, @Optional String variableCapacity) {
        this.visual = SlotVisualCT.unpack(visual);
        this.variableAmount = variableAmount;
        this.variableCapacity = variableCapacity;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentText alignLeft() {
        this.alignment = Alignment.LEFT;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentText alignCenter() {
        this.alignment = Alignment.CENTER;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentText alignRight() {
        this.alignment = Alignment.RIGHT;
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
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new TextSlot(assembly,this, x, y);
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

        public Alignment getAlignment() {
            return component.alignment;
        }

        public Fill getFill(AssemblyProcessor assembly) {
            Object amount = assembly.getVariable(component.variableAmount);
            Object capacity = assembly.getVariable(component.variableCapacity);

            if (amount instanceof Number && capacity instanceof Number)
                return new Fill(((Number) amount).floatValue(), ((Number) capacity).floatValue());
            if (capacity instanceof Number)
                return new Fill(0, ((Number) capacity).floatValue());
            else
                return new Fill(0, 0);
        }

        public boolean hasToolTip() {
            return !component.tooltip.isEmpty();
        }

        public Iterable<TextPart> getToolTip() {
            return component.tooltip;
        }
    }
}

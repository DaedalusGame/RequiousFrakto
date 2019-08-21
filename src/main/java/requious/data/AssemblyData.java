package requious.data;

import com.google.gson.annotations.Expose;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.item.ItemStack;
import requious.Registry;
import requious.block.BlockAssembly;
import requious.compat.crafttweaker.ComponentFaceCT;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.compat.jei.JEISlot;
import requious.compat.jei.slot.*;
import requious.data.component.*;
import requious.recipe.AssemblyRecipe;
import requious.util.PlaceType;
import requious.util.SlotVisual;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.requious.Assembly")
public class AssemblyData extends BaseData {
    @Expose(serialize = false, deserialize = false)
    public transient ComponentBase[][] slots = new ComponentBase[9][5];
    @Expose(serialize = false, deserialize = false)
    public transient Map<String,List<AssemblyRecipe>> recipes = new HashMap<>();

    public PlaceType placeType = PlaceType.Any;
    public boolean hasGUI = true;

    @Expose(serialize = false, deserialize = false)
    public transient List<JEISlot> jeiSlots = new ArrayList<>();
    @Expose(serialize = false, deserialize = false)
    public transient List<AssemblyRecipe> jeiRecipes = new ArrayList<>();
    @Expose(serialize = false, deserialize = false)
    public transient List<ItemStack> jeiCatalysts = new ArrayList<>();
    @Expose(serialize = false, deserialize = false)
    private transient BlockAssembly block;

    public AssemblyProcessor constructProcessor() {
        AssemblyProcessor processor = new AssemblyProcessor(this);
        processor.setComponent(slots);
        processor.setup();
        return processor;
    }

    public void setBlock(BlockAssembly block) {
        this.block = block;
    }

    public BlockAssembly getBlock() {
        return block;
    }

    @ZenMethod
    public static AssemblyData get(String identifier) {
        return Registry.getAssemblyData(identifier);
    }

    private void setSlot(int x, int y, ComponentBase component) {
        slots[x][y] = component;
        component.setPosition(x,y);
    }

    private void setJEISlot(JEISlot slot) {
        jeiSlots.removeIf(oldSlot -> slot.x == oldSlot.x && slot.y == oldSlot.y);
        jeiSlots.add(slot);
    }

    public int getJEIWidth() {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (JEISlot slot : jeiSlots) {
            min = Math.min(min,slot.x);
            max = Math.max(max,slot.x+1);
        }
        return max;
    }

    public int getJEIHeight() {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (JEISlot slot : jeiSlots) {
            min = Math.min(min,slot.y);
            max = Math.max(max,slot.y+1);
        }
        return max;
    }

    @ZenMethod
    public ComponentItem setItemSlot(int x, int y, ComponentFaceCT face, int capacity) {
        ComponentItem component = new ComponentItem(face.get(),capacity);
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public ComponentFluid setFluidSlot(int x, int y, ComponentFaceCT face, int capacity) {
        ComponentFluid component = new ComponentFluid(face.get(),capacity);
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public ComponentEnergy setEnergySlot(int x, int y, ComponentFaceCT face, int capacity) {
        ComponentEnergy component = new ComponentEnergy(face.get(),capacity);
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public ComponentLaser setLaserSlot(int x, int y, ComponentFaceCT face) {
        ComponentLaser component = new ComponentLaser(face.get());
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public ComponentSelection setSelectionSlot(int x, int y, String selectionGroup, int index) {
        ComponentSelection component = new ComponentSelection(selectionGroup,index);
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public ComponentDuration setDurationSlot(int x, int y) {
        ComponentDuration component = new ComponentDuration();
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public ComponentAutomata setAutomataSlot(int x, int y, ComponentFaceCT face, int capacity) {
        ComponentAutomata component = new ComponentAutomata(face.get(),capacity);
        setSlot(x,y,component);
        return component;
    }

    @ZenMethod
    public void addRecipe(AssemblyRecipe recipe) {
        recipes.computeIfAbsent(recipe.processGroup,k -> new ArrayList<>()).add(recipe);
    }

    @ZenMethod
    public void setJEIItemSlot(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new ItemSlot(x,y,group,SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIFluidSlot(int x, int y, String group) {
        setJEISlot(new FluidSlot(x,y,group));
    }

    @ZenMethod
    public void setJEIEnergySlot(int x, int y, String group, @Optional String unit) {
        if(unit == null)
            unit = "none";
        setJEISlot(new EnergySlot(x,y,group,unit));
    }

    @ZenMethod
    public void setJEILaserSlot(int x, int y, String group) {
        setJEISlot(new LaserSlot(x,y,group));
    }

    @ZenMethod
    public void setJEISelectionSlot(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new SelectionSlot(x,y,group,SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIDecoration(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new DecorationSlot(x,y,group,SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIDurationSlot(int x, int y, String group, SlotVisualCT visual) {
        setJEISlot(new DurationSlot(x,y,group,SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void addJEIRecipe(AssemblyRecipe recipe) {
        if(recipe.hasJEICategory()) {
            CraftTweakerAPI.logError("Recipe already has a JEI category.");
            return;
        }
        jeiRecipes.add(recipe);
        recipe.setJEICategory(this);
    }

    @ZenMethod
    public void addJEICatalyst(ItemStack catalyst) {
        jeiCatalysts.add(catalyst);
    }

    public Iterable<ItemStack> getJEICatalysts() {
        return jeiCatalysts;
    }

    public boolean hasJEIRecipes() {
        return !jeiRecipes.isEmpty();
    }
}

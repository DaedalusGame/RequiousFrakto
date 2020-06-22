package requious.recipe;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;
import requious.data.AssemblyData;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@ZenRegister
@ZenClass("mods.requious.AssemblyRecipe")
public class AssemblyRecipe implements IRecipeWrapper {
    List<RequirementBase> requirements = new ArrayList<>();
    IRecipeFunction function;
    int duration;

    boolean jeiGenerated;
    AssemblyData jeiCategory;
    public List<JEISlot> jeiSlots = new ArrayList<>();
    public String processGroup = "";

    public AssemblyRecipe(IRecipeFunction function) {
        this.function = function;
    }

    @ZenMethod
    public static AssemblyRecipe create(IRecipeFunction function) {
        return new AssemblyRecipe(function);
    }

    @ZenMethod
    public AssemblyRecipe requireItem(String group, IIngredient ingredient) {
        requirements.add(new RequirementIngredient(group, ingredient));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireItem(String group, IIngredient ingredient, int min, int max) {
        requirements.add(new RequirementIngredient(group, ingredient, min, max));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireFluid(String group, ILiquidStack ingredient) {
        requirements.add(new RequirementFluid(group, ingredient));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireFluid(String group, ILiquidStack ingredient, int min, int max) {
        requirements.add(new RequirementFluid(group, ingredient, min, max));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireEnergy(String group, int energy, @Optional String mark) {
        requirements.add(new RequirementEnergy(group, energy, mark));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireEnergy(String group, int min, int max, @Optional String mark) {
        requirements.add(new RequirementEnergy(group, min, max, mark));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireLaser(String group, int energy, @Optional String mark, @Optional SlotVisualCT slotVisual) {
        requirements.add(new RequirementLaser(group, energy, mark, SlotVisualCT.unpack(slotVisual)));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireLaser(String group, String type, int energy, @Optional String mark, @Optional SlotVisualCT slotVisual) {
        requirements.add(new RequirementLaser(group, type, energy, mark, SlotVisualCT.unpack(slotVisual)));
        return this;
    }


    @ZenMethod
    public AssemblyRecipe requireSelection(String group, IItemStack stack, boolean reset) {
        requirements.add(new RequirementSelection(group, CraftTweakerMC.getItemStack(stack), reset));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireDuration(String group, int duration) {
        requirements.add(new RequirementDuration(group, duration));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireWorldCondition(String group, IWorldFunction function, int interval) {
        requirements.add(new RequirementWorld(group, function, interval));
        return this;
    }

    @ZenMethod
    public AssemblyRecipe requireRandomChance(String group, double chance, int interval) {
        requirements.add(new RequirementWorld(group, container -> doesRandomApply(container.tile.getWorld(),interval,chance), 0));
        return this;
    }

    private boolean doesRandomApply(World world, int interval, double chance) {
        long seed = hashCode() + world.getTotalWorldTime() % interval;
        return new Random(seed).nextDouble() > chance;
    }

    @ZenMethod
    public AssemblyRecipe addJEIInfo(String group, String langKey, SlotVisualCT slotVisual) {
        requirements.add(new RequirementJEI(group,langKey,SlotVisualCT.unpack(slotVisual)));
        return this;
    }


    @ZenMethod
    public AssemblyRecipe setSubProcess(String processGroup) {
        this.processGroup = processGroup;
        return this;
    }

    public  List<ConsumptionResult> matches(AssemblyProcessor assembly, RecipeContainer container) {
        List<ComponentBase.Slot> slots = assembly.getSlots();
        List<ConsumptionResult> results = new ArrayList<>();

        for(RequirementBase requirement : requirements) {
            ConsumptionResult result = requirement.createResult();
            results.add(result);
            boolean matched = false;
            MatchResult worldMatch = requirement.matches(assembly, result);
            if(worldMatch == MatchResult.MATCHED)
                matched = true;
            if(worldMatch == MatchResult.CANCEL)
                return null;
            if(!matched)
                for (int i = 0; i < slots.size(); i++) {
                    ComponentBase.Slot slot = slots.get(i);
                    MatchResult match = requirement.matches(slot, result);
                    if(match == MatchResult.MATCHED) {
                        matched = true;
                        result.setSlot(slot);
                        requirement.fillContainer(slot, result, container);
                        break;
                    } else if(match == MatchResult.CANCEL) {
                        break;
                    }
                }
            if(!matched)
                return null;
        }

        return results;
    }

    public boolean fitsResults(AssemblyProcessor assembly, RecipeContainer container) {
        List<ComponentBase.Slot> slots = assembly.getSlots();
        HashSet<Integer> blockedSlots = new HashSet<>(); //You can't insert into the same slot twice, it's illegal.
        for(ResultBase result : container.getResults()) {
            boolean matched = false;
            if(result.matches(assembly)) {
                matched = true;
            }
            if(!matched)
                for (int i = 0; i < slots.size(); i++) {
                    ComponentBase.Slot slot = slots.get(i);
                    if(blockedSlots.contains(i) || !result.matches(slot))
                        continue;
                    blockedSlots.add(i);
                    matched = true;
                }
            if(!matched)
                return false;
        }
        return true;
    }

    public void consumeRequirements(List<ConsumptionResult> results) {
        for (ConsumptionResult result : results) {
            result.consume();
        }
    }

    public void produceResults(AssemblyProcessor assembly, RecipeContainer container) {
        List<ComponentBase.Slot> slots = assembly.getSlots();
        for(ResultBase result : container.getResults()) {
            result.produce(assembly);
            for (ComponentBase.Slot slot : slots) {
                if (result.matches(slot)) {
                    result.produce(slot);
                    break;
                }
            }
        }
    }

    public void calculate(RecipeContainer container) {
        function.calculate(container);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        generateJEI();
        IngredientCollector collector = new IngredientCollector();
        for(JEISlot slot : jeiSlots) {
            slot.getIngredients(collector);
        }
        collector.collect(ingredients); //Possibly cache since it doesn't change ever.
    }

    public void setJEICategory(AssemblyData assembly) {
        jeiCategory = assembly;
    }

    public void generateJEI() {
        if(!jeiGenerated) {
            for (JEISlot slot : jeiCategory.jeiSlots) {
                jeiSlots.add(slot.copy());
            }
            for (RequirementBase requirement : requirements) {
                for (JEISlot slot : jeiSlots) {
                    boolean filled = requirement.fillJEI(slot);
                    if(filled)
                        break;
                }
            }
            RecipeContainer container = new RecipeContainer(true);
            calculate(container);
            for (ResultBase result : container.getResults()) {
                for (JEISlot slot : jeiSlots) {
                    boolean filled = result.fillJEI(slot);
                    if(filled)
                        break;
                }
            }
            jeiGenerated = true;
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        for (JEISlot slot : jeiSlots) {
            slot.render(minecraft,recipeWidth,recipeHeight,mouseX,mouseY);
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ITooltipFlag.TooltipFlags tooltipFlag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        List<String> tooltip = new ArrayList<>();
        for (JEISlot slot : jeiSlots) {
            if(mouseX >= slot.x*18 && mouseY >= slot.y*18 && mouseX < slot.x*18+18 && mouseY < slot.y*18+18)
                slot.getTooltip(tooltip,tooltipFlag);
        }
        return tooltip;
    }

    public boolean hasJEICategory() {
        return jeiCategory != null;
    }
}

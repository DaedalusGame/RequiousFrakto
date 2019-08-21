package requious.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IIngredientType;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class IngredientCollector {
    Map<IIngredientType, List> inputs = new IdentityHashMap<>();
    Map<IIngredientType, List> outputs = new IdentityHashMap<>();

    public <T> void addInput(IIngredientType<T> type, T ingredient) {
        List list = inputs.computeIfAbsent(type, value -> new ArrayList());
        list.add(ingredient);
    }

    public <T> void addOutput(IIngredientType<T> type, T ingredient) {
        List list = outputs.computeIfAbsent(type, value -> new ArrayList());
        list.add(ingredient);
    }

    public void collect(IIngredients ingredients) {
        for (IIngredientType key : inputs.keySet()) {
            ingredients.setInputs(key,inputs.get(key));
        }
        for (IIngredientType key : outputs.keySet()) {
            ingredients.setOutputs(key,outputs.get(key));
        }
    }
}

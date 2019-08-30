package requious.compat.jei.ingredient;

import requious.util.SlotVisual;

public class JEIInfo implements IFakeIngredient {
    public String langKey;
    public SlotVisual visual;

    public JEIInfo(String langKey, SlotVisual visual) {
        this.langKey = langKey;
        this.visual = visual;
    }

    @Override
    public String getDisplayName() {
        return "Info";
    }

    @Override
    public String getUniqueID() {
        return "info";
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

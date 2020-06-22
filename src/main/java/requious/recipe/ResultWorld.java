package requious.recipe;

import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.jei.JEISlot;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;

public class ResultWorld extends ResultBase {
    IWorldFunction worldCheck;

    public ResultWorld(IWorldFunction worldCheck) {
        super("world");
        this.worldCheck = worldCheck;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        //NOOP
    }

    @Override
    public boolean matches(AssemblyProcessor assembly) {
        return assembly.run(worldCheck);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}

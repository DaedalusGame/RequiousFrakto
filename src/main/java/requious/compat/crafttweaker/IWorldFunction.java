package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("requious.fluid.IWorldFunction")
@ZenRegister
public interface IWorldFunction {
    boolean run(MachineContainer machineContainer);
}

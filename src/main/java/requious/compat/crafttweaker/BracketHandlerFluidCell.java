package requious.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import requious.Registry;
import requious.data.AssemblyData;
import requious.data.FluidCellData;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

import java.util.List;

@BracketHandler
@ZenRegister
public class BracketHandlerFluidCell implements IBracketHandler {
    private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerFluidCell.class, "getFromString", String.class);

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if(tokens == null || tokens.size() < 3 || !tokens.get(0).getValue().equalsIgnoreCase("fluidCell"))
            return null;
        String name = tokens.get(2).getValue();
        return position -> new ExpressionCallStatic(position,environment,method, new ExpressionString(position, name));
    }

    public static FluidCellData getFromString(String name) {
        return Registry.getFluidCellData(name);
    }

    @Override
    public String getRegexMatchingString() {
        return "fluidCell:.*";
    }

    @Override
    public Class<?> getReturnedClass() {
        return FluidCellData.class;
    }
}

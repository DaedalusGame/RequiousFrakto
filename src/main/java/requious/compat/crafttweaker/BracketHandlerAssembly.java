package requious.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import requious.Registry;
import requious.data.AssemblyData;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

import java.util.List;

@BracketHandler
@ZenRegister
public class BracketHandlerAssembly implements IBracketHandler {
    private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerAssembly.class, "getFromString", String.class);

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if(tokens == null || tokens.size() < 3 || !tokens.get(0).getValue().equalsIgnoreCase("assembly"))
            return null;
        String name = tokens.get(2).getValue();
        return position -> new ExpressionCallStatic(position,environment,method, new ExpressionString(position, name));
    }

    public static AssemblyData getFromString(String name) {
        return Registry.getAssemblyData(name);
    }

    @Override
    public String getRegexMatchingString() {
        return "assembly:.*";
    }

    @Override
    public Class<?> getReturnedClass() {
        return AssemblyData.class;
    }
}

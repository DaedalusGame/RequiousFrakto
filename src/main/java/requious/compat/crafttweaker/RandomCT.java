package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Random;

@ZenRegister
@ZenClass("mods.requious.Random")
public class RandomCT {
    private Random random;
    private int seed;

    public RandomCT() {
        random = new Random();
        seed = random.nextInt();
        reset();
    }

    public void reset() {
        random.setSeed(seed);
    }

    @ZenMethod
    public int nextInt(int max) {
        return random.nextInt(max);
    }

    @ZenMethod
    public double nextDouble() {
        return random.nextDouble();
    }
}

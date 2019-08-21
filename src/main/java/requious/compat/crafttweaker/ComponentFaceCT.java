package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import requious.util.ComponentFace;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.requious.ComponentFace")
public class ComponentFaceCT {
    private static final ComponentFaceCT ALL = new ComponentFaceCT(ComponentFace.All);
    private static final ComponentFaceCT FRONT = new ComponentFaceCT(ComponentFace.Front);
    private static final ComponentFaceCT BACK = new ComponentFaceCT(ComponentFace.Back);
    private static final ComponentFaceCT SIDE = new ComponentFaceCT(ComponentFace.Side);
    private static final ComponentFaceCT FRONT_BACK = new ComponentFaceCT(ComponentFace.FrontBack);
    private static final ComponentFaceCT FRONT_SIDE = new ComponentFaceCT(ComponentFace.FrontSide);
    private static final ComponentFaceCT BACK_SIDE = new ComponentFaceCT(ComponentFace.BackSide);
    private static final ComponentFaceCT NONE = new ComponentFaceCT(ComponentFace.None);

    ComponentFace internal;

    public ComponentFaceCT(ComponentFace internal) {
        this.internal = internal;
    }

    public ComponentFace get() {
        return internal;
    }

    @ZenMethod
    public static ComponentFaceCT all() {
        return ALL;
    }

    @ZenMethod
    public static ComponentFaceCT front() {
        return FRONT;
    }

    @ZenMethod
    public static ComponentFaceCT back() {
        return BACK;
    }

    @ZenMethod
    public static ComponentFaceCT side() {
        return SIDE;
    }

    @ZenMethod
    public static ComponentFaceCT front_back() {
        return FRONT_BACK;
    }

    @ZenMethod
    public static ComponentFaceCT front_side() {
        return FRONT_SIDE;
    }

    @ZenMethod
    public static ComponentFaceCT back_side() {
        return BACK_SIDE;
    }

    @ZenMethod
    public static ComponentFaceCT none() {
        return NONE;
    }
}

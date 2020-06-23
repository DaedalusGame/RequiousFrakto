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
    private static final ComponentFaceCT UP = new ComponentFaceCT(ComponentFace.Up);
    private static final ComponentFaceCT DOWN = new ComponentFaceCT(ComponentFace.Down);
    private static final ComponentFaceCT NORTH = new ComponentFaceCT(ComponentFace.North);
    private static final ComponentFaceCT SOUTH = new ComponentFaceCT(ComponentFace.South);
    private static final ComponentFaceCT EAST = new ComponentFaceCT(ComponentFace.East);
    private static final ComponentFaceCT WEST = new ComponentFaceCT(ComponentFace.West);
    private static final ComponentFaceCT HORIZONTAL = new ComponentFaceCT(ComponentFace.Horizontal);
    private static final ComponentFaceCT VERTICAL = new ComponentFaceCT(ComponentFace.Vertical);

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

    @ZenMethod
    public static ComponentFaceCT north() {
        return NORTH;
    }

    @ZenMethod
    public static ComponentFaceCT south() {
        return SOUTH;
    }

    @ZenMethod
    public static ComponentFaceCT east() {
        return EAST;
    }

    @ZenMethod
    public static ComponentFaceCT west() {
        return WEST;
    }

    @ZenMethod
    public static ComponentFaceCT up() {
        return UP;
    }

    @ZenMethod
    public static ComponentFaceCT down() {
        return DOWN;
    }

    @ZenMethod
    public static ComponentFaceCT horizontal() {
        return HORIZONTAL;
    }

    @ZenMethod
    public static ComponentFaceCT vertical() {
        return VERTICAL;
    }
}

package requious.util;

import net.minecraft.util.EnumFacing;

public enum Facing {
    UP_LOCAL(EnumFacing.UP,false),
    DOWN_LOCAL(EnumFacing.DOWN,false),
    NORTH_LOCAL(EnumFacing.NORTH,false),
    SOUTH_LOCAL(EnumFacing.SOUTH,false),
    EAST_LOCAL(EnumFacing.EAST,false),
    WEST_LOCAL(EnumFacing.WEST,false),
    UP_GLOBAL(EnumFacing.UP,true),
    DOWN_GLOBAL(EnumFacing.DOWN,true),
    NORTH_GLOBAL(EnumFacing.NORTH,true),
    SOUTH_GLOBAL(EnumFacing.SOUTH,true),
    EAST_GLOBAL(EnumFacing.EAST,true),
    WEST_GLOBAL(EnumFacing.WEST,true);

    boolean global;
    EnumFacing facing;

    Facing(EnumFacing facing, boolean global) {
        this.facing = facing;
        this.global = global;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isLocal() {
        return !global;
    }
}

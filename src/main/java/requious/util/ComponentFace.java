package requious.util;

import net.minecraft.util.EnumFacing;

import java.util.HashSet;

public enum ComponentFace {
    None(new Facing[0]),
    Front(new Facing[]{Facing.UP_LOCAL}),
    Back(new Facing[]{Facing.DOWN_LOCAL}),
    Side(new Facing[]{Facing.NORTH_LOCAL,Facing.SOUTH_LOCAL,Facing.EAST_LOCAL,Facing.WEST_LOCAL}),
    FrontBack(new Facing[]{Facing.UP_LOCAL,Facing.DOWN_LOCAL}),
    FrontSide(new Facing[]{Facing.UP_LOCAL,Facing.NORTH_LOCAL,Facing.SOUTH_LOCAL,Facing.EAST_LOCAL,Facing.WEST_LOCAL}),
    BackSide(new Facing[]{Facing.DOWN_LOCAL,Facing.NORTH_LOCAL,Facing.SOUTH_LOCAL,Facing.EAST_LOCAL,Facing.WEST_LOCAL}),
    All(new Facing[]{Facing.UP_LOCAL,Facing.DOWN_LOCAL,Facing.NORTH_LOCAL,Facing.SOUTH_LOCAL,Facing.EAST_LOCAL,Facing.WEST_LOCAL}),
    Up(new Facing[]{Facing.UP_GLOBAL}),
    Down(new Facing[]{Facing.DOWN_GLOBAL}),
    North(new Facing[]{Facing.NORTH_GLOBAL}),
    South(new Facing[]{Facing.SOUTH_GLOBAL}),
    East(new Facing[]{Facing.EAST_GLOBAL}),
    West(new Facing[]{Facing.WEST_GLOBAL}),
    Horizontal(new Facing[]{Facing.NORTH_GLOBAL, Facing.SOUTH_GLOBAL, Facing.EAST_GLOBAL, Facing.WEST_GLOBAL}),
    Vertical(new Facing[]{Facing.UP_LOCAL, Facing.DOWN_GLOBAL});

    Facing[] sides;
    HashSet<EnumFacing> localFacings = new HashSet<>();
    HashSet<EnumFacing> globalFacings = new HashSet<>();

    ComponentFace(Facing[] sides) {
        this.sides = sides;
        for (Facing side : sides) {
            if(side.isGlobal())
                globalFacings.add(side.getFacing());
            else
                localFacings.add(side.getFacing());
        }
    }

    public Facing[] getSides() {
        return sides;
    }

    public boolean matches(EnumFacing sideLocal, EnumFacing sideGlobal) {
        /*switch (sideLocal) {
            case DOWN:
                return this == Back || this == FrontBack || this == BackSide || this == All;
            case UP:
                return this == Front || this == FrontBack || this == FrontSide || this == All;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                return this == Side || this == FrontSide || this == BackSide || this == All;
        }
        switch (sideGlobal) {
            case DOWN:
                return this == Down || this == Vertical;
            case UP:
                return this == Up || this == Vertical;
            case NORTH:
                return this == North || this == Horizontal;
            case SOUTH:
                return this == South || this == Horizontal;
            case WEST:
                return this == West || this == Horizontal;
            case EAST:
                return this == East || this == Horizontal;
        }*/
        if(localFacings.contains(sideLocal))
            return true;
        if(globalFacings.contains(sideGlobal))
            return true;
        return false;
    }

    public Facing getSide(int i) {
        return sides[i % sides.length];
    }
}

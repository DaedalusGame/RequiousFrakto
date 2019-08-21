package requious.util;

import net.minecraft.util.EnumFacing;

public enum ComponentFace {
    None(new EnumFacing[0]),
    Front(new EnumFacing[]{EnumFacing.UP}),
    Back(new EnumFacing[]{EnumFacing.DOWN}),
    Side(EnumFacing.HORIZONTALS),
    FrontBack(new EnumFacing[]{EnumFacing.UP,EnumFacing.DOWN}),
    FrontSide(new EnumFacing[]{EnumFacing.UP,EnumFacing.NORTH,EnumFacing.EAST,EnumFacing.SOUTH,EnumFacing.WEST}),
    BackSide(new EnumFacing[]{EnumFacing.DOWN,EnumFacing.NORTH,EnumFacing.EAST,EnumFacing.SOUTH,EnumFacing.WEST}),
    All(EnumFacing.VALUES);

    EnumFacing[] sides;

    ComponentFace(EnumFacing[] sides) {
        this.sides = sides;
    }

    public boolean matches(EnumFacing side) {
        switch (side) {
            case DOWN:
                return this == Back || this == FrontBack || this == BackSide || this == All;
            case UP:
                return this == Front || this == FrontBack || this == FrontSide || this == All;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                return this == Side || this == FrontSide || this == BackSide || this == All;
            default:
                return false;
        }
    }

    public EnumFacing getSide(int i) {
        return sides[i % sides.length];
    }
}

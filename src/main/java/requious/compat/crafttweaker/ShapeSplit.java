package requious.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;
import requious.item.Shape;

public class ShapeSplit implements IShape {
    Shape shape;

    public ShapeSplit(Shape shape) {
        this.shape = shape;
    }

    @Override
    public Shape getShape() {
        return shape.toLayer();
    }

    @Override
    public IItemStack toItem() {
        return null;
    }
}

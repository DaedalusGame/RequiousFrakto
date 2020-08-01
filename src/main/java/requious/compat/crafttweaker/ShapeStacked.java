package requious.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;
import requious.item.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ShapeStacked implements IShape {
    List<Shape> shapes = new ArrayList<>();

    public ShapeStacked() {}

    public ShapeStacked(Collection<Shape> shapes) {
        this.shapes.addAll(shapes);
    }

    @Override
    public Shape getShape() {
        Shape base = null;
        Shape top = null;
        for(Shape shape : shapes) {
            for(Shape piece = shape; piece != null; piece = piece.getInner()) {
                Shape layer = piece.toLayer();
                if(top == null) {
                    base = layer;
                    top = layer;
                } else {
                    top.setInner(layer);
                    top = layer;
                }
            }
        }
        if(base == null)
            base = new Shape();
        return base;
    }

    @Override
    public IItemStack toItem() {
        return null;
    }
}

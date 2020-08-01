package requious.compat.crafttweaker;

import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import requious.Registry;
import requious.item.Shape;
import stanhebben.zenscript.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ZenClass("requious.shape")
@ZenRegister
public interface IShape {
    Shape getShape();

    @ZenCaster
    @ZenMethod
    default IItemStack toItem() {
        return CraftTweakerMC.getIItemStack(Registry.SHAPE.create(getShape()));
    }

    @ZenMethod
    default IShape[] cut() {
        Shape shape = getShape();
        List<IShape> shapes = new ArrayList<>();
        for(Shape piece = shape; piece != null; piece = piece.getInner()) {
            shapes.add(new ShapeCut(piece, Shape.Piece.TOP_LEFT));
            shapes.add(new ShapeCut(piece, Shape.Piece.TOP_RIGHT));
            shapes.add(new ShapeCut(piece, Shape.Piece.BOTTOM_LEFT));
            shapes.add(new ShapeCut(piece, Shape.Piece.BOTTOM_RIGHT));
        }
        return shapes.toArray(new IShape[shapes.size()]);
    }

    @ZenMethod
    default IShape[] unstack() {
        Shape shape = getShape();
        List<IShape> shapes = new ArrayList<>();
        for(Shape piece = shape; piece != null; piece = piece.getInner()) {
            shapes.add(new ShapeBase(piece));
        }
        return shapes.toArray(new IShape[shapes.size()]);
    }

    @ZenMethod
    default IShape toLayer() {
        Shape shape = getShape();
        return new ShapeBase(shape.toLayer());
    }

    @ZenOperator(OperatorType.ADD)
    default IShape stack(IShape other) {
        return new ShapeStacked(Lists.newArrayList(getShape(),other.getShape()));
    }

    @ZenMethodStatic
    static IShape stack(IShape[] shapes) {
        return new ShapeStacked(Arrays.stream(shapes).map(IShape::getShape).collect(Collectors.toList()));
    }
}

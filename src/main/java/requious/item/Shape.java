package requious.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import requious.compat.crafttweaker.IShape;
import requious.compat.crafttweaker.ShapeBase;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shape {
    public enum Piece {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
    }

    public static class Part {
        ResourceLocation texture;
        String material;
        Color color;

        public Part(ResourceLocation texture, String material, Color color) {
            this.texture = texture;
            this.material = material;
            this.color = color;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public String getMaterial() {
            return material;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Part) {
                Part other = (Part) obj;
                return other.material.equals(material) && other.texture.equals(texture) && other.color.equals(color);
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return Objects.hash(texture, material, color);
        }
    }

    Shape inner;
    Part[] parts = new Part[4];

    public Shape() {}

    public Shape(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null && tagCompound.hasKey("shape",10))
            readFromNBT(tagCompound.getCompoundTag("shape"));
    }

    public Shape(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public boolean isEmpty() {
        for (int i = 0; i < parts.length; i++) {
            if(parts[i] != null)
                return false;
        }
        return inner == null || inner.isEmpty();
    }

    public void setInner(Shape inner) {
        this.inner = inner;
    }

    public Shape getInner() {
        return inner;
    }

    public void setPart(Piece piece, Part part) {
        parts[piece.ordinal()] = part;
    }

    public Part getPart(Piece piece) {
        return parts[piece.ordinal()];
    }

    public Shape toLayer() {
        Shape layer = new Shape();
        for (int i = 0; i < parts.length; i++)
            layer.parts[i] = parts[i];
        return layer;
    }

    public List<Shape> split() {
        List<Shape> shapes = new ArrayList<>();
        for(Shape piece = this; piece != null; piece = piece.getInner()) {
            shapes.add(piece);
        }
        return shapes;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Shape) {
            Shape other = (Shape) obj;
            for (int i = 0; i < parts.length; i++) {
                if(!Objects.equals(parts[i],other.parts[i]))
                    return false;
            }
            return Objects.equals(inner, other.inner);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inner, parts[0], parts[1], parts[2], parts[3]);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        for(int i = 0; i < parts.length; i++) {
            Part part = parts[i];
            if(part != null) {
                tag.setInteger("color" + i, part.getColor().getRGB());
                tag.setString("material" + i, part.getMaterial());
                tag.setString("texture" + i, part.getTexture().toString());
            }
        }
        tag.setTag("inner", inner.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        for(int i = 0; i < parts.length; i++) {
            if(!tag.hasKey("color"+i))
                continue;
            Color color = new Color(tag.getInteger("color"+i));
            String material = tag.getString("material"+i);
            ResourceLocation texture = new ResourceLocation(tag.getString("texture"+i));
            parts[i] = new Part(texture,material,color);
        }
        if(tag.hasKey("inner")) {
            inner = new Shape(tag.getCompoundTag("inner"));
        }
    }
}

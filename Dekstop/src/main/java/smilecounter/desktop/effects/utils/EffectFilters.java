package smilecounter.desktop.effects.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.RescaleOp;

public class EffectFilters {
    public static AffineTransformOp flipHorizontal(double scale, int width){
        AffineTransform tx = AffineTransform.getScaleInstance(-scale, scale);
        tx.translate(width, 0);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    public static AffineTransformOp scale(double scale){
        AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    public static AffineTransformOp rotate(double angle, double scale, int width, int height){
        AffineTransform tx = new AffineTransform();
        tx.translate(width/2, height/2);
        tx.rotate(Math.toRadians(angle));
        tx.scale(scale, scale);
        tx.translate(-width/2, -height/2);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    public static RescaleOp makeTransparent(float alpha){
        float[] factors = new float[] {
                1.f, 1.f, 1.f, alpha
        };
        float[] offsets = new float[] {
                0.0f, 0.0f, 30.0f, alpha - 1
        };
        return new RescaleOp(factors, offsets, null);
    }
}

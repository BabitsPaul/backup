package util.ui;

import java.awt.*;

public class PaintingUtil
{
    private static final double SQRT_3 = Math.sqrt(3);

    public static final double RIGHT = 0,
                                UP = Math.PI * 1.5, //UP and DOWN need to be interchanged (coordinate system)
                                LEFT = Math.PI,
                                DOWN = Math.PI * 0.5;

    /**
     * generates a triangle in the center of which lies the point (x, y).
     * By default the tip of the triangle points to the right and can be rotated
     * counter-clockwise. Height specifies the height of the triangle (base-line to tip).
     * Pointiness = 1 produces a equilateral triangle. For values larger a isosceles
     * triangle will be produced, where pointiness specifies the relation baseline : height
     *
     * @param rotation rotation of the triangle in rad
     * @param x x-position of the center of the triangle
     * @param y y-position of the center of the triangle
     * @param height the height of the triangle
     * @param pointiness ratio baseline : height
     * @return a triangle with the specified properties
     */
    public static Polygon getTriangle(double rotation, int x, int y, int height, double pointiness)
    {
        double sideLength = 2 * height / SQRT_3;

        double[] xtmp = new double[]{
                - height / 2, height / 2, - height / 2
        };

        double[] ytmp = new double[]{
                - (sideLength / 2), 0, (sideLength / 2)
        };

        Polygon result = new Polygon();

        for(int i = 0; i < 3; i++)
        {
            //spikiness transformation
            ytmp[i] *= pointiness;

            double xh = xtmp[i], yh = ytmp[i];

            //rotation
            xtmp[i] = Math.cos(rotation) * xh - Math.sin(rotation) * yh;
            ytmp[i] = Math.sin(rotation) * xh + Math.cos(rotation) * yh;

            //translation
            xtmp[i] += x;
            ytmp[i] += y;

            result.addPoint((int) xtmp[i], (int) ytmp[i]);
        }

        return result;
    }
}

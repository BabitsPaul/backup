package copy.profiler;

import copy.profiler.timingutil.IOSpeed;
import util.ui.PaintingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class ProfilerDiagram
{
    private static final int IMAGE_WIDTH = 500;
    private static final int IMAGE_HEIGHT = 200;

    private static final int PADDING = 8;

    private JComponent diagram;

    private BufferedImage biMain, biBackgroud;

    private ProfilerCache cache;

    public ProfilerDiagram(ProfilerCache cache)
    {
        this.cache = cache;

        biMain = new BufferedImage(IMAGE_WIDTH + PADDING * 2, IMAGE_HEIGHT + PADDING * 2, BufferedImage.TYPE_4BYTE_ABGR);
        biBackgroud = new BufferedImage(IMAGE_WIDTH + PADDING * 2, IMAGE_HEIGHT + PADDING * 2, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public JComponent getComponent()
    {
        if(diagram == null)
        {
            if(!SwingUtilities.isEventDispatchThread())
                throw new RuntimeException("Can't create UI in event-queue");

            diagram = new JPanel()
            {
                public void paintComponent(Graphics g)
                {
                    g.drawImage(biMain,0, 0, getWidth(), getHeight(), this);
                }
            };
            diagram.setPreferredSize(new Dimension(biMain.getWidth(), biMain.getHeight()));
        }

        return diagram;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // scale factor                                                                                                   //
    //                                                                                                                //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //pixels per data-point
    private double scaleFactorX;

    //pixel per byte
    private double scaleFactorY = 1.0;

    private void updateScaleFactors()
    {
        int dpCount = cache.list().size();

        if(dpCount == 0)
        {
            //just dummy values to not disrupt rendering
            scaleFactorX = 1.0;
            scaleFactorY = 1.0;
        }

        IOSpeed max = cache.max().current;

        scaleFactorX = (double) IMAGE_WIDTH / (dpCount - 1);
        //1 / bytes per pixel
        scaleFactorY = 1 / (max.getSpeed() * max.getDataUnit().getBytesPerUnit() / max.getTimeUnit().getFactor());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // rendering                                                                                                      //
    //                                                                                                                //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int ARROW_DIAMETER = 5;

    private static final Polygon ARROW_RIGHT =
            PaintingUtil.getTriangle(PaintingUtil.RIGHT, PADDING + IMAGE_WIDTH, PADDING + IMAGE_HEIGHT, ARROW_DIAMETER, 1.0),
                                    ARROW_UP =
            PaintingUtil.getTriangle(PaintingUtil.UP, PADDING, PADDING, ARROW_DIAMETER, 1.0);

    private void drawScale(Graphics g)
    {
        g.setColor(Color.black);

        //horizontal axis
        g.drawLine(PADDING, IMAGE_HEIGHT + PADDING, IMAGE_WIDTH + PADDING, IMAGE_HEIGHT + PADDING);
        g.fillPolygon(ARROW_RIGHT);

        //vertical axis
        g.drawLine(PADDING, PADDING, PADDING, IMAGE_HEIGHT + PADDING);
        g.fillPolygon(ARROW_UP);
    }

    private void drawDiagram(Graphics g)
    {
        //TODO incorrect display

        g.setColor(Color.green);

        //build polygon
        java.util.List<ProfilerDataPoint> dataPoints = cache.list();

        int[] movesX = new int[dataPoints.size() + 2];
        int[] movesY = new int[dataPoints.size() + 2];

        Iterator<ProfilerDataPoint> dpIter = dataPoints.iterator();
        for(int i = 0; dpIter.hasNext(); i++)
        {
            IOSpeed speed = dpIter.next().current;

            movesX[i] = (int) (i * scaleFactorX) + PADDING;
            movesY[i] = (int) (speed.getSpeed() * speed.getDataUnit().getBytesPerUnit() /
                                speed.getTimeUnit().getFactor() * scaleFactorY) + PADDING;

            if(movesX[i] > IMAGE_WIDTH || movesY[i] > IMAGE_HEIGHT)
                System.out.println("Invalid position");
        }

        //lower border of the diagram
        movesX[movesX.length - 1] = PADDING + 1;
        movesX[movesX.length - 2] = IMAGE_WIDTH + PADDING;
        movesY[movesY.length - 2] = movesY[movesY.length - 1] = IMAGE_HEIGHT + PADDING - 1;

        g.fillPolygon(movesX, movesY, dataPoints.size() + 2);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // rendering                                                                                                      //
    //                                                                                                                //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update()
    {
        updateScaleFactors();

        Graphics2D g = (Graphics2D) biBackgroud.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, biBackgroud.getWidth(), biBackgroud.getHeight());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        drawScale(g);
        drawDiagram(g);
        g.dispose();

        //update reference to image
        BufferedImage swap = biMain;
        biMain = biBackgroud;
        biBackgroud = swap;
    }
}
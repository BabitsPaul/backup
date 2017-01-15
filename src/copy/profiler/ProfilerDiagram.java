package copy.profiler;

import util.unit.IOSpeed;
import util.unit.Time;
import util.ui.PaintingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

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
                throw new RuntimeException("Can't create UI outside of event-queue");

            if(SwingUtilities.isEventDispatchThread())
            {
                diagram = new JPanel()
                {
                    public void paintComponent(Graphics g)
                    {
                        g.drawImage(biMain,0, 0, getWidth(), getHeight(), this);
                    }
                };
                diagram.setPreferredSize(new Dimension(biMain.getWidth(), biMain.getHeight()));
            }
            else
            {
                try {
                    SwingUtilities.invokeAndWait(()->{
                        diagram = new JPanel()
                        {
                            public void paintComponent(Graphics g)
                            {
                                g.drawImage(biMain,0, 0, getWidth(), getHeight(), this);
                            }
                        };
                        diagram.setPreferredSize(new Dimension(biMain.getWidth(), biMain.getHeight()));
                    });
                } catch (InterruptedException | InvocationTargetException e) {
                    //TODO exception handling
                    e.printStackTrace();
                }
            }
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
        Time running = cache.list().get(dpCount - 1).totalTimeRunning;

        if(dpCount == 0)
        {
            //just dummy values to not disrupt rendering
            scaleFactorX = 1.0;
            scaleFactorY = 1.0;

            return;
        }

        IOSpeed max = cache.max().current;

        scaleFactorX = (double) IMAGE_WIDTH / (running.getValue() * running.getUnit().getFactor());
        //pixel per byte    make sure the diagram never fills the entire area
        scaleFactorY = IMAGE_HEIGHT / (max.getSpeed() * max.getDataUnit().getBytesPerUnit() / max.getTimeUnit().getFactor()) * 0.8;
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
        g.setColor(Color.green);

        //build polygon
        //TODO optimize (reuse polygon)
        java.util.List<ProfilerDataPoint> dataPoints = cache.list();
        Polygon p = new Polygon();

        for (ProfilerDataPoint dp : dataPoints) {
            IOSpeed speed = dp.current;

            int x = (int) (dp.totalTimeRunning.getUnit().getFactor() * dp.totalTimeRunning.getValue() * scaleFactorX) + PADDING;
            int y = PADDING + IMAGE_HEIGHT - (int) (speed.getSpeed() * speed.getDataUnit().getBytesPerUnit() /
                    speed.getTimeUnit().getFactor() * scaleFactorY);

            p.addPoint(x, y);
        }

        //lower border of the diagram
        p.addPoint(IMAGE_WIDTH + PADDING, IMAGE_HEIGHT + PADDING - 1);
        p.addPoint(PADDING + 1, IMAGE_HEIGHT + PADDING + 1);

        g.fillPolygon(p);
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
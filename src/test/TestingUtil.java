package test;

import copy.profiler.ProfilerCache;
import copy.profiler.ProfilerDataPoint;
import copy.profiler.ProfilerDiagram;
import util.io.AbstractIOObject;
import util.io.FileObject;
import util.io.IOObjectIterator;
import util.unit.IOSpeed;
import util.unit.Time;
import util.unit.TimeUnit;
import mgr.Manager;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class TestingUtil
{
    public static void run()
    {
        //decide what to run
        testTotal();
        //testProfilerDiagramDynamic();
        //testProfilerDiagramStatic();
        //testTime();
        //testIOObjectIterator();
        //testIOObject();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // overall test                                                               //
    //                                                                            //
    //                                                                            //
    ////////////////////////////////////////////////////////////////////////////////

    private static void testTotal()
    {
        new Manager().setup();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // profiler diagram                                                           //
    //                                                                            //
    //                                                                            //
    ////////////////////////////////////////////////////////////////////////////////

    private static void testProfilerDiagramStatic()
    {
        //build cache
        ProfilerCache cache = new ProfilerCache();

        ProfilerDataPoint dp1 = new ProfilerDataPoint();
        dp1.totalTimeRunning = new Time(0, TimeUnit.SECONDS);
        dp1.current = new IOSpeed(50, 5000, TimeUnit.SECONDS);
        cache.place(dp1);

        ProfilerDataPoint dp2 = new ProfilerDataPoint();
        dp2.totalTimeRunning = new Time(1, TimeUnit.SECONDS);
        dp2.current = new IOSpeed(50, 10000, TimeUnit.SECONDS);
        cache.place(dp2);

        ProfilerDataPoint dp3 = new ProfilerDataPoint();
        dp3.totalTimeRunning = new Time(2, TimeUnit.SECONDS);
        dp3.current = new IOSpeed(50, 3500, TimeUnit.SECONDS);
        cache.place(dp3);

        ProfilerDataPoint dp4 = new ProfilerDataPoint();
        dp4.totalTimeRunning = new Time(3, TimeUnit.SECONDS);
        dp4.current = new IOSpeed(50, 6000, TimeUnit.SECONDS);
        cache.place(dp4);

        //generate diagram
        SwingUtilities.invokeLater(()->{
            ProfilerDiagram diagram = new ProfilerDiagram(cache);
            diagram.update();
            display(diagram.getComponent());
        });
    }

    private static void testProfilerDiagramDynamic()
    {
        long started = System.currentTimeMillis();

        ProfilerCache cache = new ProfilerCache();
        ProfilerDiagram diagram = new ProfilerDiagram(cache);

        //generate diagram
        try {
            SwingUtilities.invokeAndWait(()->{
                diagram.update();
                display(diagram.getComponent());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        Random rnd = new Random();

        Thread t = new Thread(()->{
            while(true){
                ProfilerDataPoint dp = new ProfilerDataPoint();
                dp.totalTimeRunning = new Time((System.currentTimeMillis() - started) / 1000, TimeUnit.SECONDS);
                dp.current = new IOSpeed(50, 2000 + rnd.nextInt(8000), TimeUnit.SECONDS);
                cache.place(dp);

                diagram.update();
                diagram.getComponent().repaint();

                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){}
            }
        });
        t.setName("Cache Inserter");
        t.start();
    }

    //////////////////////////////////////////////////////////////////////////////
    // timeunit                                                                 //
    //                                                                          //
    //                                                                          //
    //////////////////////////////////////////////////////////////////////////////

    private static void testTime()
    {
        Time[] t = new Time[]{
                new Time(40, TimeUnit.NANO_SECONDS),
                new Time(500000000, TimeUnit.NANO_SECONDS),
                new Time(532555555500000L, TimeUnit.NANO_SECONDS),
                new Time(50, TimeUnit.MILLI_SECONDS),
                new Time(600000000L, TimeUnit.MILLI_SECONDS),
                new Time(3980, TimeUnit.SECONDS)    //1:6:20
        };

        for(Time time : t)
            System.out.println(time);
    }

    //////////////////////////////////////////////////////////////////////////////
    // utility                                                                  //
    //                                                                          //
    //                                                                          //
    //////////////////////////////////////////////////////////////////////////////

    private static void display(JComponent comp)
    {
        SwingUtilities.invokeLater(()->{
            JFrame frame = new JFrame("Test");
            frame.getContentPane().add(comp);
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    ///////////////////////////////////////////////////////////////////////////////
    // util.io                                                                   //
    //                                                                           //
    //                                                                           //
    ///////////////////////////////////////////////////////////////////////////////

    private static void testIOObject()
    {
        AbstractIOObject iobj = new FileObject("..");
        System.out.println(iobj);

        for(AbstractIOObject child : iobj.listChildren())
            System.out.println(child);
    }

    private static void testIOObjectIterator()
    {
        IOObjectIterator iter = new IOObjectIterator(new FileObject(".."));

        while(iter.hasNext())
        {
            System.out.println(iter.next().getName());
        }
    }
}

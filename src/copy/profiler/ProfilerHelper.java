package copy.profiler;

import copy.CopyState;
import copy.profiler.timingutil.IOSpeed;
import copy.profiler.timingutil.Time;
import copy.profiler.timingutil.TimeUnit;
import javafx.util.Pair;

import java.util.LinkedList;

public class ProfilerHelper
{
    private CopyState state;

    private long startTime;

    private ProfilerDataPoint dataPoint;

    public ProfilerHelper(CopyState state)
    {
        this.state = state;

        startTime = System.currentTimeMillis();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // general values                                                                             //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private long totalBytesProgress;

    public void interpolationTick()
    {
        //load all values before interpolation to keep maximum precision
        dataPoint = new ProfilerDataPoint();
        dataPoint.currentTimeMills = new Time(System.currentTimeMillis(), TimeUnit.MILLI_SECONDS);
        dataPoint.currentTimeNanos = new Time(System.nanoTime(), TimeUnit.NANO_SECONDS);
        dataPoint.totalTimeRunning = new Time(dataPoint.currentTimeMills.getValue() - startTime - totalPauseDuration, TimeUnit.MILLI_SECONDS);
        totalBytesProgress = state.getTotalBytesProgress();

        interpolateTotalSpeed();
        interpolateCurrentSpeed();
        interpolateMiddleTermSpeed();

        estimateRemainingTime();
    }

    public ProfilerDataPoint getLastPoint()
    {
        return dataPoint;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // pausing                                                                                    //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private long totalPauseDuration = 0;

    private long lastPauseStart;

    private boolean paused = false;

    public void processPaused()
    {
        if(paused)
            throw new IllegalStateException("Already paused");

        lastPauseStart = System.currentTimeMillis();
        paused = true;

        //update current time measurement
        lastNanoTime = System.nanoTime();
    }

    public void processContinued()
    {
        if(!paused)
            throw new IllegalStateException("Not paused");

        totalPauseDuration += System.currentTimeMillis() - lastPauseStart;
        paused = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // total speed                                                                                //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void interpolateTotalSpeed()
    {
        dataPoint.total = new IOSpeed(dataPoint.currentTimeMills.getValue() - startTime - totalPauseDuration, totalBytesProgress, TimeUnit.MILLI_SECONDS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // current speed                                                                              //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private long lastNanoTime;

    private long lastBytesCopied = 0;

    private void interpolateCurrentSpeed()
    {
        dataPoint.current = new IOSpeed(dataPoint.currentTimeNanos.getValue() - lastNanoTime,totalBytesProgress - lastBytesCopied, TimeUnit.NANO_SECONDS);

        lastNanoTime = dataPoint.currentTimeNanos.getValue();
        lastBytesCopied = totalBytesProgress;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // middle term speed                                                                           //
    //                                                                                             //
    //                                                                                             //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    //TODO use cache
    private static final int DEFAULT_DATA_POINTS = 1000;

    private int dataPoints = DEFAULT_DATA_POINTS;

    private LinkedList<Pair<Long, Long>> lastWrites = new LinkedList<>();

    private void interpolateMiddleTermSpeed()
    {
        //TODO instable + handle speed, when nothing is copied
        dataPoint.middleTerm = dataPoint.current;
        return;

        /*
        //do not create middle term speed on first interpolation
        if(totalBytesProgress - lastBytesCopied == 0)
            return;

        //update list of writes
        if(lastWrites.size() > dataPoints)
            lastWrites.remove(0);

        //no need to keep an eye on pauses, as data-points are created independently
        lastWrites.add(new Pair<>(dataPoint.currentTimeNanos.getValue() - lastNanoTime, totalBytesProgress - lastBytesCopied));

        double tmpSpeed = lastWrites.stream().mapToDouble(p->p.getValue() / p.getKey()).sum() / lastWrites.size();
        dataPoint.middleTerm = new IOSpeed(tmpSpeed, TimeUnit.NANO_SECONDS, DataUnit.SI_BYTE);
        */
    }

    public void setDataPointNum(int ndataPoints)
    {
        dataPoints = ndataPoints;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // remaining time estimation                                                                    //
    //                                                                                              //
    //                                                                                              //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int WEIGHT_TOTAL_SPEED = 5;
    private static final int WEIGHT_CURRENT_SPEED = 2;
    private static final int WEIGHT_MIDDLE_TERM_SPEED = 7;

    private long timeRemaining = -1;

    private void estimateRemainingTime()
    {
        if(!state.isPrecomputationComplete())
            return;

        long bytesRemaining = state.getTotalBytes() - totalBytesProgress;

        final int[] weights = new int[]{
                WEIGHT_CURRENT_SPEED,
                WEIGHT_MIDDLE_TERM_SPEED,
                WEIGHT_TOTAL_SPEED
        };

        final IOSpeed[] speed = new IOSpeed[]{
                dataPoint.current,
                dataPoint.middleTerm,
                dataPoint.total
        };

        long arithmMeanNumerator = 0L,
                arithmMeanDenominator = 0L;
        for(int i = 0; i < weights.length; i++)
        {
            arithmMeanNumerator += (long) (bytesRemaining * speed[i].getTimeUnit().getFactor()
                                                / (speed[i].getSpeed() * speed[i].getDataUnit().getBytesPerUnit())) * weights[i];

            arithmMeanDenominator += weights[i];
        }

        timeRemaining = arithmMeanNumerator / arithmMeanDenominator;
    }

    public long getTimeRemaining(TimeUnit unit)
    {
        return timeRemaining * unit.getFactor() / TimeUnit.SECONDS.getFactor();
    }
}

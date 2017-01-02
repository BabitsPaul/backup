package copy.profiler;

import copy.CopyState;
import javafx.util.Pair;

import java.util.LinkedList;

public class ProfilerHelper
{
    private CopyState state;

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

    private long totalBytesProgress,
                    currentTimeMillis,
                    currentTimeNano;

    public void interpolationTick()
    {
        //load all values before interpolation to keep maximum precision
        currentTimeNano = System.nanoTime();
        currentTimeMillis = System.currentTimeMillis();
        totalBytesProgress = state.getTotalBytesProgress();

        interpolateTotalSpeed();
        interpolateCurrentSpeed();
        interpolateMiddleTermSpeed();

        estimateRemainingTime();
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

    private IOSpeed total = new IOSpeed(1, 0, TimeUnit.SECONDS);

    private long startTime;

    private void interpolateTotalSpeed()
    {
        total = new IOSpeed(currentTimeMillis - startTime - totalPauseDuration, totalBytesProgress, TimeUnit.MILLI_SECONDS);
    }

    public IOSpeed getTotal()
    {
        return total;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // current speed                                                                              //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private IOSpeed current = new IOSpeed(1, 0, TimeUnit.SECONDS);

    private long lastNanoTime;

    private long lastBytesCopied = 0;

    private void interpolateCurrentSpeed()
    {
        current = new IOSpeed(currentTimeNano - lastNanoTime,totalBytesProgress - lastBytesCopied, TimeUnit.NANO_SECONDS);

        lastNanoTime = currentTimeNano;
        lastBytesCopied = totalBytesProgress;
    }

    public IOSpeed getCurrent()
    {
        return current;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // middle term speed                                                                           //
    //                                                                                             //
    //                                                                                             //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int DEFAULT_DATA_POINTS = 1000;

    private int dataPoints = DEFAULT_DATA_POINTS;

    private LinkedList<Pair<Long, Long>> lastWrites = new LinkedList<>();

    private IOSpeed middleTerm = new IOSpeed(1, 0, TimeUnit.SECONDS);

    private void interpolateMiddleTermSpeed()
    {
        //update list of writes
        if(lastWrites.size() > dataPoints)
            lastWrites.remove(0);

        //no need to keep an eye on pauses, as data-points are created independently
        lastWrites.add(new Pair<>(currentTimeNano - lastNanoTime, totalBytesProgress - lastBytesCopied));

        double tmpSpeed = lastWrites.stream().mapToDouble(p->p.getValue() / p.getKey()).sum() / lastWrites.size();
        middleTerm = new IOSpeed(tmpSpeed, TimeUnit.NANO_SECONDS, DataUnit.SI_BYTE);
    }

    public void setDataPointNum(int ndataPoints)
    {
        dataPoints = ndataPoints;
    }

    public IOSpeed getMiddleTerm()
    {
        return middleTerm;
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
                current,
                middleTerm,
                total
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

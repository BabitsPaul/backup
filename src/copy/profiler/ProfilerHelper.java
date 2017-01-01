package copy.profiler;

import copy.CopyState;

public class ProfilerHelper
{
    //TODO median speed (evtl for interpolation of time)

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

    private IOSpeed middleTerm = new IOSpeed(1, 0, TimeUnit.SECONDS);

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // remaining time estimation                                                                    //
    //                                                                                              //
    //                                                                                              //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int WEIGHT_TOTAL_SPEED = 5;
    private static final int WEIGHT_CURRENT_SPEED = 2;

    private long timeRemaining = -1;

    private void estimateRemainingTime()
    {
        if(!state.isPrecomputationComplete())
            return;

        long bytesRemaining = state.getTotalBytes() - totalBytesProgress;

        //time = bytes * (speed * factor to bytes/second) ^ (-1)
        long timeRemainingTotal = (long) (bytesRemaining * total.getTimeUnit().getFactor()
                                            / (total.getSpeed() * total.getDataUnit().getBytesPerUnit()));

        long timeRemainingCurrent = (long) (bytesRemaining * current.getTimeUnit().getFactor()
                                            / (current.getSpeed() * current.getDataUnit().getBytesPerUnit()));

        timeRemaining = (WEIGHT_TOTAL_SPEED * timeRemainingTotal + WEIGHT_CURRENT_SPEED * timeRemainingCurrent)
                            / (WEIGHT_CURRENT_SPEED + WEIGHT_TOTAL_SPEED);
    }

    public long getTimeRemaining(TimeUnit unit)
    {
        return timeRemaining * unit.getFactor() / TimeUnit.SECONDS.getFactor();
    }
}

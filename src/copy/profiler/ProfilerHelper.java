package copy.profiler;

import copy.CopyState;

public class ProfilerHelper
{
    //TODO pausing
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
    }

    public IOSpeed getTotal() {
        return total;
    }

    public IOSpeed getCurrent() {
        return current;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // pausing                                                                                    //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void processPaused()
    {

    }

    public void processContinued()
    {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // total speed                                                                                //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private IOSpeed total;

    private long startTime;

    private void interpolateTotalSpeed()
    {
        total = new IOSpeed(currentTimeMillis - startTime, totalBytesProgress, TimeUnit.MILLI_SECONDS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // current speed                                                                              //
    //                                                                                            //
    //                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private IOSpeed current;

    private long lastNanoTime;

    private long lastBytesCopied = 0;

    private void interpolateCurrentSpeed()
    {
        current = new IOSpeed(currentTimeNano - lastNanoTime,totalBytesProgress - lastBytesCopied, TimeUnit.NANO_SECONDS);

        lastNanoTime = currentTimeNano;
        lastBytesCopied = totalBytesProgress;
    }
}

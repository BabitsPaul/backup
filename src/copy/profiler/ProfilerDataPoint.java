package copy.profiler;

import copy.profiler.timingutil.IOSpeed;
import copy.profiler.timingutil.Time;

import java.util.Comparator;

public class ProfilerDataPoint
{
    private static long next_id = 0;

    public final long ID = next_id++;

    public IOSpeed current, total, middleTerm;

    public Time currentTimeMills = Time.NA,
                    currentTimeNanos = Time.NA,
                    totalTimeRunning = Time.NA,
                    timeRemaining = Time.NA;
}

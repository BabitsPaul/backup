package copy.profiler.timingutil;

public class IOSpeed
    implements Comparable<IOSpeed>
{
    /**
     * speed in dataunit/seconds
     */
    private double speed;

    private DataUnit dataUnit;

    public IOSpeed(long deltaTime, long deltaBytes, TimeUnit timeUnit)
    {
        double seconds = (double) deltaTime / timeUnit.getFactor();
        speed = deltaBytes / seconds;
        dataUnit = DataUnit.SI_BYTE;

        while (speed / dataUnit.getBytesPerUnit() >= 1000)
            dataUnit = DataUnit.up(dataUnit);

        //final speed
        speed /= dataUnit.getBytesPerUnit();
    }

    public IOSpeed(double speed, TimeUnit timeUnit, DataUnit unit)
    {
        //transform to bytes/second
        speed *= unit.getBytesPerUnit();
        speed /= timeUnit.getFactor() * TimeUnit.SECONDS.getFactor();

        dataUnit = DataUnit.SI_BYTE;

        while (speed / dataUnit.getBytesPerUnit() >= 1000)
            dataUnit = DataUnit.up(dataUnit);

        //final speed
        speed /= dataUnit.getBytesPerUnit();
    }

    public double getSpeed() {
        return speed;
    }

    public DataUnit getDataUnit() {
        return dataUnit;
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
    }

    public String toString()
    {
        return speed + dataUnit.getName() + "/" + getTimeUnit().getName();
    }

    @Override
    public int compareTo(IOSpeed o) {
        return Double.compare(speed * dataUnit.getBytesPerUnit(), (o.speed * o.dataUnit.getBytesPerUnit()));
    }
}

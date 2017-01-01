package copy.profiler;

public class IOSpeed
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

    public double getSpeed() {
        return speed;
    }

    public DataUnit getDataUnit() {
        return dataUnit;
    }
}

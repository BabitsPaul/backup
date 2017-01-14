package copy.profiler.timingutil;

public class Time
    implements Comparable<Time>
{
    //this time is NOT applicable to transform
    public static final Time NA = new Time(0, null){
        public String toString()
        {
            return "NA";
        }

        public Time transform(TimeUnit unit)
        {
            return this;
        }
    };

    private long value;

    private TimeUnit unit;

    public Time(long value, TimeUnit unit)
    {
        this.value = value;
        this.unit = unit;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }

    public long getValue()
    {
        return value;
    }

    public Time transform(TimeUnit unit)
    {
        return new Time(value * this.unit.getFactor() / unit.getFactor(), unit);
    }

    public String toString()
    {
        //TODO
        return "time";
    }

    @Override
    public int compareTo(Time o) {
        return Long.compare(value * unit.getFactor(), (o.value * o.unit.getFactor()));
    }
}

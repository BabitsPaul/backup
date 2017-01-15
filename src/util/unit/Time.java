package util.unit;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Time
    implements Comparable<Time>
{
    private static final NumberFormat FORMAT_SECONDS = new DecimalFormat("00.00");
    private static final NumberFormat FORMAT_MILLISECONDS = new DecimalFormat("0.00");
    private static final NumberFormat INT_2PLACES = new DecimalFormat("##");

    //this time is NOT applicable to transform
    public static final Time NA = new Time(0, TimeUnit.SECONDS){
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

    private String representation;

    public Time(long value, TimeUnit unit)
    {
        this.value = value;
        this.unit = unit;

        representation = genRepresentation();
    }

    private String genRepresentation()
    {
        double v = value;

        switch (unit)
        {
            case NANO_SECONDS:
                if(value < 1000000)
                    return value + "ns";

                v /= 1000000;
            case MILLI_SECONDS:
                if(v < 1000)
                    return FORMAT_MILLISECONDS.format(v) + "ms";

                v /= 1000;
            case SECONDS:
                double seconds = v % 60;
                int tmp = (int) v / 60;

                int minutes = tmp % 60;
                int hours = tmp / 60;

                return hours + ":" + INT_2PLACES.format(minutes) + ":" + FORMAT_SECONDS.format(seconds);
            default:
                throw new RuntimeException("Illegal unit " + unit);
        }
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
        return representation;
    }

    @Override
    public int compareTo(Time o) {
        return Long.compare(value * unit.getFactor(), (o.value * o.unit.getFactor()));
    }
}

package util.unit;

public enum TimeUnit {
    SECONDS(1, "sec"),
    MILLI_SECONDS(1000, "ms"),
    NANO_SECONDS(1000000000, "ns");

    private int factor;

    private String name;

    TimeUnit(int factor, String name)
    {
        this.name = name;
        this.factor = factor;
    }

    public int getFactor() {
        return factor;
    }

    public String getName() {
        return name;
    }
}

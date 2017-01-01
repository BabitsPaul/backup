package copy.profiler;

public enum DataUnit
{
    SI_BYTE(1, "byte"),
    SI_KILO_BYTE(1000, "KB"),
    SI_MEGA_BYTE(1000 * 1000, "MB"),
    SI_GIGA_BYTE(1000 * 1000 * 1000, "GB");

    public static final int FACTOR = 1000;

    private int bytesPerUnit;

    private String name;

    DataUnit(int bytesPerUnit, String name)
    {
        this.name = name;
        this.bytesPerUnit = bytesPerUnit;
    }

    public int getBytesPerUnit() {
        return bytesPerUnit;
    }

    public String getName() {
        return name;
    }

    public static final DataUnit up(DataUnit unit)
    {
        switch (unit)
        {
            case SI_BYTE:       return SI_KILO_BYTE;
            case SI_KILO_BYTE:  return SI_MEGA_BYTE;
            case SI_MEGA_BYTE:  return SI_GIGA_BYTE;
            case SI_GIGA_BYTE:  return null;
            default:            throw new IllegalArgumentException("WTF???!!!");    //definitely shouldn't happen
        }
    }

    public static final DataUnit down(DataUnit unit)
    {
        switch (unit)
        {
            case SI_BYTE:       return null;
            case SI_KILO_BYTE:  return SI_BYTE;
            case SI_MEGA_BYTE:  return SI_KILO_BYTE;
            case SI_GIGA_BYTE:  return SI_MEGA_BYTE;
            default:            throw new IllegalArgumentException("WTF???!!!");    //definitely shouldn't happen
        }
    }
}
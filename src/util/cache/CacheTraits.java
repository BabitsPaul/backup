package util.cache;

import java.util.Properties;

public class CacheTraits
    extends Properties
{
    /**
     * general traits
     */
    public static final String CACHE_TYPE = "factory.cache.type";
    public static final String SPECIAL_VALUES = "cache.specialvalues";

    /**
     * windowed cache specific traits
     */
    public static final String WINDOWED_WINDOW_SIZE = "cache.windowed.size";
    public static final String WINDOWED_ELEMENT_DISTANCE = "cache.windowed.distance";
    public static final String WINDOWED_ELEMENT_DIST_DETERMINAND = "cache.windowed.distance.det";

    public CacheTraits(Object... traits)
    {
        for(int i = 0; i < traits.length ; i += 2)
            put(traits[i], traits[i + 1]);
    }

    public Object getOrDefault(Object key, CacheTraits defaults)
    {
        return containsKey(key) ? get(key) : defaults.get(key);
    }
}

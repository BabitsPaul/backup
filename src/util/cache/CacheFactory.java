package util.cache;

import java.util.Properties;

public class CacheFactory
{
    private static Properties factoryConfig = new Properties();

    static
    {
        factoryConfig.put(CacheTraits.CACHE_TYPE, CacheType.WINDOWED);
    }

    public static <T> AbstractCache<T> getNewCache(CacheTraits traits)
    {
        switch((CacheType) factoryConfig.get(CacheTraits.CACHE_TYPE))
        {
            case WINDOWED:
                return new WindowedCache<>(traits);
            default:
                throw new IllegalStateException("Invalid cache type");
        }
    }

    public enum CacheType
    {
        WINDOWED
    }
}

package copy.profiler;

import util.cache.AbstractCache;
import util.cache.CacheFactory;
import util.cache.CacheTraits;
import util.cache.SpecialValue;
import util.ui.CollectionsHelper;

import java.util.List;

public class ProfilerCache
{
    private static final String SPECIAL_MAX = "special.max";

    private AbstractCache<ProfilerDataPoint> cache;

    public ProfilerCache()
    {
        cache = CacheFactory.getNewCache(
                new CacheTraits(
                CacheTraits.CACHE_TYPE, CacheFactory.CacheType.WINDOWED,
                        CacheTraits.WINDOWED_WINDOW_SIZE, 1000,
                        CacheTraits.SPECIAL_VALUES,
                            CollectionsHelper.createMap(
                                    SPECIAL_MAX, new SpecialValue<ProfilerDataPoint>(
                                            (a, b) -> a == null ? true : a.current.compareTo(b.current) < 0)
                            )
                )
        );
    }

    public void place(ProfilerDataPoint dp)
    {
        cache.place(dp);
    }

    public ProfilerDataPoint max()
    {
        return cache.getSpecial(SPECIAL_MAX);
    }

    public List<ProfilerDataPoint> list()
    {
        return cache.list();
    }
}

package copy.profiler;

import util.cache.AbstractCache;
import util.cache.CacheFactory;
import util.cache.CacheTraits;
import util.ui.CollectionsHelper;

import java.util.List;
import java.util.function.BiFunction;

public class ProfilerCache
{
    private static final String SPECIAL_MAX = "special.max";

    private AbstractCache<ProfilerDataPoint> cache;

    public ProfilerCache()
    {
        cache = CacheFactory.getNewCache(
                new CacheTraits(
                CacheTraits.CACHE_TYPE, CacheFactory.CacheType.WINDOWED,
                        CacheTraits.WINDOWED_WINDOW_SIZE, 10,
                        CacheTraits.SPECIAL_VALUES,
                            CollectionsHelper.createMap(
                                    SPECIAL_MAX, (BiFunction<ProfilerDataPoint, ProfilerDataPoint, Boolean>) (a, b) -> a.current.compareTo(b.current) > 0
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

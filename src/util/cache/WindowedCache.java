package util.cache;

import java.util.*;

public class WindowedCache<T>
    implements AbstractCache<T>
{
    private static final CacheTraits DEFAULT_TRAITS =
            new CacheTraits(CacheTraits.WINDOWED_WINDOW_SIZE, 10000,
                                    CacheTraits.WINDOWED_ELEMENT_DISTANCE, 1,
                                    CacheTraits.WINDOWED_ELEMENT_DIST_DETERMINAND, null,
                                    CacheTraits.SPECIAL_VALUES, new HashMap<>());

    private final int maximumElements;

    private final long elementDistance;

    private final boolean keepDistance;

    private final DistanceDeterminand<T> determinand;

    private int totalElements;

    private LinkedList<T> internalCache;

    private Map<String, SpecialValue<T>> specialValues;

    public WindowedCache(CacheTraits traits)
    {
        maximumElements = (Integer) traits.getOrDefault(CacheTraits.WINDOWED_WINDOW_SIZE, DEFAULT_TRAITS);
        elementDistance = (Long) traits.getOrDefault(CacheTraits.WINDOWED_ELEMENT_DISTANCE, DEFAULT_TRAITS);
        determinand = (DistanceDeterminand<T>) traits.getOrDefault(CacheTraits.WINDOWED_ELEMENT_DIST_DETERMINAND, DEFAULT_TRAITS);
        keepDistance = (determinand != null);
        specialValues = (Map<String, SpecialValue<T>>) traits.getOrDefault(CacheTraits.SPECIAL_VALUES, DEFAULT_TRAITS);

        if(maximumElements <= 0)
            throw new IllegalArgumentException("Cache must hold at least one value");

        if(elementDistance < 0)
            throw new IllegalArgumentException("Distance between elements must be positive");

        totalElements = 0;
        internalCache = new LinkedList<>();
    }

    @Override
    public void place(T t)
    {
        //ignore items that don't belong here
        if(keepDistance && determinand.distance(internalCache.getLast(), t) < elementDistance)
                return;

        if(totalElements == maximumElements)
            internalCache.removeFirst();

        internalCache.addLast(t);

        //update all special values
        specialValues.values().forEach(sv->sv.checkVal(t));
    }

    @Override
    public List<T> list() {
        return Collections.unmodifiableList(internalCache);
    }

    @Override
    public T getSpecial(String identifier) {
        if(specialValues.containsKey(identifier))
            return specialValues.get(identifier).getValue();
        else
            throw new IllegalArgumentException("Special Value with name " + identifier  + " doesn't exist");
    }
}

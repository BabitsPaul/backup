package util.cache;

@FunctionalInterface
public interface DistanceDeterminand<T>
{
    long distance(T a, T b);
}

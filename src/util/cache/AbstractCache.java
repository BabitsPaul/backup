package util.cache;

import java.util.List;

public interface AbstractCache<T> {
    void place(T t);

    List<T> list();

    T getSpecial(String identifier);
}
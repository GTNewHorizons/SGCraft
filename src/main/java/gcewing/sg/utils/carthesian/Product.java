package gcewing.sg.utils.carthesian;

import java.util.Collections;
import java.util.Iterator;

public class Product<T> implements Iterable<T[]> {

    private final Class<T> clazz;
    private final Iterable<? extends T>[] iterables;

    Product(Class<T> clazz, Iterable<? extends T>[] iterables) {
        this.clazz = clazz;
        this.iterables = iterables;
    }

    public Iterator<T[]> iterator() {
        return (Iterator<T[]>) (this.iterables.length <= 0
                ? Collections.singletonList((Object[]) Cartesian.createArray(this.clazz, 0)).iterator()
                : new ProductIterator(this.clazz, this.iterables));
    }

}

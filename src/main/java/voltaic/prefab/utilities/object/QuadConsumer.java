package voltaic.prefab.utilities.object;

public interface QuadConsumer<K, V, S, D> {
    void accept(K k, V v, S s, D d);
}

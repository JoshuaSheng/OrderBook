import java.util.AbstractMap;

public class Tuple<K, V> extends AbstractMap.SimpleEntry<K, V>{
    public Tuple(K key, V value) {
        super(key, value);
    }
}

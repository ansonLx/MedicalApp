package anson.std.medical.dealer;

/**
 * Created by anson on 17-5-9.
 */

public interface Consumer<T> {

    void apply(T t);
}

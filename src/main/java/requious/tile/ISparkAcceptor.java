package requious.tile;

import requious.entity.EntitySpark;
import requious.entity.ISparkValue;

public interface ISparkAcceptor {
    void receive(EntitySpark spark);

    boolean canAccept(ISparkValue value);
}

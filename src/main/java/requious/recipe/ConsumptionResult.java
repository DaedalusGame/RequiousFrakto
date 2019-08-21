package requious.recipe;

import requious.data.component.ComponentBase;

public abstract class ConsumptionResult<T> {
    RequirementBase requirement;
    ComponentBase.Slot slot;
    T consumed;

    public ConsumptionResult(RequirementBase requirement, T consumed) {
        this.requirement = requirement;
        this.consumed = consumed;
    }

    public T getConsumed() {
        return consumed;
    }

    public abstract void add(T amount);

    public void setSlot(ComponentBase.Slot slot) {
        this.slot = slot;
    }

    public void consume() {
        requirement.consume(slot,this);
    }

    public static class Integer extends ConsumptionResult<java.lang.Integer> {
        public Integer(RequirementBase requirement, java.lang.Integer consumed) {
            super(requirement, consumed);
        }

        @Override
        public void add(java.lang.Integer amount) {
            consumed += amount;
        }
    }
}

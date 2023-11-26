package me.lundy.lobster.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ValueChangeDetector<T> {

    private final Timer timer;
    private final Supplier<T> valueSupplier;

    public ValueChangeDetector(Supplier<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
        this.timer = new Timer();
    }

    public void register(T initialValue, Consumer<T> action, long checkIntervalMillis) {
        this.timer.schedule(new ValueChecker(initialValue, action), 0L, checkIntervalMillis);
    }

    public void registerAndAccept(T initialValue, Consumer<T> action, long checkIntervalMillis) {
        this.timer.schedule(new ValueChecker(initialValue, action), 0L, checkIntervalMillis);
        action.accept(initialValue);
    }

    private class ValueChecker extends TimerTask {

        private final Consumer<T> action;
        private T previousValue;

        public ValueChecker(T value, Consumer<T> action) {
            this.action = action;
            this.previousValue = value;
        }

        @Override
        public void run() {
            T currentValue = valueSupplier.get();
            if (!currentValue.equals(this.previousValue)) {
                this.action.accept(currentValue);
                this.previousValue = currentValue;
            }
        }
    }

}
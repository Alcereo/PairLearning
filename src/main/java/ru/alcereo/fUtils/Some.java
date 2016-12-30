package ru.alcereo.fUtils;


class Some<T> extends Option<T> {

    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public <R> Option<R> map(Func<T, R> func) {
        return some(func.execute(value));
    }

    @Override
    public <R> Option<R> flatMap(Func<T, Option<R>> func) {
        return func.execute(value);
    }

    @Override
    public Option<T> filter(Func<T, Boolean> filterPredicate) {
        if (filterPredicate.execute(value))
            return this;
        else
            return none();
    }

    @Override
    public T getOrElse(T valueElse) {
        return value;
    }
}


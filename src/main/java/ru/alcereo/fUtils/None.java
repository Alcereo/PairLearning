package ru.alcereo.fUtils;


class None<T> extends Option<T>{

    public None() {
    }

    @Override
    public <R> Option<R> map(Func<T, R> func) {
        return none();
    }

    @Override
    public <R> Option<R> flatMap(Func<T, Option<R>> func) {
        return none();
    }

    @Override
    public Option<T> filter(Func<T, Boolean> filterPredicate) {
        return none();
    }

    @Override
    public T getOrElse(T valueElse) {
        return valueElse;
    }
}


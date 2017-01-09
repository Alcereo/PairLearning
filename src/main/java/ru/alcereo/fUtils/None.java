package ru.alcereo.fUtils;


class None<T, Es extends Exception> extends Option<T, Es> {

    @Override
    public Option map(Func func){
        return this;
    }

    @Override
    public Option flatMap(Func func) {
        return this;
    }

    @Override
    public Option filter(Func filterPredicate) {
        return this;
    }

    @Override
    public T getOrElse(T valueElse) {
        return valueElse;
    }

    @Override
    public Option _throwCausedException(){
        return this;
    }

    @Override
    public <W extends Exception> Option<T,W> _wrapNoneWithException(Exceptioned<W> exceptioned) {
        return new ExcOpt<T,W>(exceptioned.getNewException(new NullPointerException()));
    }

    @Override
    public boolean isException() {
        return false;
    }

    @Override
    public String getExceptionMessage() {
        return "";
    }

    @Override
    public Option _wrapAndTrowException(Exceptioned exceptioned){
        return this;
    }

    @Override
    public Option _wrapException(Exceptioned exceptioned) {
        return this;
    }
}


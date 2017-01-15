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
    public Option throwException(){
        return this;
    }

    @Override
    public <W extends Exception> Option<T,W> wrapNoneWithException(Exceptioned<W> exceptioned) {
        return new ExcOpt<T,W>(exceptioned.getNewException(new NullPointerException()));
    }

    @Override
    public boolean isException() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public String getExceptionMessage() {
        return "";
    }

    @Override
    public Option wrapAndTrowException(Exceptioned exceptioned){
        return this;
    }

    @Override
    public Option wrapException(Exceptioned exceptioned) {
        return this;
    }
}


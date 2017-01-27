package ru.alcereo.pairlearning.Service.TopicService;

public enum TopicPredicate {
    LEARN,
    TEACH;

    public boolean equalToName(String name){
        return this.name().equalsIgnoreCase(name);
    }

}

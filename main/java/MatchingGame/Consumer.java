package MatchingGame;

public interface Consumer<T extends Event> {
    void accept(T event);

}

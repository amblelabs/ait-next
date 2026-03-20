# ECS

Unlike a traditional ECS, we use a hybrid approach.

TARDISes (entities) contain the states they own, unlike a traditional ECS, where entities
would be purely a number (id) that would point to a column of entries in an array of components.

## Events

Events must be made and handled as results (observation) of state change.

E.g.: onArtronChanged. Must be called as a result of the artron changing, not _before_ artron change.

```java
class ArtronBehavior extends TBehavior {
    
    void setArtron(Tardis tardis, int artron) {
        ArtronState artron = tardis.resolve(ArtronState.ID);
        artron.artron = artron;
        artron.markDirty();
        
        this.handle(new ArtronChangeEvent(tardis, artron));
    }
}
```

In some cases, the event must be a filter/accumulator. In that case, event being not an observer of state change 
result is alright.

## States (Components)

Handle state and serialization. No logic besides that.

## Behaviors (Systems)

Sometimes you need to access system instances to play by the rules of events.

To do that, use the `@Resolve` annotation:

```java
class AlarmsBehavior implements TBehavior {
    
    @Resolve
    private final ArtronSystem _artron = behavior();
}
```
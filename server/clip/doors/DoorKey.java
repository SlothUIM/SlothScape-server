package server.clip.doors;

import java.util.Objects;

public class DoorKey {
    public final int id, x, y, h;

    public DoorKey(int id, int x, int y, int h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.h = h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoorKey)) return false;
        DoorKey dk = (DoorKey) o;
        return id == dk.id && x == dk.x && y == dk.y && h == dk.h;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, h);
    }
}
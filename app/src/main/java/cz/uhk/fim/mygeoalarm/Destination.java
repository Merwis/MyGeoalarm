package cz.uhk.fim.mygeoalarm;

/**
 * Created by Petr on 21. 5. 2015.
 */
public class Destination {

    private Long id;
    private String name;
    private String coordinates;
    private float radius;
    private int active;

    public Destination() {

    }

    public Destination(Long id, String name, String coordinates, float radius, int active) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.radius = radius;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}

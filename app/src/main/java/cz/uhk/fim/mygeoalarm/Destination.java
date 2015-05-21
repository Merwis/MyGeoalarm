package cz.uhk.fim.mygeoalarm;

/**
 * Created by Petr on 21. 5. 2015.
 */
public class Destination {

    private Long id;
    private String name;
    private String coordinates;
    private float radius;

    public Destination(Long id, String name, String coordinates, float radius) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.radius = radius;
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
}

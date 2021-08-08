package graphics3d.solids.voxelworld.d;

import graphics3d.Hit;
import graphics3d.Vec3;

public class Vec3Hit {

    /*
     * A simple wrapper class used to pair up hits and associated vect-
     * ors for easier retreival of material information from the model.
     */

    private Vec3 v;
    private Hit h;

    public Vec3Hit(Vec3 v, Hit h) {
        this.v = v;
        this.h = h;
    }

    public Hit h() {
        return h;
    }

    public double t() {
        return h.t();
    }

    public Vec3 v() {
        return v;
    }
}

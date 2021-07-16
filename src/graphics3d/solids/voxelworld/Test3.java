package graphics3d.solids.voxelworld;

import graphics3d.Vec3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Test3 {

    public static void main(String[] args) {

        Random rng = new Random(42);

        int lvl = 3;

        Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

        boolean[][][] rv = new boolean[dim.xInt()][dim.yInt()][dim.zInt()];

        for (int i = 0; i < dim.xInt(); i++)
            for (int j = 0; j < dim.yInt(); j++)
                for (int k = 0; k < dim.zInt(); k++)
                    rv[i][j][k] = i == j && j == k;

        Octree o = Octree.fromModel(rv);

        System.out.println("treeDepth : " + o.treeDepth());
        System.out.println("o.data().length : " + o.data().length);

        Set<Vec3> set0 = new HashSet<Vec3>();
        Set<Vec3> set1 = new HashSet<Vec3>();

        set0.add(Vec3.ZERO);

        for (int l = o.data().length - 1; l > 0; l--) {
            int unit = (int) Math.pow(2, l);

            Iterator<Vec3> it = set0.iterator();
            while (it.hasNext()) {
                Vec3 u = it.next();
                for (int n = 0; n < 8; n++) {
                    int p = n & 1;
                    int q = (n >> 1) & 1;
                    int r = (n >> 2) & 1;
                    Vec3 v = Vec3.xyz(unit*u.xInt()+p, unit*u.yInt()+q, unit*u.zInt()+r);
                    if (o.data()[l - 1][v.xInt()][v.yInt()][v.zInt()])
                        set1.add(v);
                }
            }

            set0.clear();

            it = set1.iterator();
            while (it.hasNext())
                set0.add(it.next());
            set1.clear();

            System.out.println();
        }

        set0.stream().forEach(System.out::println);
    }
}

package graphics3d.solids.voxelworld;

import graphics3d.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test2 {

    public static void main(String[] args) {

        Random rng = new Random(42);

        int lvl = 1;

        Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

        boolean[][][] rv = new boolean[dim.xInt()][dim.yInt()][dim.zInt()];

        for (int i = 0; i < dim.xInt(); i++)
            for (int j = 0; j < dim.yInt(); j++)
                for (int k = 0; k < dim.zInt(); k++)
//                    rv[i][j][k] = rng.nextDouble() < 0.85;
                    rv[i][j][k] = i == j && j == k;

        Octree o = Octree.fromModel(rv);

        System.out.println("treeDepth : " + o.treeDepth());
        System.out.println("o.data().length : " + o.data().length);

        List<Vec3> list0 = new ArrayList<Vec3>();
        List<Vec3> list1 = new ArrayList<Vec3>();

        for (int l = o.data().length - 1; l > 0; l--) {
            int count = 0;
            int unit = (int) Math.pow(2, l);
            for (int i = 0; i < o.data()[l].length; i++) {
                for (int j = 0; j < o.data()[l][i].length; j++) {
                    for (int k = 0; k < o.data()[l][i][j].length; k++)
                        if (o.data()[l][i][j][k]) {
                            System.out.printf("[%d] %d (%d, %d, %d) <-> (%d, %d, %d) - %d - %n", count++, l, i * unit, j * unit, k * unit, (i + 1) * unit, (j + 1) * unit, (k + 1) * unit, unit);
                            for (int n = 0; n < 8; n++) {
                                int p = n & 1;
                                int q = (n >> 1) & 1;
                                int r = (n >> 2) & 1;
//                                System.out.printf(" -> (%d, %d, %d) %n", unit*i+p, unit*j+q, unit*k+r );
                                Vec3 v = Vec3.xyz(unit*i+p, unit*j+q, unit*k+r);
                                list0.add(v);
                            }
                            if (l > 1) {
                                list0.clear();
                            }
                        }
                }
            }
            list0.stream().forEach(System.out::println);

            System.out.println();
        }

//        for (int l = o.data().length - 1; l >= 0; l--)
//            for (int i = 0; i < dim.xInt(); i++)
//                for (int j = 0; j < dim.yInt(); j++)
//                    for (int k = 0; k < dim.zInt(); k++)
//                        if (o.data()[l][i][j][k])
//                            System.out.println(o.data()[l][i][j][k]);

    }
}

package graphics3d.solids.voxelworld;

import graphics3d.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test1 {

    public static void main(String[] args) {

        Random rng = new Random(42);

        int lvl = 2;

        Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

        boolean[][][] rv = new boolean[dim.xInt()][dim.yInt()][dim.zInt()];

        for (int i = 0; i < dim.xInt(); i++)
            for (int j = 0; j < dim.yInt(); j++)
                for (int k = 0; k < dim.zInt(); k++) {
                    rv[i][j][k] = rng.nextDouble() < 0.85;
                    if (rv[i][j][k])
                        System.out.println(Vec3.xyz(i, j, k));
                }

        Octree o = Octree.fromModel(rv);
        boolean[][][][] b = o.data();

        for (int i = b.length - 1; i >= 0; i--)
            System.out.printf("len0 : %d, len1 : %d, len2 : %d, len3 : %d, unit : %d %n", b.length, b[i].length, b[i][0].length, b[i][0][0].length, (int) Math.pow(2, i));

        for (int i = 0; i < 8; i++) {
            System.out.printf("%d%d%d %n", i & 1, (i >> 1) & 1, (i >> 2) & 1);
        }

//        List<Vec3> list = new ArrayList<Vec3>();
//        list.add(Vec3.ZERO);
//
//        int modelLength = lvl;
//
//        for (int j = o.data()[0].length - 1; j >= 0; j--) {
//
//            System.out.println("J VALUE :: " + j);
//
//            int unit = (int) Math.pow(2, j + 1);
//            List<Vec3> newList = new ArrayList<Vec3>();
//
//            for (Vec3 v : list) {
//
//                for (int i = 0; i < 8; i++) {
//                    int p = i & 1;
//                    int q = (i >> 1) & 1;
//                    int r = (i >> 2) & 1;
//                    Vec3 u = v.mul(unit).add(Vec3.xyz(p, q, r));
//                    Vec3 w = u.add(Vec3.EXYZ);
//                    newList.add(u);
//                    System.out.println(u + " ::: " + w);
//                }
//                System.out.println();
//            }
//
//            list.clear();
//            list.addAll(newList);
//        }
    }
}

package graphics3d.solids.voxelworld;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;
import mars.geometry.Vector;

import java.net.StandardSocketOptions;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VoxOctree implements Solid {

	
	/********************************************************************
	 * 																	*
	 * ID : 0x															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/
	
	
	Octree octree;
	
	
	private VoxOctree(Octree octree) {
		this.octree = octree;
	}
	
	
	public static VoxOctree arr(boolean[][][] arr) {
		return new VoxOctree(Octree.fromModel(arr));
	}


	@Override
	public Hit firstHit(Ray ray, double afterTime) {

		Octree o = octree;
		boolean[][][][] b = o.data();

		List<Vec3> l0 = new ArrayList<Vec3>();
		List<Vec3> l1 = new ArrayList<Vec3>();

		l0.add(Vec3.ZERO);

		for (int l = b.length - 1; l > 0; l--) {

			int unit = (int) Math.pow(2, l);

			for (Vec3 vv : l0) {

				for (int i = 0; i < 8; i++) {
					int f = i & 1;
					int g = (i >> 1) & 1;
					int h = (i >> 2) & 1;

					Vec3 a = (vv.mul(2)).add(Vec3.xyz(f, g, h));
					Vec3 p = a.mul(unit);
					Vec3 d = (a.add(Vec3.EXYZ)).mul(unit);

					Hit[] hits = Box.$.pd(p, d).hits(ray);
					if (b[l - 1][a.xInt()][a.yInt()][a.zInt()] && hits.length > 0)
						l1.add(a);
				}
			}

			l0.clear();
			l0.addAll(l1);
			l1.clear();
		}

		// ovo bi trebao da dobijem - ako ovako popunim listu sve radi savrseno
		// medjutim REC JE O ISTIM VEC3 vrednostima u oba slucaja...
//		l0.clear();
//		for (int i = 0; i < 4; i++)
//			l1.add(Vec3.xyz(i, i, i));

		List<Hit> hitList = new ArrayList<Hit>();

		for (Vec3 w : l0) {

			Hit[] hits = Box.$.pd(w, Vec3.EXYZ).hits(ray);
			if (hits.length > 0) {
				if (hits[0].t() > afterTime)
				hitList.add(hits[0]);
				else if (hits[1].t() > afterTime)
				hitList.add(hits[1]);
			}
		}

		if (!hitList.isEmpty())
			return hitList.get(0);

		return Hit.POSITIVE_INFINITY;
	}

	@Override
	public Hit[] hits(Ray ray) {
		return NO_HITS;
	}
}

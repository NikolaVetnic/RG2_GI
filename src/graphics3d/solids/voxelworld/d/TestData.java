package graphics3d.solids.voxelworld.d;

import java.util.ArrayList;
import java.util.List;

import graphics3d.Color;
import graphics3d.Ray;
import graphics3d.Vec3;
import graphics3d.solids.HalfSpace;
import mars.geometry.Vector;

public class TestData {

	public static void main(String[] args) {
		
//		VoxelData vd = VoxelData.create(true, Color.WHITE);
//		
//		Vec3 d  = Vec3.xyz(1, 1, 0);
//		
//		Vec3 s0 = d.signum();
//		System.out.println("s0 " + s0);
//
//		Vec3 s1 = s0.inverse();
//		System.out.println("s1 " + s1);
//		
//		Vec3 s2 = s1.add(Vec3.EXYZ).mul(0.5);
//		System.out.println("s2 " + s2);
//		
//		System.out.println("s1.mul(Vec3.EX) " + s1.mul(Vec3.EX));
//		
//		var v0 = Vec3.xyz(4, 3, 0);
//		var ray = Ray.pd(Vec3.ZERO, Vec3.xyz(0.832, 0.555, 0));
//		var tx = HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EX)).hits(ray)[0].t();
//		var ty = HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EY)).hits(ray)[0].t();
//		System.out.println("tx " + tx + " | r(tx) " + ray.at(tx));
//		System.out.println("ty " + ty + " | r(ty) " + ray.at(ty));
//		
//		System.out.println("s0.div(ray.d()) " + s0.div(ray.d()));
		
//		System.out.println((Math.log(32) / Math.log(2) + 1) + " | " + Math.pow(2, 7));
		
//		System.out.println(1 << 5);
		
		Vec3 v = Vec3.EX;
		int unit = 1;
		
		List<Vec3> l0 = new ArrayList<>();
		for  (int idx = 0; idx < 8; idx++) {
			
			l0.add(Vec3.xyz(idx & 1, (idx >> 1) & 1, (idx >> 2) & 1));
			
			Vec3 i = l0.get(idx);		// get position of current box relative to parent box
			Vec3 pos = v.mul(2);				// base position of box hit in current level
			Vec3 currPos = pos.add(i);		// position of current box in current level

			Vec3 p = currPos.mul(unit);
			
			System.out.println("i " + i);
			System.out.println("pos " + pos);
			System.out.println("currPos " + currPos);
			System.out.println("p " + p);
			System.out.println();
		}
	}
}

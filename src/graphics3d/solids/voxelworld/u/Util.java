package graphics3d.solids.voxelworld.u;

import java.util.ArrayList;
import java.util.List;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Vec3;
import mars.geometry.Vector;

public class Util {
	
	
	public static final int DEFAULT_CHUNK_SIZE = 10;
	public static final int DEFAULT_SHIFT = 0x400 - 1;
	
	
	/**
	 * Splits the integer bitwise into a list of chunks of requested si
	 * ze.
	 * 
	 * @param int num
	 * @param int chunkSize
	 * @return List<Integer>
	 */
	
	public static List<Integer> split(int num, int chunkSize) {
		
		int shift = (int) Math.pow(2, chunkSize) - 1;
		
		List<Integer> chunks = new ArrayList<Integer>();
		
		int chunkCount = 0;
		int numLength = Integer.toBinaryString(num).length();
		
		while (chunkCount * chunkSize <= numLength)
			chunks.add((num >> chunkCount++ * chunkSize) & shift);
		
		return chunks;
	}
	
	
	/**
	 * Splits the integer bitwise into a list of chunks of requested si
	 * ze and number, padding with zeros if necessary.
	 * 
	 * @param int num
	 * @param int chunkSize
	 * @return List<Integer>
	 */
	
	public static List<Integer> splitToNumber(int num, int chunkSize, int chunkNum) {
		
		int shift = (int) Math.pow(2, chunkSize) - 1;
		
		List<Integer> chunks = new ArrayList<Integer>();
		
		for (int i = 0; i < chunkNum; i++)
			chunks.add((num >> i * chunkSize) & shift);
		
		return chunks;
	}
	
	
	/**
	 * Assembles the list of chunks of prescribed size into the origina
	 * l integer.
	 * 
	 * @param List<Integer> chunks
	 * @param int chunkSize
	 * @return int
	 */
	
	public static int assemble(List<Integer> chunks, int chunkSize) {
		
		int num = 0;
		
		for (int i = 0; i < chunks.size(); i++)
			num = num + (chunks.get(i) << (i * chunkSize));
		
		return num;
	}
	
	
	/**
	 * Packs the three integers (basically a Vec3 object) into a Vec2 o
	 * bject using the 10-bit chunks; max value per int allowed - 2^20.
	 * 
	 * @param int p
	 * @param int q
	 * @param int r
	 * @return Vector
	 */
	
	public static Vector pack(int p, int q, int r) {
		
		int[] pp = { p, q, r };
		int[] qq = { 0, 0 };
		
		for (int i = 0; i < 6; i++)
			qq[i/3] = qq[i/3] + (((pp[i/2] >> (i%2) * DEFAULT_CHUNK_SIZE) & DEFAULT_SHIFT) << ((i%3) * DEFAULT_CHUNK_SIZE));
		
		return Vector.xy(qq[0], qq[1]);
	}
	
	
	/**
	 * Packs the three integers (basically a Vec3 object) into a Vec2 o
	 * bject using the 10-bit chunks; max value per int allowed - 2^20.
	 * 
	 * @param Vec3 p
	 * @return Vector
	 */
	
	public static Vector pack(Vec3 p) {
		return pack(p.xInt(), p.yInt(), p.zInt());
	}
	
	
	/**
	 * Reconstructs the Vec3 object from a Vec2 parameter.
	 * 
	 * @param Vector q
	 * @return Vec3
	 */
	
	public static Vec3 unpack(Vector q) {
		
		int[] qq = { q.xInt(), q.yInt() };
		int[] pp = { 0, 0, 0 };
		
		for (int i = 0; i < 6; i++)
			pp[i/2] = pp[i/2] + (((qq[i/3] >> (i%3) * DEFAULT_CHUNK_SIZE) & DEFAULT_SHIFT) << ((i%2) * DEFAULT_CHUNK_SIZE));
		
		return Vec3.xyz(pp[0], pp[1], pp[2]);
	}
	
	
	public static Vec3[] getLoopData(Vec3 modelSize, Ray ray) {
		
		int lenX = modelSize.xInt(),
			lenY = modelSize.yInt(),
			lenZ = modelSize.zInt();
		
		int dx = ray.d().x() >= 0 ?  +1 : -1,
			dy = ray.d().y() >= 0 ?  +1 : -1,
			dz = ray.d().z() >= 0 ?  +1 : -1;
			
		int 		   xs = -1		, xe = -1	  , xd =  0;		
		if (dx == 1) { xs =  0		; xe = lenX   ; xd = +1; }
		else		 { xs = lenX - 1; xe = -1	  ; xd = -1; }

		int 		   ys = -1		, ye = -1	  , yd =  0;		
		if (dy == 1) { ys =  0		; ye = lenY   ; yd = +1; }
		else		 { ys = lenY - 1; ye = -1	  ; yd = -1; }

		int 		   zs = -1		, ze = -1	  , zd =  0;		
		if (dz == 1) { zs =  0		; ze = lenZ   ; zd = +1; }
		else		 { zs = lenZ - 1; ze = -1	  ; zd = -1; }
		
		return new Vec3[] { 
        		Vec3.xyz(xs, xe, xd),
        		Vec3.xyz(ys, ye, yd),
        		Vec3.xyz(zs, ze, zd),
        };
	}
	
	
	public static Vec3[] getLoopData(Vec3 size, Ray ray, Hit[] boundingBoxHits) {
			
		Vec3 vx = Vec3.xyz(
				ray.d().x() >= 0 ?  +1 : -1, 
				ray.d().y() >= 0 ?  +1 : -1, 
				ray.d().z() >= 0 ?  +1 : -1);
		
		Vec3 v0 = ray.at(boundingBoxHits[0].t()).floor();	
		Vec3 u0 = Vec3.xyz(
				v0.xInt() == size.xInt() ? -1 : (v0.xInt() == -1 ? 1 : 0), 
				v0.yInt() == size.yInt() ? -1 : (v0.yInt() == -1 ? 1 : 0), 
				v0.zInt() == size.zInt() ? -1 : (v0.zInt() == -1 ? 1 : 0));
		
		Vec3 v1 = ray.at(boundingBoxHits[1].t()).floor();		
		Vec3 u1 = Vec3.xyz(
				(v1.xInt() == size.xInt() ? -1 : (v1.xInt() == -1 ? 1 : 0)), 
				(v1.yInt() == size.yInt() ? -1 : (v1.yInt() == -1 ? 1 : 0)), 
				(v1.zInt() == size.zInt() ? -1 : (v1.zInt() == -1 ? 1 : 0)));
		
		return new Vec3[] { v0.add(u0), v1.add(u1.add(vx)), vx };
	}
}

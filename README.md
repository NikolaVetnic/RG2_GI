# Computer Graphics 2 Global Illumination Model

[![Computer Graphics 2 Gallery](http://nikolapacekvetnic.rs/wp-content/uploads/2022/04/RG2-Terrain_VallesMarineris_Showcase-20210812-1833-scaled.jpg)](http://nikolapacekvetnic.rs/?page_id=1253)

Real time ray tracing renderer written as part of Computer Graphics 2 course at Faculty of Sciences at University of Novi Sad. The accent is on VoxelWorld Solid and the optimization thereof.

**NOTE:** core renderer was implemented by course professor. 

## VoxelWorld

VoxelWorld is a set of solids used to store voxel information and means of rendering them using the project's ray tracing engine. 

The array of algorithms employed for traversing the voxel solids starts of with a simple brute force approach, then takes into consideration the direction of the tracing ray during iterrations, then switches to an approach professor and I called "grid march" (which boils down to checking only one of possible seven neighbors based on the direction vector of the ray), and finishes off with a procedure which employs octrees (octal trees) as means of storing voxel information.

## Triangular Mesh

Triangular mesh is a solid used to store information on polygonal meshes commonly used for storing geometrical data of 3D objects. The meshes can be loaded from `OBJ` files, texturing as well as surface smoothing (based on surface normals) is supported.

## Quadrics

Quadric is a solid which implements all the quadric surfaces, which themselves are a generalization of conic sections. Solids implemented include sphere, cylinder, cone, ellipsoid, paraboloid, hyperboloid and hyperbolic paraboloid.

Full showcase: [http://nikolapacekvetnic.rs/?page_id=1253](http://nikolapacekvetnic.rs/?page_id=1253)

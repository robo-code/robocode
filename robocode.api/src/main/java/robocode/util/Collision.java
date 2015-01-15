package robocode.util;

import static robocode.util.Coordinates.getAffineTransformShip;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import robocode.naval.interfaces.IProjectile;
import robocode.robotinterfaces.ITransformable;

/**
 * This class contains more advanced collision detection methods then the
 * original Robocode has. It also contains some helper methods to make the job
 * slightly easier.
 * 
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 * @since Robocode 1.9.1.2 / Naval Edition
 * @version 1.1
 */
public class Collision {
	
	/**
	 * Determines whether two (straight) lines intersect with each other.
	 * <p/>
	 * <i>This is a workaround for
	 * {@link Line2D#linesIntersect(double, double, double, double, double, double, double, double) Line2D.linesIntersect}
	 * which returns an incorrect value on certain versions of Java. This is
	 * confirmed to be at least the case in Java 5. (Status as of writing is
	 * still unresolved!)</i>
	 * 
	 * @param lineA The first of the two lines that has to intersect.
	 * @param lineB The second of the two lines that has to intersect.
	 * @return {@code true} if the lines intersect with one another; {@code false} otherwise.
	 */
	protected static final boolean intersect(Line2D.Double lineA, Line2D.Double lineB) {
		double x1 = lineA.x1, x2 = lineA.x2, x3 = lineB.x1, x4 = lineB.x2;
		double y1 = lineA.y1, y2 = lineA.y2, y3 = lineB.y1, y4 = lineB.y2;

		double dx13 = (x1 - x3), dx21 = (x2 - x1), dx43 = (x4 - x3);
		double dy13 = (y1 - y3), dy21 = (y2 - y1), dy43 = (y4 - y3);

		double dn = dy43 * dx21 - dx43 * dy21;

		double ua = (dx43 * dy13 - dy43 * dx13) / dn;
		double ub = (dx21 * dy13 - dy21 * dx13) / dn;

		return (ua >= 0 && ua <= 1) && (ub >= 0 && ub <= 1);
	}
	
	/**
	 * Generates a {@link java.awt.geom.Polygon polygon} that resembles the given
	 * {@link java.awt.geom.Rectangle2D rectangle},
	 * when the given {@link java.awt.AffineTransform transformation} is applied.
	 * @param r The rectangle on which the transformation has to take place.
	 * @param at The transformation to apply.
	 * @return A {@code Polygon} representing the transformed rectangle.
	 */
	public static final Polygon getPolygon(Rectangle2D r, AffineTransform at) {
		
		// Create the polygon object.
		Polygon p = new Polygon();
		
		// Get the polygon points after applying
		// the transformation to the rectangle.
		PathIterator i = r.getPathIterator(at);
        while (!i.isDone()) {
            double[] xy = new double[2];
                        
            int ret = i.currentSegment(xy);
                        
            // Do not include the point (0,0). (segment end)
            if (ret != PathIterator.SEG_CLOSE) {
            	p.addPoint((int)xy[0], (int)xy[1]);                
            }
                     
            i.next();
        }
		return p;
	}
	
	/**
	 * Generates a {@link java.awt.geom.Polygon Polygon} that resembles the given
	 * {@link java.awt.geom.Arc2D Arc2D},
	 * when the given {@link java.awt.AffineTransform AffineTransform} is applied.
	 * @param a The {@code Arc2D} on which the transformation has to take place.
	 * @param at The {@code AffineTransform} to apply to the {@code Arc2D}.
	 * @return A {@code Polygon} representing the transformed rectangle.
	 */
	protected static final Polygon getPolygon(Arc2D a, AffineTransform at) {
		Polygon p = new Polygon();
		
		p.addPoint((int)a.getCenterX(), (int)a.getCenterY());
		p.addPoint((int)a.getStartPoint().getX(), (int)a.getStartPoint().getY());
		p.addPoint((int)a.getEndPoint().getX(), (int)a.getEndPoint().getY());
		
		return p;
	}
	
	/**
	 * Generates a list of distinct points from the bounding box of a robot.
	 * @param peer The peer from whom to get the distinct points of its bounding box.
	 * @return {@code ArrayList<Point>} containing the distinct points
	 *         of a robot's bounding box.
	 */
	protected static final ArrayList<Point> distinct(ITransformable peer) {
		Rectangle2D r = getRectangle(peer);
		AffineTransform at = getAffineTransformShip(peer);
		Polygon poly = getPolygon(r, at);
		return distinct(poly);
	}
	
	/**
	 * Generates a list of distinct points from a {@link java.awt.Polygon polygon}.
	 * @param p The polygon from whom to get the points.
	 * @return {@code ArrayList<Point>} containing the distinct points of a polygon.
	 */
	protected static final ArrayList<Point> distinct(Polygon p) {
		ArrayList<Point> vertices = new ArrayList<Point>();
		
		// Determine the maximal possible index. (IndexOutOfRangeException)
		int minIdx = Math.min(p.xpoints.length, p.ypoints.length);
		minIdx = Math.min(minIdx, p.npoints);
		
		// Add all the points to the list.
		for (int i = 0; i < minIdx; i++) {
			Point vertex = new Point(p.xpoints[i], p.ypoints[i]);
			if (!vertices.contains(vertex)) {
				vertices.add(vertex);
			}
		}
		
		// The return size is expected to be at least 2!
		assert(vertices.size() >= 2);
		
		return vertices;
	}
	
	/**
	 * Determines whether the {@link java.awt.geom.Line2D.Double line}
	 * intersects with the {@link java.awt.Polygon polygon}.
	 * @param p The polygon that has the intersect.
	 * @param lineA The line that has to intersect the polygon.
	 * @return {@code true} when the polygon and line intersect. {@code false} otherwise.
	 */
	protected static final boolean intersectLine(Polygon p, Line2D.Double lineA) {
		ArrayList<Point> vertices = distinct(p);
		for (int i = 0; i < vertices.size(); i++) {
			Line2D.Double lineB;			
			
			if (i == 0) { // This is a special one, we have to take the first and last!
				lineB = new Line2D.Double(vertices.get(0), vertices.get(vertices.size() - 1));
			} else {
				lineB = new Line2D.Double(vertices.get(i - 1), vertices.get(i));
			}
			
			if (intersect(lineA, lineB)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines whether the {@link java.awt.geom.Line2D.Double line}
	 * is inside the polygon of the peer.
	 * @param peer The peer that should contain the line.
	 * @param line The line that should be inside the polygon.
	 * @return {@code true} when the line is inside the polygon. {@code false} otherwise.
	 */
	protected static final boolean insidePolygon(ITransformable peer, Line2D.Double line) {
		Rectangle2D r = getRectangle(peer);
		AffineTransform at = getAffineTransformShip(peer);
		Polygon p = getPolygon(r, at);
		return p.contains(line.x1, line.y1);
	}
	
	/**
	 * Determines whether two {@link java.awt.Polygon polygons} intersect with each other.
	 * @param p0 The first polygon that has to intersect.
	 * @param p1 The second polygon that has to intersect.
	 * @return {@code true} when the polygons intersect. 
	 */
	protected static final boolean intersectPolygon(Polygon p0, Polygon p1) {
		ArrayList<Point> v0 = distinct(p0);
		ArrayList<Point> v1 = distinct(p1);
		
		for (int i = 0; i < v0.size(); i++) {

			Line2D.Double lineA;
			if (i == 0) { // This is a special one, we have to take the first and last!
				lineA = new Line2D.Double(v0.get(0), v0.get(v0.size() - 1));
			} else {
				lineA = new Line2D.Double(v0.get(i - 1), v0.get(i));
			}
			
			for (int j = 0; j < v1.size(); j++) {
				Line2D.Double lineB;
				
				if (j == 0) { // This is a special one, we have to take the first and last!
					lineB = new Line2D.Double(v1.get(0), v1.get(v1.size() - 1));
				} else {
					lineB = new Line2D.Double(v1.get(j - 1), v1.get(j));
				}

				if (intersect(lineA, lineB)) {
					//Logger.logMessage("intersectPolygon");
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a HashMap with a list of Integers in it.  Only used for Ship at the moment
	 * The sides of a Ship are like so:     	  1
	 * 											/  \
	 * 										0	|  |  2
	 * 											|__|
	 * 											 3	
	 * Say we got two Ships of which Ship1 has hit Ship2 from the side (Ship1_Side1 hit Ship2_Side0)
	 * In that case this Map would return the following.
	 * 		0 : Nothing
	 * 		1 : 0       (as in: Side 0)
	 * 		2 : Nothing
	 * 		3 : Nothing
	 * Based on this Map you can conclude that Ship1 rammed Ship2 his Side0, using Side1. 
	 * @param p0  "Ship1"  It's the Polygon you will get the collision points of
	 * @param p1  "Ship2"  The Polygon colliding with p0
	 * @return A Hashmap with the Collision points of the given polygon.
	 */
	protected static final HashMap<Integer, List<Integer>> collisionPointsPolygon(Polygon p0, Polygon p1) {
		ArrayList<Point> v0 = distinct(p0);
		ArrayList<Point> v1 = distinct(p1);
		
		HashMap<Integer, List<Integer>> pointsOfIntersection = new HashMap<Integer, List<Integer>>();
		
		
		
		for (int i = 0; i < v0.size(); i++) {

			Line2D.Double lineA;
			List<Integer> list = new ArrayList<Integer>();
			
			if (i == 0) { // This is a special one, we have to take the first and last!
				lineA = new Line2D.Double(v0.get(0), v0.get(v0.size() - 1));
			} else {
				lineA = new Line2D.Double(v0.get(i - 1), v0.get(i));
			}
			
			for (int j = 0; j < v1.size(); j++) {
				Line2D.Double lineB;
				
				if (j == 0) { // This is a special one, we have to take the first and last!
					lineB = new Line2D.Double(v1.get(0), v1.get(v1.size() - 1));
				} else {
					lineB = new Line2D.Double(v1.get(j - 1), v1.get(j));
				}
				
				if (intersect(lineA, lineB)) {
					//Logger.logMessage("intersectPolygon");
					list.add(j);
				}
			}
			pointsOfIntersection.put(i, list);
		}
		
		return pointsOfIntersection;
	}
	
	/**
	 * Determines whether the {@link java.awt.geom.Rectangle2D rectangle} and
	 * {@link java.awt.geom.Line2D.Double line} intersect with each other.
	 * <p/>
	 * <i>The transformation of the rectangle will be applied before
	 * determining whether the rectangle and line intersect!</i>
	 * @param r The rectangle that has to intersect with the line.
	 * @param at The transformation that has to be applied to the rectangle.
	 * @param line The line that has to intersect with the rectangle.
	 * @return {@code true} when the rectangle and line intersect.
	 */
	protected static final boolean intersect(Rectangle2D r, AffineTransform at, Line2D.Double line) {
		Polygon p = getPolygon(r, at);
		
		if (p.contains(line.getP1()))
			return true;
		
		return intersectLine(p, line);
	}
	
	/**
	 * Determines whether two {@link java.awt.geom.Rectangle2D rectangles}
	 * intersect with each other.
	 * @param r0 The first rectangle that has to intersect.
	 * @param at0 The transformation of the first rectangle.
	 * @param r1 The second rectangle that has to intersect.
	 * @param at1 The transformation of the second rectangle.
	 * @return {@code true} when the two rectangles intersect; {@code false} otherwise.
	 */
	protected static final boolean intersect(Rectangle2D r0, AffineTransform at0, Rectangle2D r1, AffineTransform at1) {
		Polygon p0 = getPolygon(r0, at0);
		Polygon p1 = getPolygon(r1, at1);

		return intersectPolygon(p0, p1);
	}
	
	/**
	 * Determines whether two robots collided.
	 * @param p0 The first robot that has to collide.
	 * @param p1 The second robot that has to collide.
	 * @return {@code true} when the robots collide. {@code false} otherwise.
	 */
	public static final boolean collide(ITransformable p0, ITransformable p1) {
		
		Rectangle2D r0 = getRectangle(p0);
		Rectangle2D r1 = getRectangle(p1);
		
		AffineTransform at0 = getAffineTransformShip(p0);
		AffineTransform at1 = getAffineTransformShip(p1);
		
		return intersect(r0, at0, r1, at1);
	}
	
	/**
	 * Returns a List with all the points of collision. This function is used to check where a Ship got hit and by which side.
	 *  * The sides of a Ship are like so:     	  1
	 * 											/  \
	 * 										0	|  |  2
	 * 											|__|
	 * 											 3	
	 * With this Map you can ask for each side, by which sides of the other Ship you got hit.
	 * @param p0 The Peer we want to check the Collision of
	 * @param p1 The Peer that collides with the above peer
	 * @return
	 */
	public static Map<Integer, List<Integer>> getCollisionPoints(ITransformable p0, ITransformable p1){
		Rectangle2D r0 = getRectangle(p0);
		Rectangle2D r1 = getRectangle(p1);
		
		AffineTransform at0 = getAffineTransformShip(p0);
		AffineTransform at1 = getAffineTransformShip(p1);
		
		Polygon pl0 = getPolygon(r0, at0);
		Polygon pl1 = getPolygon(r1, at1);
		
		return collisionPointsPolygon(pl0, pl1);
	}
	
	/**
	 * Determines whether or not the peer is inside the shape.
	 * @param a The scan arc of the ship's radar.
	 * @param peer The peer that may be spotted by the radar.
	 * @return {@code true} when the ship is inside the arc. {@code false} otherwise.
	 */
	public static final boolean insideScan(Arc2D a, ITransformable peer) { // Both have multiple points!
		Polygon p = getPolygon(peer);
		ArrayList<Point> vertices = distinct(p);
		
		// 1. Check whether the points are inside the shape.
		for (Point vertex: vertices) {
			if (a.contains(vertex)) {
				return true;
			}
		}
		
		// 2. When the points are not inside we are going to intersect it!
		for (int i = 0; i < vertices.size(); i++) {
			Line2D line;		
			if (i == 0) {
				line = new Line2D.Double(vertices.get(0), vertices.get(vertices.size() - 1));
			} else {
				line = new Line2D.Double(vertices.get(i - 1), vertices.get(i));
			}
			
			if (/* A */line.intersectsLine(a.getCenterX(), a.getCenterY(), a.getStartPoint().getX(), a.getStartPoint().getY()) ||
				/* B */line.intersectsLine(a.getCenterX(), a.getCenterY(), a.getEndPoint().getX(), a.getEndPoint().getY()) ||
				/* C */line.intersectsLine(a.getStartPoint().getX(), a.getStartPoint().getY(), a.getEndPoint().getX(), a.getEndPoint().getY())) {
				return true;
			}
			
		}
		
		return false;
	}
	
	/**
	 * Determines whether or not the projectile is inside the scan arc.
	 * @param a The scan arc that should scan the projectile.
	 * @param projectile The projectile that has to be scanned.
	 * @return {@code true} if the projectile got scanned; {@code false} otherwise.
	 */
	public static final boolean insideScan(Arc2D a, IProjectile projectile) {
		return a.contains(new Point2D.Double(projectile.getX(), projectile.getY()));
	}
	
	/**
	 * Determines whether the robot collides
	 * with a {@link java.awt.geom.Line2D.Double Line2D.Double}.
	 * @param p The peer that has to collide.
	 * @param l The line that has to collide with the peer.
	 * @return {@code true} when the peer and the line collide. {@code false} otherwise.
	 */
	public static final boolean collide(ITransformable p, Line2D.Double l) {
		Rectangle2D r = getRectangle(p);
		AffineTransform at = getAffineTransformShip(p);
		return intersect(r, at, l) || insidePolygon(p, l);
	}
	
	/**
	 * Determines whether the {@link java.awt.geom.Line2D.Double Line2D.Double}
	 * intersect with each other.
	 * @param lineA The first line that has to intersect.
	 * @param lineB The second line that has to intersect.
	 * @return {@code true} when the lines intersect. {@code false} otherwise.
	 */
	public static final boolean collide(Line2D.Double lineA, Line2D.Double lineB) {
		return intersect(lineA, lineB);
	}
	
	/**
	 * THOMA: STILL NEEDS A FEW JUNIT TESTS
	 * Collision method that is mainly used for mines. 
	 * Checks whether a Rectangle (standing up straight) collides with a turning rectangle (like that of a ship)
	 * @param p
	 * @param rectangle
	 * @return
	 */
	public static final boolean collide(ITransformable p, Rectangle2D rectangle){
		Polygon polygonTransformable = getPolygon(getRectangle(p), getAffineTransformShip(p));
        Polygon polygonRectangle = getPolygon(rectangle, new AffineTransform());
//		return intersectPolygon(polygonTransformable, rectangleToPolygon(rectangle)) || insidePolygon(polygonTransformable, rectangleToPolygon(rectangle));
		return intersectPolygon(polygonTransformable, polygonRectangle) || insidePolygon(polygonTransformable, polygonRectangle);

	}
	
	public static boolean insidePolygon(Polygon p0, Polygon p1){
		boolean inside = false;
		for(Point p : distinct(p1)){
			if(p0.contains(p)){
				inside = true;
			}
		}
		return inside;
	}
	
	public static Polygon rectangleToPolygon(Rectangle2D rectangle){
		Polygon result = new Polygon();
		result.addPoint((int)rectangle.getX(), (int)rectangle.getY());
		result.addPoint((int)(rectangle.getX() + rectangle.getWidth()), (int)rectangle.getY());
		result.addPoint((int)(rectangle.getX() + rectangle.getWidth()), (int)(rectangle.getY() + rectangle.getHeight()));
		result.addPoint((int)rectangle.getX(), (int)(rectangle.getX() + rectangle.getHeight()));
		return result;
	}



	// ===============================================
	//  Helper methods 
	// ===============================================
	
	/**
	 * Get the bounding box rectangle for the specified robot.
	 * @param peer The robot from whom to get the bounding box rectangle.
	 * @return Bounding box {@code Rectangle2D}
	 */
	public static final Rectangle2D getRectangle(ITransformable peer) {
		
		// Get the standard rectangle. (Tank rectangle!)
		Rectangle2D rect = peer.getBoundingBox();
		
		return rect;
	}
	
	/**
	 * Get the polygon of a peer.
	 * @param peer The peer to get the polygon from.
	 * @return The polygon of the peer.
	 */
	public static final Polygon getPolygon(ITransformable peer) {
		Rectangle2D r = getRectangle(peer);
		AffineTransform at = getAffineTransformShip(peer);
		return getPolygon(r, at);
	}
}

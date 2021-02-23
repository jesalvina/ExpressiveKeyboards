package gesturelogger.model;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class CharacterPath {
	private ArrayList<ArrayList<Point>> points;
	private Path [] paths;
	private int pathIndex;
	private char character;
	public int nbpoints, cardinal;
	private Rect bound = new Rect(); //left, top, right, bottom
	
	
	/**
	 * 
	 * @param c
	 */
	public CharacterPath ( char c, int Ymin ) {
		this( c, Ymin, CardinalSpline.NPOINTS, CardinalSpline.LENGTH_TANGENT );
	}

	
	/**
	 * 
	 * @param c
	 * @param nbpoints
	 * @param cardinal
	 */
	public CharacterPath ( char c, int Ymin, int nbpoints, int cardinal ) {
		this.character = c;
		this.nbpoints = nbpoints;
		this.cardinal = cardinal;
		points = new ArrayList<ArrayList<Point>>();
		
		bound.top = Ymin;
	}
	
	
	public CharacterPath ( CharacterPath cp ) {
		this.character = cp.getCharacter();
		this.nbpoints = cp.nbpoints;
		this.cardinal = cp.cardinal;
		this.bound = new Rect(cp.getBoundingBox());
		
		points = new ArrayList<ArrayList<Point>>();
		ArrayList<Point> l;
		for ( int i=0; i<cp.getPoints().size();i++ ) {
			points.add(new ArrayList<Point>());	//destination
			l = cp.getPoints().get(i);			//source
			
			//copy
			for ( int j=0; j<l.size(); j++ ) {
				points.get(i).add( new Point( l.get(j) ) );
			}
		}
	}
	
	
	/**
	 * to add a new point to the path
	 * override while registering the new point
	 * @param x
	 * @param y
	 */
	public void draw ( float x, float y ) {
		if ( points.isEmpty() ) {
			points.add(new ArrayList<Point>());
		}
		points.get(pathIndex).add(new Point((int)x,(int)y));
	}
	
	public void detach () {
		//for the first time, don't do anything
		if ( points.isEmpty() ) {
			pathIndex = 0;
		}
		//if the last spline is not empty, then detach
		else if ( points.size() == (pathIndex+1) && !points.get(pathIndex).isEmpty() ) {
			points.add(new ArrayList<Point>());
			pathIndex++;
		}
	}
	
	
	/**
	 * to reset the path
	 * override while removing all points
	 * @param x
	 * @param y
	 */
	public void reset() {
		paths = null;
		points.clear();
		pathIndex = 0;
	}
	
	
	public char getCharacter() {
		return character;
	}
	
	
	/**
	 * 
	 * @param s
	 * @param index
	 * @return
	 */
	public void generatePath () {
		generatePath( this.nbpoints, this.cardinal );
	}
	
	
	/**
	 * 
	 * @param s
	 * @param index
	 * @param npoints
	 * @param cardinal
	 * @return
	 */
	public void generatePath ( int npoints, int cardinal ) {
		paths = new Path[points.size()];
		for ( int i=0; i < points.size(); i++ ) {
			if ( points.get(i).size() >= 2 ) {
				paths[i] = Constant.SPLINE.create( points.get(i), npoints, cardinal );
			}
		}
		//path = Constant.SPLINE.create( points.get(index), npoints, 8 );
		//this.nbpoints = npoints;
		//this.cardinal = cardinal;
	}
	
	
	/**
	 * 
	 * @param s
	 * @param index
	 * @return
	 */
	public Path[] getPath () {
		return paths;
	}
	
	
	/**
	 * 
	 * @param s
	 * @param index
	 * @return
	 */
	public Path getPath ( int index ) {
		return paths[index];
	}
	
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Point>> getPoints () {
		return points;
	}
	
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public ArrayList<Point> getPoints ( int index ) {
		return points.get(index);
	}
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getPoint( int x, int y ) {
		Point p;
		for ( int i=0; i<points.size(); i++ ) {
			for ( int j=0; j<points.size(); i++ ) {
				p = points.get(i).get(j);
				if ( Math.abs(x - p.x) <= Constant.CONTROLPOINT_RAD && Math.abs(y - p.y ) <= Constant.CONTROLPOINT_RAD ) {
					return p;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * to remove the last dot
	 * @return
	 */
	public Point undot() {
		Point p = null;
		//IF there is at least one point in the last spline
		if ( !points.isEmpty() && !points.get(pathIndex).isEmpty() ) {
			p = points.get(pathIndex).remove(points.get(pathIndex).size()-1);
			//if after removal, there's no point in the last spline anymore, then move the pointer to the previous spline
			if ( points.get(pathIndex).isEmpty() ) {
				points.remove(pathIndex);
				pathIndex = ( pathIndex == 0 ? 0 : pathIndex-1 );
			}
		}
		//ELSE move to the previous spline then remove the last point
		else if ( !points.isEmpty() && pathIndex > 0 ) {
			points.remove(pathIndex);
			pathIndex--;
			p = points.get(pathIndex).remove(points.get(pathIndex).size()-1);
		}
		System.out.println("UNDOT; now on spline "+pathIndex);
		return p;
	}
	
	
	public Rect getBoundingBox () {
		int xmin=100000, xmax=-1;
		ArrayList<Point> l;
		//get the most left & most right point.x
		for ( int i=0; i<points.size();i++ ) {
			l = points.get(i);
			for ( int j=0; j<l.size(); j++ ) {
				if ( xmin > l.get(j).x ) {
					xmin = l.get(j).x;
				}
				
				if ( xmax < l.get(j).x ) {
					xmax = l.get(j).x;
				}
			}
		}
		bound.left = xmin;
		bound.right = xmax;
		
		return bound;
	}
	
	
	public void setTop ( int Ymin ) {
		bound.top = Ymin;
	}
		
	
	/**
	 * to get a string of the character path
	 * (char);(number_of_point);(cardinality);(topLeft-Y);(spline1);(spline2);...
	 *        where spline is x1,y1,x2,y2,...
	 */
	public String toString() {
		String s = this.character + ";" + this.nbpoints + ";" + this.cardinal + ";" + 
					bound.top + ";";
		
		ArrayList<Point> spline;
		Point p;
		for ( int i=0; i < points.size(); i++) {
			spline = points.get(i);
			for ( int j=0; j < spline.size(); j++ ) {
				p = spline.get(j);
				s += p.x + "," + p.y;
				
				//for the last index, don't put a comma
				if ( j != (spline.size()-1) ) {
					s += ",";
				}
			}
			//for the last index, don't put a semi-colon
			if ( i != (points.size()-1) ) {
				s += ";";
			}
		}
		
		return s;
	}
	
	
}

package gesturelogger.model;


import java.awt.*;
import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.Point;

/**
 * http://www.antonioshome.net/attic/splines/
 * @author jalvina
 *
 */
public class CardinalSpline {
	
	/**
	 * Increment NPOINTS for better resolution (lower performance).
	 */
	//public static final int NPOINTS = 10, CARDINAL = 5; //DEFAULT
	public static final int NPOINTS = 10, LENGTH_TANGENT = 5; //DEFAULT

	private double[] B0;
	private double[] B1;
	private double[] B2;
	private double[] B3;

	
	/**
	 * 
	 */
	private void initialize( int npoint )
	{
		B0 = new double[ npoint ];
		B1 = new double[ npoint ];
		B2 = new double[ npoint ];
		B3 = new double[ npoint ];
		
		double deltat = 1.0 / (npoint-1);
		double t = 0.0;
		double t1, t12, t2 = 0.0;
		for( int i=0; i<npoint; i++ ){
			t1 = 1-t;
			t12 = t1*t1;
			t2 = t*t;
			B0[i] = t1*t12;
			B1[i] = 3*t*t12;
			B2[i] = 3*t2*t1;
			B3[i] = t*t2;
			t+=deltat;
		}

	}
	
	
	/**
	 * 
	 * @param points
	 * @param npoints
	 * @param cardinal
	 * @return
	 */
	public Path create( ArrayList<Point> points, int npoints, int cardinal )
	{
		initialize( npoints );
		return this.generatePath( points, npoints, cardinal );
	}


	/**
	 * Creates a GeneralPath representing a curve connecting different
	 * points.
	 * @param points the points to connect (at least 3 points are required).
	 * @return a GeneralPath that connects the points with curves.
	 */
	public Path create( ArrayList<Point> points )
	{
		initialize( NPOINTS );
		return this.generatePath( points, NPOINTS, LENGTH_TANGENT );
	}
	
	
	/**
	 * 
	 * @param points
	 * @param npoints
	 * @param lengthTangent
	 * @return
	 */
	private Path generatePath ( ArrayList<Point> points, int npoints, int lengthTangent ) {
		Path path = new Path(); //the output
		
		if ( points.size() == 2 ){
			path.moveTo( points.get(0).x, points.get(0).y );
			path.lineTo( points.get(1).x, points.get(1).y );
		}
		else if ( points.size() > 2 ) {
			int n = points.size();
			Point [] p = new Point[ n + 2 ];
			System.arraycopy( points.toArray(), 0, p, 1, n );
			
			p[0] = new Point( 2*points.get(0).x - 2*points.get(1).x + points.get(2).x,
							  2*points.get(0).y - 2*points.get(1).y + points.get(2).y );
			p[n+1] = new Point( 2*p[n-2].x - 2*p[n-1].x + p[n].x,
								2*p[n-2].x - 2*p[n-1].x + p[n].x );
	
			path.moveTo( p[1].x, p[1].y );
			for( int i=1; i<p.length-2; i++ )
			{
				for( int j=0; j<npoints; j++ )
				{
					double x = p[i].x * B0[j]
									 + (p[i].x+(p[i+1].x-p[i-1].x)/lengthTangent)*B1[j]
									 + (p[i+1].x-(p[i+2].x-p[i].x)/lengthTangent)*B2[j]
									 + (p[i+1].x*B3[j]);
					double y = p[i].y * B0[j]
									 + (p[i].y+(p[i+1].y-p[i-1].y)/lengthTangent)*B1[j]
									 + (p[i+1].y-(p[i+2].y-p[i].y)/lengthTangent)*B2[j]
									 + (p[i+1].y*B3[j]);
					path.lineTo( (float)x, (float)y );
				}
			}
		}
		return path;
	}
}

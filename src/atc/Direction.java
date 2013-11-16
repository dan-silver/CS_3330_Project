//Copyright (C) 2003 Zheli Erwin Yu
//
//This file is part of ATCJ.
//
//ATCJ is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//ATCJ is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with ATCJ; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package atc;

import java.awt.Point;
import java.lang.Math;
import java.lang.String;
import java.lang.Character;

/**
 * Direction type, represented by nomalized (dx. dy ).
 */
public class Direction extends Point
{
  protected void normalize()
  {
    if ( x != 0 ) x = x>0 ? 1: -1;
    if ( y != 0 ) y = y>0 ? 1: -1;
  }

  public Direction( int a, int b ) { super( a, b ); normalize(); }
  public Direction( Direction d ) { super( (Point)d ); normalize(); }

  public Direction( Position from, Position to )
  {
    super();
    int dx, dy;
    dx = to.x - from.x;
    dy = to.y - from.y;
    // 0.4 ~= tan(pi/8)
    if ( 0.4*Math.abs(dx) > Math.abs(dy) ) dy = 0;
    if ( 0.4*Math.abs(dy) > Math.abs(dx) ) dx = 0;

    x = dx; y = dy;
    normalize();
  }

  public Direction getReverse()
  {
    return new Direction( -x, -y );
  }

  public void tick( Turn t )
  {
    if( t == null ) return;
    int new_x, new_y;
    new_x = t.matrix[0][0]*x + t.matrix[0][1]*y;
    new_y = t.matrix[1][0]*x + t.matrix[1][1]*y;
    x = new_x; y = new_y;
    normalize();
  }

  public String getDirName()
  {
    String ret_name = new String("");
    if( y != 0 )
      ret_name += y>0? "S" : "N";
    if( x != 0 )
      ret_name += x>0? "E" : "W";
    return ret_name;
  }

  public static Direction charToDir( char c )
  {
    switch ( Character.toLowerCase(c) )
    {
      case 'a': return new Direction( -1, 0 );
      case 'w': return new Direction( 0, -1 );
      case 'd': return new Direction( 1, 0 );
      case 'x': return new Direction( 0, 1 );
      case 'q': return new Direction( -1, -1 );
      case 'e': return new Direction( 1, -1 );
      case 'c': return new Direction( 1, 1 );
      case 'z': return new Direction( -1, 1 );
    }
    return null;
  }
};

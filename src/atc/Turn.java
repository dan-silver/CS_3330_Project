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

/**
 * Turn type, represented by the turn matrix.
 */
public class Turn
{
  public int matrix[][] = new int[2][2];

  public Turn( int a, int b, int c, int d )
  {
    matrix[0][0] = a;
    matrix[0][1] = b;
    matrix[1][0] = c;
    matrix[1][1] = d;
  }

  public Turn( Turn t )
  {
    int x, y;
    for( x=0; x<2; x++ )
      for( y=0; y<2; y++ )
        matrix[x][y] = t.matrix[x][y];
  }

  public boolean equals( Object t )
  {
    if ( ! (t instanceof Turn) )
      return false;
    return ((Turn)t).matrix[0][0] == matrix[0][0] &&
           ((Turn)t).matrix[0][1] == matrix[0][1] &&
           ((Turn)t).matrix[1][0] == matrix[1][0] &&
           ((Turn)t).matrix[1][1] == matrix[1][1] ;
  }

  public int hashCode()
  {
    int result = matrix[0][0];
    result = result*7 + matrix[0][1];
    result = result*7 + matrix[1][0];
    result = result*7 + matrix[1][1];
    return result;
  }

  //////////////////////
  // turn towards, using four basic turns.
  public static Turn turnTowards ( Direction from, Direction to )
  {
    int dx, dy;
    dx = from.x*to.x + from.y*to.y;
    dy = from.x*to.y - from.y*to.x;

    if( dx>=0 && dy==0 ) return null;
    if( dx>0 )
      return dy>0 ? new Turn(SMALL_RIGHT) : new Turn(SMALL_LEFT);
    return dy>0 ? new Turn(RIGHT) : new Turn(LEFT);
  }

  public static Turn charToTurn ( char c )
  {
    switch(c)
    {
      case 'r': return new Turn(SMALL_RIGHT);
      case 'l': return new Turn(SMALL_LEFT);
      case 'R': return new Turn(RIGHT);
      case 'L': return new Turn(LEFT);
    }
    return null;
  }

  public static Turn SMALL_RIGHT = new Turn( 1, -1, 1, 1 );
  public static Turn SMALL_LEFT = new Turn( 1, 1, -1, 1 );
  public static Turn RIGHT = new Turn( 0, -1, 1, 0 );
  public static Turn LEFT = new Turn( 0, 1, -1, 0 );
};

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

/**
 * Position type, represented by (x, y).
 */
public class Position extends Point
{
  public Position() { super(); }
  public Position( Position p ) { super( (Point)p ); }
  public Position( int a, int b ) { super( a, b ); }

  public void tick( Direction dir )
  {
    if( dir == null ) return;
    x += dir.x;
    y += dir.y;
  }

  public boolean isColliding( Position pos )
  {
    if( pos == null ) return false;
    return Math.abs( pos.x - x ) <=1 && Math.abs( pos.y - y ) <= 1;
  }
};

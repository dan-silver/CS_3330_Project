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

import java.lang.Object;

/**
 * ATCObj that moves, with direction, speed.
 */
public class MovingObj extends ATCObj
{
  public Direction dir = null;
  public int inv_speed = 1;
  public int speed_count = 0;
  public int tick_count = 0;

  public MovingObj() { super(); }
  public MovingObj( MovingObj ao ) 
    { 
      super( (ATCObj)ao ); 
      if( ao != null )
      {
        inv_speed = ao.inv_speed;
        speed_count = ao.speed_count;
        if( ao.dir != null )
          dir = new Direction( ao.dir );
      }
    }

  public MovingObj( Position p, Direction d, int altitude, int i_speed )
  {
    super();
    if( p != null )
      pos = new Position( p );
    if( d != null )
      dir = new Direction( d );
    alt = altitude;
    inv_speed = i_speed;
  }
};


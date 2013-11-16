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
 * ATCObj that does not move, with (entry) direction and exit direction/altitude.
 */
public class StaticObj extends ATCObj
{
  public Direction dir = null;
  public Direction exit_dir = null;
  public int exit_alt = 9;

  protected StaticObj() { super(); }
  public StaticObj( StaticObj ao ) 
  { 
    super( (ATCObj)ao ); 
    if( ao.dir != null )
    {
      dir = new Direction( ao.dir );
      exit_dir = new Direction( ao.dir );
    }
  }

  protected StaticObj ( Position p )
  {
    super();
    alt = 0;
    if( p != null )
      pos = new Position( p );
  }

  public StaticObj ( Position p, Direction d )
  {
    super();
    alt = 0;
    if( p != null )
      pos = new Position( p );
    if( d != null )
    {
      dir = new Direction( d );
      exit_dir = new Direction( d );
    }
  }
  
  public String getName()
  {
    return new String("O") + id;
  }
};


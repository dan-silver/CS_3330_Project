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

public class Line extends StaticObj
{
  public Position second_end = null;

  public Line() { super(); }
  public Line( Line ao ) 
    { 
      super( (StaticObj)ao ); 
      if( ao.second_end != null )
        second_end = new Position( ao.second_end );
    }

  public Line( Position p1, Position p2 )
  {
    super( p1 );
    if( p2 != null )
      second_end = new Position( p2 );
  }
};


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

public class Airfield extends StaticObj
{
  protected Airfield() { super(); }
  public Airfield( Airfield ao ) 
  { 
    super( (StaticObj)ao ); 
    alt = 0;
    exit_alt = 0;
  }

  public Airfield( Position p, Direction d )
  {
    super( p, d );
    alt = 0;
    exit_alt = 0;
  }
  
  public String getName()
  {
    return new String("A") + id;
  }
};


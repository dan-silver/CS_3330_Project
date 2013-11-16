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

public class Exit extends StaticObj
{
  protected Exit() { super(); alt = 9; }
  public Exit( Exit ao ) 
  { 
    super( (StaticObj)ao ); 
    alt = 7;
    exit_alt = 9;
  }

  public Exit( Position p, Direction d )
  {
    super( p, d );
    alt = 7;
    exit_alt = 9;
    exit_dir = null; // We don't care which direction you are exiting, yet.
  }
  
  public String getName()
  {
    return new String("E") + id;
  }
};


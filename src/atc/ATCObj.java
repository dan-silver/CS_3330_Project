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
 * Base class of all objects in the radar area, has id, altitude, position.
 */
public class ATCObj extends Object
{
  public boolean changed_flag = false;
  public int id;
  public int alt = 0;
  public Position pos = null;

  public ATCObj() { super(); }
  public ATCObj( ATCObj ao ) 
    { 
      super(); 
      changed_flag = ao.changed_flag;
      id = ao.id;
      alt = ao.alt;
      if( ao.pos != null )
        pos = new Position ( ao.pos );
    }
};

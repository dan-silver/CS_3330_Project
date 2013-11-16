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

public class TurnCommand extends DIRCommand
{
  public Direction dir = null;

  public TurnCommand() { super(); }
  public TurnCommand( TurnCommand tc )
  {
    super( (DIRCommand)tc );
    if ( tc.dir != null )
      dir = new Direction( tc.dir );
  }
  public TurnCommand( Direction d )
  {
    super();
    if ( d != null )
      dir = new Direction( d );
    active_flag = true;
  }
  public TurnCommand( Direction d, Position p )
  {
    super(p);
    if ( d != null )
      dir = new Direction( d );
    active_flag = false;
  }
};

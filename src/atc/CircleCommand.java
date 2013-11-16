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

public class CircleCommand extends DIRCommand
{
  public Turn turn = null;

  public CircleCommand() { super(); turn=Turn.LEFT; }
  public CircleCommand( CircleCommand cc )
  {
    super( (DIRCommand)cc );
    if ( cc.turn != null )
      turn = new Turn( cc.turn );
  }
  public CircleCommand( Turn t )
  {
    super();
    if ( t != null )
      turn = new Turn( t );
    active_flag = true;
  }
  public CircleCommand( Turn t, Position p )
  {
    super(p);
    if( t != null )
      turn = new Turn( t );
    active_flag = false;
  }
};

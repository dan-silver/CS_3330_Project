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
import java.lang.String;

/**
 * "View" implementation for debugging only.
 */
public class ATCUI_dummy_impl extends Object implements ATCUI
{
  protected ATC atc_obj = null;
  protected ATCUI_dummy_impl() { super(); }
  public ATCUI_dummy_impl( ATC a )
  {
    super();
    atc_obj = a;
  }
  
  public void initUI( int dx, int dy )
  {
    System.out.println( "Demension is " + dx + "x" + dy );
  }

  public void StaticObjNew( StaticObj so )
  {
    // Temporary
    if( so instanceof Beacon )
    {
      System.out.println( "New: Beacon #" + so.id + 
          " pos=[" + so.pos.x + ":" + so.pos.y + "]" );
      return;
    }
    if( so instanceof Airfield )
    {
      System.out.println( "New: Airfield #" + so.id + 
          " pos=[" + so.pos.x + ":" + so.pos.y + "]" );
      return;
    }
    if( so instanceof Exit )
    {
      System.out.println( "New: Exit #" + so.id + 
          " pos=[" + so.pos.x + ":" + so.pos.y + "]" );
      return;
    }
    if( so instanceof Line )
    {
      System.out.println( "New: Line #" + so.id );
      return;
    }
  }

  public void CommandUpdate( String cmd_str )
  {
    System.out.println( "Set: Command String: " + cmd_str );
  }

  public void PlaneNew( Plane p )
  {
    System.out.println( "New: Plane #" + p.getIdChar() + 
        " pos=[" + p.pos.x + ":" + p.pos.y + "] alt=" + p.alt +
        " dir=" + p.dir.getDirName() + " ispd=" + p.inv_speed );
  }

  public void PlaneUpdate( Plane p )
  {
    System.out.println( "Update: Plane #" + p.getIdChar() + 
        " pos=[" + p.pos.x + ":" + p.pos.y + "] alt=" + p.alt +
        " dir=" + p.dir.getDirName());
  }

  public void PlaneRemove( Plane p )
  {
    System.out.println( "Remove: Plane #" + p.getIdChar() );
  }

  public void InfoUpdate( int tick_count, int safe_count ) {}

  public void ready(){}
  public void start(){}
  public void gameOver( String s ){}
  public void refresh(){}
  public void close(){}
};

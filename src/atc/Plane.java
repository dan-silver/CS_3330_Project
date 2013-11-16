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

public class Plane extends MovingObj
{
  public boolean waiting_flag = false;
  public boolean takeoff_flag = true;
  public ALTCommand alt_cmd = null;
  public DIRCommand dir_cmd = null;
  public StaticObj destination = null;

  protected Plane() { super(); }
  public Plane( Plane ao ) 
    { 
      super( (MovingObj)ao ); 
      waiting_flag = ao.waiting_flag;
      takeoff_flag = ao.takeoff_flag;
      destination = ao.destination;
    }

  public Plane
    ( Position p, Direction d, int altitude, int i_speed, StaticObj des )
  {
    super( p, d, altitude, i_speed );
    if( altitude == 0 )
    {
      waiting_flag = true;
      takeoff_flag = false;
    }
    destination = des;
  }

  public void setCommand( Command c )
  {
    if( c instanceof ALTCommand )
    {
      alt_cmd = (ALTCommand)c;
      waiting_flag = false;
    }
    else
      if( c instanceof DIRCommand )
        dir_cmd = (DIRCommand)c;
  }

  public void clearALTCommand() { alt_cmd = null; }
  public void clearDIRCommand() { dir_cmd = null; }

  protected void processALTCommand()
  {
    if( alt_cmd == null || !alt_cmd.active_flag ) return;
    if( alt == alt_cmd.alt )
    {
      clearALTCommand();
      return;
    }
    alt = alt_cmd.alt > alt ? alt+1 : alt-1;
    if( alt == alt_cmd.alt )
      clearALTCommand();
    if( alt > 0 && !takeoff_flag )
      takeoff_flag = true;
  }

  protected void processDIRCommand()
  {
    if( dir_cmd == null ) return;
    if( !dir_cmd.active_flag )
      if( pos.equals( dir_cmd.pos ) )
        dir_cmd.active_flag = true;
      else
        return;

    if( dir_cmd instanceof TurnCommand )
    {
      Direction new_dir = ((TurnCommand)dir_cmd).dir;
      if( new_dir == null ) return;
      if( dir.equals( new_dir ) )
      {
        clearDIRCommand();
        return;
      }
      Turn turn = Turn.turnTowards( dir, new_dir );
      dir.tick( turn );
      if( dir.equals( new_dir ) )
        clearDIRCommand();
    }

    if( dir_cmd instanceof CircleCommand )
    {
      Turn turn = ((CircleCommand)dir_cmd).turn;
      if( turn == null ) return;
      dir.tick( turn );
    }
  }

  public synchronized void tick()
  {
    if( waiting_flag )
    {
      changed_flag = false;
      return;
    }
    if( ++speed_count < inv_speed )
    {
      changed_flag = false;
      return;
    }
    else
    {
      speed_count = 0;
      tick_count++;
      changed_flag = true;
      processDIRCommand();
      pos.tick(dir);
      processALTCommand();
    }
  }

  public char getIdChar()
  {
    if( inv_speed == 1 )
      return (char)('a'+id);
    else
      return (char)('A'+id);
  }

  // for debug only
  public void printDebug()
  {
    System.out.println( "#"+tick_count+"["+pos.x+":"+pos.y+":"+alt+"] ["+dir.x+":"+dir.y+"]" );
  }
};

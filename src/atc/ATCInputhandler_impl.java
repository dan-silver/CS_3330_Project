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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Object;
import java.util.*;

/**
 * "Controller" implementation using the same control method as the Classical ATC.
 */
public class ATCInputhandler_impl extends Object implements ATCInputhandler
{
  protected ATC atc_obj = null;
  protected ATCInputhandler_impl() { super(); }
  public ATCInputhandler_impl( ATC a )
  { 
    super();
    atc_obj = a;
  }

  protected LinkedList cmd_str = new LinkedList();
  protected String full_cmd_str = new String("");
  protected int plane_id;
  protected Command cmd = null;

  public void processKey( char c )
  {
    if( c == '\n' ) return;
    if( c == '\b' ) // backspace
    {
      if( ! cmd_str.isEmpty() )
        cmd_str.removeLast();
    }
    else
      cmd_str.add( new Character(c) );
    if( parse(false) )
      atc_obj.getData().setCommandString( full_cmd_str );
    else
    {
      cmd_str.removeLast();
      atc_obj.getData().setCommandString( full_cmd_str );
    }
  }

  public boolean processCommand( )
  {
    if( parse(true) )
    {
      if( cmd != null )
        atc_obj.getData().setCommand( plane_id, cmd );

      atc_obj.getData().setCommandString( "" );

      reset();
      return true;
    }
    else
    {
      atc_obj.getData().setCommandString( "" );
      reset();
      return false;
    }
  }
  

  	/*********************************************************************************************
	 * 					Part of the Change for part (E) (ecgprc)
	 * Changed this method by adding additional cases to the command loop to add the ability for the
	 * planes to turn at a particular exit. More comments below detail the changes.
	 *********************************************************************************************/
  protected boolean parse( boolean full_flag )
  {
    if(ATC.debug_flag) //DEBUG
    {
      System.out.println( "Parsing: " + cmd_str.toString() + full_flag );
    }

    int parse_state = 1;
    ListIterator it = cmd_str.listIterator();
    char c;
    Plane p = null;
    StaticObj objs[] = null, obj_to = null, obj_at = null;

    full_cmd_str = new String("");
    
    

    while( it.hasNext() && parse_state != 99 )
    {
      c = ((Character)it.next()).charValue();
      switch( parse_state )
      {
        case 1: //begin
          if( ! Character.isLetter(c) ) return false;
          full_cmd_str += c;
          full_cmd_str += ": ";
          plane_id = Character.toLowerCase(c) - 'a';
          parse_state = 2;
          break;
        case 2: //after id
          if( c!='a' && c!='t' && c!='c' ) return false;
          switch (c)
          {
            case 'a':
              full_cmd_str += "altitude: ";
              if( full_flag )
                cmd = new ALTCommand();
              parse_state = 3;
              break;
            case 't':
              full_cmd_str += "turn ";
              if( full_flag )
                cmd = new TurnCommand();
              parse_state = 6;
              break;
            case 'c':
              full_cmd_str += "circle ";
              if( full_flag )
                cmd = new CircleCommand();
              parse_state = 4;
              break;
          }
          break;
        case 3: //altitude
          int new_alt;
          try { new_alt = Integer.parseInt( Character.toString(c) ); }
          catch(Exception e) { return false; }
          if( new_alt < 0 || new_alt > 9 )
            return false;
          full_cmd_str += Integer.toString(new_alt) + "000 feet ";
          if( full_flag )
            ((ALTCommand)cmd).alt = new_alt;
          parse_state = 99;
          break;
        case 4: //circle
          if( c=='a' )
          {
            it.previous();
            parse_state = 5;
            break;
          }
          if( c!='l' && c!='r' ) return false;
          full_cmd_str += c=='l' ? "left " : "right ";
          if( full_flag )
            ((CircleCommand)cmd).turn = c=='r' ? Turn.RIGHT : Turn.LEFT;
          parse_state = 5;
          break;
         /***************************************************************************************************
      	 * 					Part of the Change for part (E) (ecgprc)
		 * Redirected the parse case for "at" in the control string to a case I created to determine
		 * whether or not the user wishes to at a beacon or an exit. Previously this case automatically
		 * redirected to case 9, which I left in the game so that the change is evident, but I do not use it.
      	 ****************************************************************************************************/
        case 5: //before at clause
          if( c!='a' ) return false;
          full_cmd_str += "at ";
          parse_state = 13;
          break;
        case 6: //turn
          if( c == 't' )
          {
            full_cmd_str += "towards ";
            parse_state = 7;
            break;
          }
          p = atc_obj.getData().getPlane( plane_id );
          if( p == null ) return false;
          Direction new_dir = new Direction( p.dir );
          switch(c){
            case 'r': new_dir.tick( Turn.SMALL_RIGHT ); 
                      full_cmd_str += "right "; break;
            case 'l': new_dir.tick( Turn.SMALL_LEFT ); 
                      full_cmd_str += "left "; break;
            case 'R': new_dir.tick( Turn.RIGHT ); 
                      full_cmd_str += "RIGHT "; break;
            case 'L': new_dir.tick( Turn.LEFT ); 
                      full_cmd_str += "LEFT "; break;
            case 'a':
            case 'q':
            case 'w':
            case 'e':
            case 'd':
            case 'c':
            case 'x':
            case 'z':
                      new_dir = Direction.charToDir( c );
                      full_cmd_str += new_dir.getDirName();
                      full_cmd_str += " ";
                      break;
            default: return false;
          } //end switch
          if( full_flag )
            ((TurnCommand)cmd).dir = new_dir;
          parse_state = 5;
          break;
        case 7: //towards sth.
          switch(c)
          {
            case 'b':
              objs = atc_obj.getData().getBeacons();
              full_cmd_str += "beacon ";
              break;
            case 'e':
              objs = atc_obj.getData().getExits();
              full_cmd_str += "exit ";
              break;
            case 'a':
              objs = atc_obj.getData().getAirfields();
              full_cmd_str += "airfield ";
              break;
            default: return false;
          }
          parse_state = 8;
          break;
        case 8: //obj#
          int obj_num;
          try { obj_num = Integer.parseInt( Character.toString(c) ); }
          catch(Exception e) { return false; }
          if( obj_num < 0 ) return false;
          full_cmd_str += "#" + c + " ";
          obj_to = objs[ obj_num ];
          if( obj_to == null ) return false;

          if( full_flag )
          {
            p = atc_obj.getData().getPlane( plane_id );
            if( p == null ) return false;
            ((TurnCommand)cmd).dir = new Direction( p.pos, obj_to.pos );
          }
          parse_state = 5;
          break;
        case 9: //at beacon
          if( c!='b' ) return false;
          full_cmd_str += "beacon ";
          parse_state = 10;
          break;
        case 10: //at beacon#
          int beacon_num;
          try { beacon_num = Integer.parseInt( Character.toString(c) ); }
          catch(Exception e) { return false; }
          if( beacon_num < 0 ) return false;
          obj_at = atc_obj.getData().getBeacons()[beacon_num];
          if( obj_at == null ) return false;

          full_cmd_str += "#" + c;
          if( full_flag )
          {
            ((DIRCommand)cmd).pos = obj_at.pos;
            ((DIRCommand)cmd).pos_obj = obj_at;
            cmd.active_flag = false;
            if( cmd instanceof TurnCommand && obj_to != null )
              ((TurnCommand)cmd).dir = new Direction( obj_at.pos, obj_to.pos );
          }
          parse_state = 99;
          break;
          /*********************************************************************************
           *		Part of the Change for part (E) (ecgprc)     
           *  Case 12 is a case I created to extend the exit clause I created (13) to be able to
           *  handle the new command (trae#). Case 12 simply requests an exit number from the
           *  user and makes use of the previously implemented logic for beacon turns in order
           *  to execute the command. 
           *********************************************************************************/
        case 12:
        	int exit_num;
            try { exit_num = Integer.parseInt( Character.toString(c) ); }
            catch(Exception e) { return false; }
            if( exit_num < 0 ) return false;
            obj_at = atc_obj.getData().getExits()[exit_num];
            if( obj_at == null ) return false;
            full_cmd_str += "#" + c;
            if( full_flag )
            {
              ((DIRCommand)cmd).pos = obj_at.pos;
              ((DIRCommand)cmd).pos_obj = obj_at;
              cmd.active_flag = false;
              if( cmd instanceof TurnCommand && obj_to != null )
                ((TurnCommand)cmd).dir = new Direction( obj_at.pos, obj_to.pos );
            }
            parse_state = 99;
             break;
         /************************************************************************
         *		Part of the Change for part (E) (ecgprc)     
         *  Case 13 is a case I created to extend the at clause to be able to
         *  handle the new command (e) or the previously created command (b).
         *  Case 13 either redirects to case 10, which requests the beacon number,
         *  or my created Case 12, which handles requests an exit number.
         *************************************************************************/
        case 13:
        	if( c!= 'b' && c!= 'e' ) return false;
        	if( c == 'b' ){
        		parse_state = 10;
                full_cmd_str += "beacon ";
        	}
        	else{
        		parse_state = 12;
            	full_cmd_str += "exit ";
        	}
        	break;
      } //end switch
    } //end while

    if( full_flag )
    {
      if( parse_state != 99 && parse_state != 5 && parse_state != 4 )
        return false;
      else
        return true;
    }
    return true;
  }

  public boolean processActionCommand( String command )
  {
    if( "New".equals( command ) )
    {
      reset();
      atc_obj.getData().generateSeed();
      atc_obj.getData().start();
    }
    else if( "Exit".equals( command ) )
    {
      atc_obj.stopATC();
    }
    else if( "Pause".equals( command ) )
    {
      atc_obj.pauseATC();
    }
    /*********************************************************************************************
	 * 					Part of the changes for part F (das2c3)
	 * Two new cases where added, "Save" and "Load".  The save case writes the seed and the load
	 * case reads the planes.txt file to get the saved seed.
	 **********************************************************************************************/
	
    else if( "Save".equals( command ) )
    {
        atc_obj.getData().save();
    }
    else if( "Load".equals( command ) )
    {
        reset();
        
        
        BufferedReader br = null;
        Long seed = null;
		try {
			br = new BufferedReader(new FileReader("planes.txt"));
            seed = Long.parseLong(br.readLine());
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        atc_obj.getData().setSeed(seed);
        
        
        atc_obj.getData().start();
    }
    return true;
  }

  public void reset()
  {
    cmd = null;
    cmd_str = new LinkedList();
  }
};

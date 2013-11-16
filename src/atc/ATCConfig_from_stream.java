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

import java.lang.String;
import java.io.*;

/**
 * This class gets game configuration from a Reader stream.
 * The configration syntax is the same as the classical ATC.
 */
public class ATCConfig_from_stream implements ATCConfig
{
  protected int dx=9, dy=9, new_plane_chance=20, init_tick_ms=3000, tick_dec=2;
  protected int next_exit = 0;
  protected int next_beacon = 0;
  protected int next_airfield = 0;
  protected int next_line = 0;
  protected String name = "";
  protected Reader input_reader = null;

  public ATCConfig_from_stream( String n, Reader reader )
  {
    name = new String(n);
    input_reader = reader;
  }
  public int get_dx() { return dx; }
  public int get_dy() { return dy; }
  public int get_new_plane_chance() { return new_plane_chance; }
  public int get_init_tick_ms() { return init_tick_ms; }
  public int get_tick_dec() { return tick_dec; }
  public String get_name() { return name; }

  private StreamTokenizer tokenizer = null;
  class ParseException extends Exception
  {
    public ParseException( String str ){super(str);}
  };

  public boolean config
        ( Beacon   beacons[],   int max_beacon,
          Exit     exits[],     int max_exit,
          Airfield airfields[], int max_airfield,
          Line     lines[],     int max_line )
  {
    try
    {
      Reader reader = new BufferedReader( input_reader );

      tokenizer = new StreamTokenizer (reader);
      tokenizer.eolIsSignificant (false);
      tokenizer.wordChars(':',':');
      tokenizer.lowerCaseMode( true );

      int token;
      while( true )
      {
        token = tokenizer.nextToken();
        if( StreamTokenizer.TT_EOF == token ) break;
        if( StreamTokenizer.TT_WORD == token )
        {
          if( "update".equals( tokenizer.sval ) )
            parse_update();
          else if( "newplane".equals( tokenizer.sval ) )
            parse_newplane();
          else if( "width".equals( tokenizer.sval ) )
            parse_width();
          else if( "height".equals( tokenizer.sval ) )
            parse_height();
          else if( "exit:".equals( tokenizer.sval ) )
            parse_exit( exits, max_exit );
          else if( "beacon:".equals( tokenizer.sval ) )
            parse_beacon( beacons, max_beacon );
          else if( "airport:".equals( tokenizer.sval ) )
            parse_airport( airfields, max_airfield );
          else if( "line:".equals( tokenizer.sval ) )
            parse_line( lines, max_line );

          else 
            throw ( new ParseException(tokenizer.toString()) );
        } // end word
        else
          throw ( new ParseException(tokenizer.toString()) );
      } //end whlie
    }
    catch (IOException e)
    {
      System.out.println (e.getMessage ());
    }
    catch (ParseException e2)
    {
      System.out.println (e2.getMessage ());
    }
    return true;
  }// end config(parse)

  private void parse_update() throws ParseException, IOException
  {
    int update_val;
    expect_token( '=' );
    update_val=expect_number();
    expect_token( ';' );
    init_tick_ms = update_val * 1000;
  }
  private void parse_newplane() throws ParseException, IOException
  {
    int r;
    expect_token( '=' );
    r=expect_number();
    expect_token( ';' );
    new_plane_chance = r;
  }
  private void parse_width() throws ParseException, IOException
  {
    int r;
    expect_token( '=' );
    r=expect_number();
    expect_token( ';' );
    dx = r;
  }
  private void parse_height() throws ParseException, IOException
  {
    int r;
    expect_token( '=' );
    r=expect_number();
    expect_token( ';' );
    dy = r;
  }

  private void parse_exit( Exit exits[], int max_exit )  
    throws ParseException, IOException
  {
    int x, y;
    char dir;
    int token;
    while( true )
    {
      token = tokenizer.nextToken();
      if( token == ';' ) return;
      if( token != '(' ) 
        throw ( new ParseException(tokenizer.toString()) );
      x = expect_number();
      y = expect_number();
      dir = expect_letter();
      expect_token( ')' );
      if( next_exit < max_exit )
      {
        exits[next_exit] = 
          new Exit( new Position(x,y), Direction.charToDir(dir) );
        exits[next_exit].id = next_exit;
        next_exit++;
      }
    }
  }

  private void parse_beacon( Beacon beacons[], int max_beacon ) 
    throws ParseException, IOException
  {
    int x, y;
    int token;
    while( true )
    {
      token = tokenizer.nextToken();
      if( token == ';' ) return;
      if( token != '(' ) 
        throw ( new ParseException(tokenizer.toString()) );
      x = expect_number();
      y = expect_number();
      expect_token( ')' );
      if( next_beacon < max_beacon )
      {
        beacons[next_beacon] = 
          new Beacon( new Position(x,y) );
        beacons[next_beacon].id = next_beacon;
        next_beacon++;
      }
    }
  }

  private void parse_airport( Airfield airfields[], int max_airfield ) 
    throws ParseException, IOException
  {
    int x, y;
    char dir;
    int token;
    while( true )
    {
      token = tokenizer.nextToken();
      if( token == ';' ) return;
      if( token != '(' ) 
        throw ( new ParseException(tokenizer.toString()) );
      x = expect_number();
      y = expect_number();
      dir = expect_letter();
      expect_token( ')' );
      if( next_airfield < max_airfield )
      {
        airfields[next_airfield] = 
          new Airfield( new Position(x,y), Direction.charToDir(dir) );
        airfields[next_airfield].id = next_airfield;
        next_airfield++;
      }
    }
  }
  
  private void parse_line( Line lines[], int max_line )
    throws ParseException, IOException
  {
    int x1, x2, y1, y2;
    int token;
    while( true )
    {
      token = tokenizer.nextToken();
      if( token == ';' ) return;
      if( token != '[' )
        throw ( new ParseException(tokenizer.toString()) );
      expect_token( '(' );
      x1 = expect_number();
      y1 = expect_number();
      expect_token( ')' );
      expect_token( '(' );
      x2 = expect_number();
      y2 = expect_number();
      expect_token( ')' );
      expect_token( ']' );
      if( next_line < max_line )
      {
        lines[next_line] = 
          new Line( new Position(x1,y1), new Position(x2,y2) );
        lines[next_line].id = next_line;
        next_line++;
      }
    }
  }

  private char expect_letter( ) throws ParseException, IOException
  {
    tokenizer.nextToken();
    if( StreamTokenizer.TT_WORD != tokenizer.ttype 
        || tokenizer.sval.length() != 1 )
      throw ( new ParseException(tokenizer.toString()) );
    else
      return tokenizer.sval.charAt(0);
  }
  private int expect_number( ) throws ParseException, IOException
  {
    tokenizer.nextToken();
    if( StreamTokenizer.TT_NUMBER != tokenizer.ttype )
      throw ( new ParseException(tokenizer.toString()) );
    else
      return (int)tokenizer.nval;
  }
  private int expect_token( int et ) throws ParseException, IOException
  {
    int t = tokenizer.nextToken();
    if( t != et )
      throw ( new ParseException(tokenizer.toString()) );
    else
      return t;
  }

};

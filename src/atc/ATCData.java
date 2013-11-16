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
import java.util.Random;

/**
 * "Model" in ATCJ's model-view-controller structure.
 */
public class ATCData extends Object
{
  protected ATC atc_obj = null;

  // Game setup data here...
  protected int dx, dy;
  protected int new_plane_chance;
  protected int init_tick_ms;
  protected int tick_ms; // in ms
  protected int tick_dec;
  protected String config_name;
  protected String user_name;

  protected int max_static_obj = 10;
  protected int max_plane = 26;
  protected int min_tick_ms = 800;
  
  // Game dynamic data here...
  protected Exit exits[];
  protected Beacon beacons[];
  protected Airfield airfields[];
  protected Line lines[];
  protected int beacon_count = 0;
  protected int exit_count = 0;
  protected int airfield_count = 0;
  protected int line_count = 0;
  public StaticObj[] getExits() { return exits; }
  public StaticObj[] getBeacons() { return beacons; }
  public StaticObj[] getAirfields() { return airfields; }

  protected Plane planes[];
  protected int next_plane = 0;
  public Plane getPlane( int i ) { return planes[i]; }

  protected int tick_count = 0;
  protected int safe_plane_count = 0;
  protected Random rand = new Random();

  protected long start_time_ms = 0;
  protected long stop_time_ms = 0;

  ATCConfig config = null;
  ATCRecord record = null;


  protected ATCData() { super(); }
  public ATCData( ATC a )
  {
    super();
    atc_obj = a;
  }

  public void setConfig( ATCConfig conf )
  {
    config = conf;
  }

  public void setRecord( ATCRecord rec )
  {
    record = rec;
  }

  public void initData()
  {
    beacons = new Beacon[max_static_obj];
    exits = new Exit[max_static_obj];
    airfields = new Airfield[max_static_obj];
    lines = new Line[max_static_obj];
    planes = new Plane[max_plane];
    int i;
    for( i=0; i < max_plane; i++ )
      planes[i] = null;
    for( i=0; i < max_static_obj; i++ )
    {
      beacons[i] = null;
      exits[i] = null;
      airfields[i] = null;
      lines[i] = null;
    }

    // Read Config
    if( config == null )
      return;
    config.config( beacons, max_static_obj,
                   exits, max_static_obj,
                   airfields, max_static_obj,
                   lines, max_static_obj );
    dx = config.get_dx();
    dy = config.get_dy();
    new_plane_chance = config.get_new_plane_chance();
    init_tick_ms = tick_ms = config.get_init_tick_ms();
    tick_dec = config.get_tick_dec();
    config_name = config.get_name();
 
    // We use the user name from OS, for now.
    try{
      user_name = System.getProperties().getProperty("user.name");
    }
    catch( java.security.AccessControlException e )
    {
      user_name = new String("unknown");
    }

    atc_obj.getUI().initUI( dx, dy );

    for( i=0; i<max_static_obj; i++ )
      if( beacons[i] != null )
      {
        beacon_count = i+1;
        atc_obj.getUI().StaticObjNew( beacons[i] );
      }
    for( i=0; i<max_static_obj; i++ )
      if( exits[i] != null )
      {
        exit_count = i+1;
        atc_obj.getUI().StaticObjNew( exits[i] );
      }
    for( i=0; i<max_static_obj; i++ )
      if( airfields[i] != null )
      {
        airfield_count = i+1;
        atc_obj.getUI().StaticObjNew( airfields[i] );
      }
    for( i=0; i<max_static_obj; i++ )
      if( lines[i] != null )
      {
        line_count = i+1;
        atc_obj.getUI().StaticObjNew( lines[i] );
      }

    atc_obj.getUI().refresh();
    atc_obj.getUI().ready();
  }

  //Timer
  java.util.Timer timer = null;
  class ATCTask extends java.util.TimerTask {
    protected ATCData data;
    public ATCTask( ATCData d ) { data = d; }
    public void run() { 
      try
      { 
        data.tick(); 
      } 
      catch( ATCGameOverException e ) 
      { 
        data.gameOver( e.getMessage() );
      }
    }
  };
  ATCTask task = null;

  public synchronized void start()
  {
    start_time_ms = System.currentTimeMillis();

    int i;
    for( i=0; i<max_plane; i++ )
      if( planes[i] != null && planes[i].takeoff_flag )
      {
        atc_obj.getUI().PlaneRemove( planes[i] );
        planes[i] = null;
      }

    next_plane = 0;
    tick_count = 0;
    safe_plane_count = 0;
    tick_ms = init_tick_ms;

    atc_obj.getUI().InfoUpdate( tick_count, safe_plane_count );

    atc_obj.getUI().start();

    task = null;
    task = new ATCTask( this );
    timer = new java.util.Timer();
    timer.scheduleAtFixedRate( task, 100, get_tick_ms() );
  }
    
  public void gameOver( String gameOverMessage )
  {
    stop_time_ms = System.currentTimeMillis();
    if( timer != null )
    {
      timer.cancel();
      timer = null;
    }
    
    // Save record
    if( start_time_ms != 0 )
      if( record != null )
        record.save( user_name, config_name, tick_count, 
            stop_time_ms - start_time_ms, safe_plane_count );
    start_time_ms = stop_time_ms = 0;
    
    if( gameOverMessage != null )
      atc_obj.getUI().gameOver( gameOverMessage );

  }

  protected void newPlane()
  {
    int r =  rand.nextInt( new_plane_chance );
    if( ATC.debug_flag ) System.out.println( "NP: r=" + r ); //DEBUG
    if( tick_count == 1 || r == new_plane_chance-1 )
    {
      int plane_id;
  synchronized(this) {
      // get next plane id.
      plane_id = next_plane;
      if( planes[plane_id] != null )
      {
        plane_id++;
        if( plane_id >= max_plane )
          plane_id %= max_plane;
        while( planes[plane_id] != null )
        {
          plane_id++;
          if( plane_id >= max_plane )
            plane_id %= max_plane;
          if( plane_id == next_plane )
            return; // full
        }
      }

      if( ATC.debug_flag ) System.out.println( "NP: id=" + plane_id ); //DEBUG

      //Pick start and destination
      int total = exit_count + airfield_count;
      if( total < 2 ) return;
      int i1, i2;
      StaticObj o1 = null, o2 = null;

      //Try to find a place not next to other planes. 4 times.
      int i, j;
      boolean found_flag = false;
      for( i=0; i<4 && !found_flag ; i++ )
      {
        i1 = rand.nextInt(total);
        do { i2 = rand.nextInt(total); } while( i1 == i2 );

        o1 = i1<exit_count ? 
          (StaticObj)(exits[i1]) : (StaticObj)(airfields[i1-exit_count]);
        o2 = i2<exit_count ? 
          (StaticObj)(exits[i2]) : (StaticObj)(airfields[i2-exit_count]);
        if( o1 == null || o2 == null )
          continue;

        found_flag = true;
        for( j=0; j<max_plane; j++ )
          if( planes[j] != null && planes[j].pos != null && 
              planes[j].takeoff_flag )
            if( Math.abs(planes[j].pos.x - o1.pos.x) <= 1 &&
                Math.abs(planes[j].pos.y - o1.pos.y) <= 1 &&
                Math.abs(planes[j].alt - o1.alt) <=1 )
              found_flag = false;
      } // end for
      if( !found_flag ) return;

      if( ATC.debug_flag ) System.out.println( "NP: found." ); //DEBUG

      Plane new_plane = 
        new Plane( o1.pos, o1.dir, o1.alt, rand.nextInt(2)+1, o2 );

      new_plane.id = plane_id;
      planes[plane_id] = new_plane;
      next_plane = plane_id+1;
      if( next_plane >= max_plane )
        next_plane %= max_plane;
    } // end synchronized
      atc_obj.getUI().PlaneNew( planes[plane_id] );
    } // end if
  }

  public void tick() throws ATCGameOverException
  {
    tick_count++;

    int plane_id;
    for( plane_id=0; plane_id<max_plane; plane_id++)
      if( planes[plane_id] != null )
      {
        planes[plane_id].tick();
      }

    newPlane();

  synchronized(this){
    for( plane_id=0; plane_id<max_plane; plane_id++)
    {
      if( planes[plane_id] == null )
        continue;

      if( planes[plane_id].changed_flag )
        atc_obj.getUI().PlaneUpdate( planes[plane_id]);

      if( isSafe(plane_id) )
      {
        atc_obj.getUI().PlaneRemove( planes[plane_id] );
        safe_plane_count++;
        planes[plane_id] = null;
        continue;
      }

      if( isDead(plane_id) )
        throw new ATCGameOverException( "Game Over!" );
    }

    if( tick_ms > min_tick_ms )
      tick_ms -= tick_dec;
  } // end synchronized
    atc_obj.getUI().InfoUpdate( tick_count, safe_plane_count );
    atc_obj.getUI().refresh();
  }
    protected boolean isSafe( int id )
    {
      if( ! planes[id].takeoff_flag )
        return false;
      StaticObj destination = planes[id].destination;
      if( destination.pos != null && destination.pos.equals( planes[id].pos ) )
        if( destination.exit_alt == planes[id].alt )
          if( destination.exit_dir != null )
          {
            if( planes[id].dir.equals( destination.exit_dir ) )
              return true;
          }
          else 
            return true;
      return false;
    }
    protected boolean isDead( int id ) throws ATCGameOverException
    {
      if( ! planes[id].takeoff_flag )
        return false;

      if( planes[id].alt <= 0 )
        throw new ATCGameOverException
          ( "Plane " + planes[id].getIdChar() + " crashed to the ground." );

      if( planes[id].pos.x < 0 || planes[id].pos.y < 0 ||
          planes[id].pos.x >= dx || planes[id].pos.y >= dy )
        throw new ATCGameOverException
          ( "Plane " + planes[id].getIdChar() + " flew out of radar area." );

      int id2;
      for( id2=0; id2<max_plane; id2++ )
        if( planes[id2] != null && id2 != id && planes[id2].takeoff_flag )
        {
          if( Math.abs( planes[id].alt - planes[id2].alt ) <= 1 &&
              Math.abs( planes[id].pos.x - planes[id2].pos.x ) <= 1 &&
              Math.abs( planes[id].pos.y - planes[id2].pos.y ) <= 1 )
            throw new ATCGameOverException( 
                "Planes " + planes[id].getIdChar() + " and " 
                + planes[id2].getIdChar() + " crashed." );
        }

      return false;
    }


  protected int get_tick_ms()
  {
    return tick_ms;
  }


  public boolean setCommand( int id, Command cmd )
  {
    if( planes[id] == null )
      return false;
    planes[id].setCommand( cmd );
    atc_obj.getUI().PlaneUpdate( planes[id] );

    return true;
  }

  public void setCommandString( String s )
  {
    atc_obj.getUI().CommandUpdate( s );
  }
};



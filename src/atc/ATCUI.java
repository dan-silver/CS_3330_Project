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
 * "View" interface in ATCJ's model-view-controller structure.
 */
public interface ATCUI
{
  /**
   * init the UI with the radar area dimension.
   */
  public void initUI( int dx, int dy );

  public void StaticObjNew( StaticObj so );
  public void CommandUpdate( String cmd_str );
  public void PlaneNew( Plane p );
  public void PlaneUpdate( Plane p );
  public void PlaneRemove( Plane p );
  public void InfoUpdate( int tick_count, int safe_count );

  /**
   * When ATCData notifies UI when it is initialized.
   */
  public void ready();

  /**
   * When ATCData tells UI the game starts.
   */
  public void start();

  /**
   * When ATCData tells UI the game is over.
   */
  public void gameOver( String s );

  /**
   * When ATCData tells UI to re-draw itself.
   */
  public void refresh();

  /**
   * When ATCData or ATC tells UI to close itself.
   */
  public void close();
};


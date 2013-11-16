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
import java.lang.Math;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * "View" implementation using awt.
 */
public class ATCUI_impl_awt
  extends Frame
  implements ATCUI, ActionListener, KeyListener, WindowListener
{
  protected ATC atc_obj = null;
  protected ATCUI_impl_awt() { super(); }
  public ATCUI_impl_awt( ATC a )
  {
    super();
    atc_obj = a;
    addWindowListener(this);
  }
  
  protected boolean keyListenerAdded = false;

  // Size
  protected int grid_size = 30, icon_size = 20;
  protected int text_height, text_gap = -3;
  protected int plane_width, plane_height;
  protected int radar_area_width, radar_area_height;
  protected int info_area_width=140;
  protected int info_area_length=23;
  protected int info_area_count=27;
  protected int dx, dy;

    // Position to Coordinate
    protected int convPos( int px ) { return px*grid_size+grid_size/2; }

  // Colors, Icons, Functions for icons
  protected Color rim_color = Color.darkGray;
  protected Color back_color = Color.yellow;
  protected Color text_color = Color.red;
  protected Color so_color = Color.orange;
  protected Color so_text_color = Color.magenta;
  protected Color line_color = Color.white;

  Image planeImages[] = new Image[9];
  String planeIconPaths[] = {
    "images/NW.gif", "images/N.gif", "images/NE.gif", "images/W.gif",
    "images/back.gif", 
    "images/E.gif", "images/SW.gif", "images/S.gif", "images/SE.gif" };

    protected void loadIcons()
    {
      int i;
      java.net.URL imageURL = null;
      MediaTracker mediaTracker = new MediaTracker( this );
      for( i=0; i<planeIconPaths.length; i++ )
      {
        try{
        imageURL = new java.net.URL( atc_obj.codeBase + planeIconPaths[i] );
        }catch(Exception e){ imageURL = null; }

        if( imageURL == null )
        {
          System.err.println("Can't load icon " + i);
          atc_obj.stopATC();
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        planeImages[i] = toolkit.getImage( imageURL );
        mediaTracker.addImage(planeImages[i], 0);
      }
      try{
        mediaTracker.waitForID(0);
      }catch( InterruptedException e ){}
    }

    protected Image dirToPlaneImage( Direction d )
    {
      int i = (d.x+1) + (d.y+1)*3;
      if( i<0 || i>=9 ) return null;
      return planeImages[i];
    }

  // UI objs
  // RadarCanvas
  class RadarCanvas extends Canvas
  {
    private Dimension dim = null;
    public RadarCanvas( int x, int y ){ super(); dim = new Dimension(x, y); }
    public Dimension getPreferredSize() { return dim; }
    public Dimension getMinimalSize() { return dim; }

    public BufferedImage backImage;

    public void paint( Graphics g )
    {
      g.drawImage( backImage, 0, 0, null );
      if( planes.isEmpty() ) return;

      Iterator it = planes.values().iterator();
      UIPlane p;
      while ( it.hasNext() )
      {
        p = (UIPlane)(it.next());
        if( p.img != null )
          g.drawImage( p.img, p.x-icon_size/2, p.y-icon_size/2, null );
        if( p.text != null )
        {
          g.setColor( text_color );
          g.drawString
            ( p.text, p.x-plane_width/2, p.y-icon_size/2-text_gap ); 
        }
      }
    }
    public void update( Graphics g )
    {
      paint(g);
    }
  };
  RadarCanvas radarCanvas;

  Panel infoArea;
    Label infoTopLine;
  Label inputArea;
  Panel controlArea;
    Button newButton;
    Button exitButton;

  // Planes
  class UIPlane extends Object
  {
    public Label info_label;
    
    public int x, y;
    public Image img = null;
    public String text = null;
  };
  Map planes = new HashMap(); // Char => UIPlane
  
  ///////////////////
  //
  public void initUI( int x, int y )
  {
    setVisible(true);

    // Load icons
    loadIcons();

    // Determine size
    dx = x; dy = y;
    text_height = getFontMetrics( getFont() ).getHeight();
    plane_width = icon_size;
    plane_height = icon_size+text_height+text_gap;
    radar_area_width = grid_size * dx;
    radar_area_height = grid_size * dy;

    // Init UI objects
    setLayout( new BorderLayout() );

    // radarCanvas
    radarCanvas = new RadarCanvas( radar_area_width, radar_area_height );
    radarCanvas.backImage = new BufferedImage
      ( radar_area_width, radar_area_height, BufferedImage.TYPE_INT_RGB );
    add( radarCanvas, BorderLayout.WEST );

      Graphics2D g = radarCanvas.backImage.createGraphics();
      g.setBackground( back_color );
      g.setColor( rim_color );
      g.fillRect( 0, 0, radar_area_width, radar_area_height );
      g.setColor( back_color );
      g.fillRect( convPos(0), convPos(0),
          radar_area_width-grid_size, radar_area_height-grid_size );
      g.setColor( line_color );
      int i, j;
      for ( i=0; i<dx; i++ )
        for( j=0; j<dy; j++ )
          g.draw( new Rectangle( convPos(i)-1, convPos(j)-1, 1, 1 ) );

    // infoArea
    infoArea = new Panel()
    { 
      public Dimension getPreferredSize() { return new Dimension(info_area_width, radar_area_height); }
      public Dimension getMinimalSize() { return new Dimension(info_area_width, radar_area_height); }
    };
    infoArea.setSize( new Dimension(info_area_width, radar_area_height) );
    infoArea.setLayout( new GridLayout(info_area_count,1) );
      infoTopLine = new Label("");
      infoArea.add( infoTopLine );
    add( infoArea, BorderLayout.EAST );

    // inputArea
    inputArea = new Label("          ");
    add( inputArea, BorderLayout.SOUTH );

    // controlArea
    controlArea = new Panel();
    newButton = new Button( "New" );
    newButton.setActionCommand("New");
    newButton.addActionListener( this );
    newButton.setEnabled( false );
    newButton.setFocusable( false ); // Don't steal Frame's focus
    exitButton = new Button( "Exit" );
    exitButton.setActionCommand("Exit");
    exitButton.addActionListener( this );
    exitButton.setFocusable( false ); // Don't steal Frame's focus
    controlArea.add( newButton );
    controlArea.add( exitButton );
    add( controlArea, BorderLayout.NORTH );
    controlArea.validate();

    pack();
  }


  ///////////////////
  //
  public void StaticObjNew( StaticObj so )
  {
    Graphics2D g = radarCanvas.backImage.createGraphics();
    g.setColor( so_color );

    if( so instanceof Beacon )
    {
      g.drawOval(
          convPos( so.pos.x ) - grid_size/6,
          convPos( so.pos.y ) - grid_size/6,
          grid_size/3, grid_size/3
          );
      g.setColor( so_text_color );
      g.drawString( 
          Integer.toString(so.id), 
          convPos( so.pos.x ) - grid_size/6,
          convPos( so.pos.y ) + grid_size/2
          );
    }

    if( so instanceof Airfield )
    {
      int xa[] = new int[3], ya[] = new int[3];
      int l1=grid_size/2, l2=grid_size/6;
      xa[0] = convPos(so.pos.x);
      ya[0] = convPos(so.pos.y);
      xa[1] = convPos(so.pos.x) - l1*so.dir.x - l2*so.dir.y;
      ya[1] = convPos(so.pos.y) - l1*so.dir.y + l2*so.dir.x;
      xa[2] = convPos(so.pos.x) - l1*so.dir.x + l2*so.dir.y;
      ya[2] = convPos(so.pos.y) - l1*so.dir.y - l2*so.dir.x;
      g.drawPolygon( xa, ya, 3 );

      g.setColor( so_text_color );
      g.drawString( 
          Integer.toString(so.id), 
          convPos( so.pos.x ) - grid_size/2,
          convPos( so.pos.y ) + grid_size/2
          );
    }

    if( so instanceof Exit )
    {
      g.drawRect(
          convPos( so.pos.x ) - grid_size/6,
          convPos( so.pos.y ) - grid_size/6,
          grid_size/3, grid_size/3
          );
      g.setColor( so_text_color );
      g.drawString( 
          Integer.toString(so.id), 
          convPos( so.pos.x ) - grid_size/4,
          convPos( so.pos.y ) + grid_size/2
          );
    }

    if( so instanceof Line )
    {
      g.setColor( line_color );
      g.drawLine (
          convPos( ((Line)so).pos.x ), 
          convPos( ((Line)so).pos.y ), 
          convPos( ((Line)so).second_end.x ), 
          convPos( ((Line)so).second_end.y )
          );
    }

  }


  ///////////////////
  //
  public void CommandUpdate( String cmd_str )
  {
    inputArea.setText( cmd_str );
    validate();
  }

  public void PlaneNew( Plane p )
  {
    UIPlane uiplane = new UIPlane();
    char id = p.getIdChar();
    uiplane.info_label = new Label( (new Character(id)).toString() );
    infoArea.add( uiplane.info_label );
    infoArea.validate();
    synchronized(this){
      planes.put( (Object)(new Character(id)), (Object)uiplane );
    }

    PlaneUpdate( p );
  }

  public void PlaneUpdate( Plane p )
  {
    char id = p.getIdChar();
    UIPlane uiplane = null;
    synchronized(this){
      uiplane = (UIPlane)(planes.get( (Object)(new Character(id)) ));
    }
    if( uiplane == null ) return;

    if( p.takeoff_flag )
    {
      uiplane.x = convPos( p.pos.x );
      uiplane.y = convPos( p.pos.y );
      uiplane.text = getPlaneText(p);
      uiplane.img = dirToPlaneImage( p.dir );

    }

    uiplane.info_label.setText( getPlaneInfoText(p) );
    infoArea.validate();
  }

    protected String getPlaneText( Plane p )
    {
      return 
          (new Character(p.getIdChar())).toString() + 
          (new Integer(p.alt)).toString();
    }

    protected String getPlaneInfoText( Plane p )
    {
      String rs = new String( " " );
      rs += (new Character(p.getIdChar())).toString() + p.alt + " ";
      rs += p.destination.getName() + " ";
      if( p.dir_cmd != null )
      {
        if( p.dir_cmd instanceof CircleCommand )
        {
          rs += "C";
        }
        else
        {
          rs += ((TurnCommand)p.dir_cmd).dir.getDirName();
        }
        if( p.dir_cmd.pos_obj != null )
        {
          rs += "@";
          rs += p.dir_cmd.pos_obj.getName();
        }
      }
      return rs;
    }

  public void PlaneRemove( Plane p )
  {
    char id = p.getIdChar();
    UIPlane uiplane = null;
    synchronized(this) {
      uiplane = (UIPlane)(planes.get( (Object)(new Character(id)) ));
    }
    if( uiplane == null ) return;

    if( uiplane.info_label != null )
      infoArea.remove( uiplane.info_label );
    synchronized(this) {
      planes.remove( (Object)(new Character(id)) );
    }

    infoArea.validate();
    infoArea.repaint();
    radarCanvas.repaint();
  }

  public void InfoUpdate( int tick_count, int safe_count )
  {
    String str = " Time: " + tick_count + ", Safe: " + safe_count;
    infoTopLine.setText( " Time: " + tick_count + ", Safe: " + safe_count );
    infoArea.validate();
  }

  // Button callback
  public void actionPerformed( ActionEvent e )
  {
    atc_obj.getInputhandler().processActionCommand( e.getActionCommand() );
  }

  public void ready()
  {
    newButton.setEnabled( true );
  }

  public void start()
  {
    newButton.setEnabled( false );
    if( ! keyListenerAdded )
    {
      addKeyListener( this );
      keyListenerAdded = true;
    }
    inputArea.setText("");
  }

  public void gameOver( String gameOverMessage )
  {
    inputArea.setText( "!!! " + gameOverMessage + " !!!" );

    newButton.setEnabled( true );
    if( keyListenerAdded )
    {
      removeKeyListener( this );
      keyListenerAdded = false;
    }
  }

  public void refresh()
  {
    //setVisible( true );
    radarCanvas.repaint();
  }

  public synchronized void close()
  {
    setVisible(false);
    dispose();
  }

  public void keyTyped(KeyEvent e)
  {
    char key = e.getKeyChar();
    if( key == '\n' )
      atc_obj.getInputhandler().processCommand();
    else
      atc_obj.getInputhandler().processKey( key );
  }
  public void keyPressed(KeyEvent e)
  {
  }
  public void keyReleased(KeyEvent e)
  {
  }

  public void windowClosing(WindowEvent event) {
    atc_obj.stopATC();
  }
  public void windowClosed(WindowEvent e){}
  public void windowOpened(WindowEvent e){}
  public void windowIconified(WindowEvent e){}
  public void windowDeiconified(WindowEvent e){}
  public void windowActivated(WindowEvent e){}
  public void windowDeactivated(WindowEvent e){}
};

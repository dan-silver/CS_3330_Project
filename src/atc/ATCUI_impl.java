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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * "View" implementation using swing.
 */
public class ATCUI_impl 
  extends JFrame 
  implements ATCUI, ActionListener, KeyListener, WindowListener
{
  protected ATC atc_obj = null;
  protected ATCUI_impl() { super(); }
  public ATCUI_impl( ATC a )
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
  protected int dx, dy;

    // Position to Coordinate
    protected int convPos( int px ) { return px*grid_size+grid_size/2; }

  // Colors, Icons, Functions for icons
  protected Color rim_color = Color.darkGray;
  protected Color back_color = Color.blue;
  protected Color text_color = Color.yellow;
  protected Color so_color = Color.orange;
  protected Color so_text_color = Color.magenta;
  protected Color line_color = Color.white;

  ImageIcon planeIcons[] = new ImageIcon[9];
  String planeIconPaths[] = {
    "images/NW.gif", "images/N.gif", "images/NE.gif", "images/W.gif",
    "images/back.gif", 
    "images/E.gif", "images/SW.gif", "images/S.gif", "images/SE.gif" };

    protected void loadIcons()
    {
      int i;
      java.net.URL imageURL = null;
      String imagePath = "";
      for( i=0; i<planeIconPaths.length; i++ )
      {
        try{
        	imagePath = planeIconPaths[i];
        //imageURL = new java.net.URL( atc_obj.codeBase + planeIconPaths[i] );
        	
        }catch(Exception e){ 
        imageURL = null; }

        if( imagePath == "" )
        {
          System.err.println("Can't load icon " + i);
          atc_obj.stopATC();
        }
        planeIcons[i] = new ImageIcon( imagePath );
      }
    }

    protected ImageIcon dirToPlaneIcon( Direction d )
    {
      int i = (d.x+1) + (d.y+1)*3;
      if( i<0 || i>=9 ) return null;
      return planeIcons[i];
    }

  // UI objs
  class RadarPane extends JLayeredPane
  {
    public JLabel back;
    public ImageIcon backIcon;
    public BufferedImage backImage;
    public RadarPane() { super(); }
  };
  RadarPane radarArea;
  JPanel infoArea;
    Label infoTopLine;
  JLabel inputArea;
  JPanel controlArea;
    JButton newButton;
    JButton exitButton;

  // Planes
  class UIPlane extends Object
  {
    public JLabel radar_label;
    public Label info_label;
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
    getContentPane().setLayout( new BorderLayout() );

    // radarArea
    radarArea = new RadarPane();
    radarArea.setMinimumSize
      ( new Dimension(radar_area_width, radar_area_height) );
    radarArea.setPreferredSize
      ( new Dimension(radar_area_width, radar_area_height) );
    getContentPane().add( radarArea, BorderLayout.CENTER );

      // radarArea.back
      radarArea.backImage = new BufferedImage
        ( radar_area_width, radar_area_height, BufferedImage.TYPE_INT_RGB );
      Graphics2D g = radarArea.backImage.createGraphics();
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

      radarArea.backIcon = new ImageIcon( radarArea.backImage );
      radarArea.back = new JLabel( radarArea.backIcon );
      radarArea.back.setBounds( 0, 0, radar_area_width, radar_area_height );
      radarArea.add( radarArea.back, new Integer(0) ); //back layer

    // infoArea
    infoArea = new JPanel();
    infoArea.setMinimumSize
      ( new Dimension(info_area_width, radar_area_height) );
    infoArea.setPreferredSize
      ( new Dimension(info_area_width, radar_area_height) );
    infoArea.setLayout( new GridLayout(27,1) );
      infoTopLine = new Label(" ");
      infoArea.add( infoTopLine );
    getContentPane().add( infoArea, BorderLayout.EAST );

    // inputArea
    inputArea = new JLabel("          ");
    getContentPane().add( inputArea, BorderLayout.SOUTH );

    // controlArea
    controlArea = new JPanel();
    newButton = new JButton( "New" );
    newButton.setActionCommand("New");
    newButton.addActionListener( this );
    newButton.setEnabled( false );
    newButton.setFocusable( false ); // Don't steal Frame's focus
    exitButton = new JButton( "Exit" );
    exitButton.setActionCommand("Exit");
    exitButton.addActionListener( this );
    exitButton.setFocusable( false ); // Don't steal Frame's focus
    controlArea.add( newButton );
    controlArea.add( exitButton );
    getContentPane().add( controlArea, BorderLayout.NORTH );

    pack();
  }


  ///////////////////
  //
  public void StaticObjNew( StaticObj so )
  {
    Graphics2D g = radarArea.backImage.createGraphics();
    g.setColor( so_color );

    if( so instanceof Beacon )
    {
      g.drawOval(
          convPos( so.pos.x ) - grid_size/6,
          convPos( so.pos.y ) - grid_size/6,
          grid_size/3, grid_size/3
          );
      JLabel sol = new JLabel( Integer.toString(so.id) );
      sol.setForeground( so_text_color );
      sol.setBounds( 
          convPos( so.pos.x ) + grid_size/6,
          convPos( so.pos.y ),
          grid_size/3, grid_size/2
          );
      radarArea.add( sol, new Integer(1) ); // So text layer
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
      JLabel sol = new JLabel( Integer.toString(so.id) );
      sol.setForeground( so_text_color );
      sol.setBounds( 
          convPos( so.pos.x ) + grid_size/6,
          convPos( so.pos.y ),
          grid_size/3, grid_size/2
          );
      radarArea.add( sol, new Integer(1) ); // So text layer
    }

    if( so instanceof Exit )
    {
      g.drawRect(
          convPos( so.pos.x ) - grid_size/6,
          convPos( so.pos.y ) - grid_size/6,
          grid_size/3, grid_size/3
          );
      JLabel sol = new JLabel( Integer.toString(so.id) );
      sol.setForeground( so_text_color );
      sol.setBounds( 
          convPos( so.pos.x ) + grid_size/6,
          convPos( so.pos.y ),
          grid_size/3, grid_size/2
          );
      radarArea.add( sol, new Integer(1) ); // So text layer
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

    radarArea.backIcon = new ImageIcon( radarArea.backImage );
    radarArea.back.setIcon( radarArea.backIcon );
  }


  ///////////////////
  //
  public void CommandUpdate( String cmd_str )
  {
    inputArea.setText( cmd_str );
  }

  public void PlaneNew( Plane p )
  {
    if(ATC.debug_flag) System.out.println( "p.n.1" ); //DEBUG
    UIPlane uiplane = new UIPlane();
    char id = p.getIdChar();
    uiplane.radar_label = null;
    uiplane.info_label = new Label( (new Character(id)).toString() );
    if(ATC.debug_flag) System.out.println( "p.n.2" ); //DEBUG
    infoArea.add( uiplane.info_label );
    if(ATC.debug_flag) System.out.println( "p.n.2.1" ); //DEBUG
    infoArea.validate();
    if(ATC.debug_flag) System.out.println( "p.n.3" ); //DEBUG
    synchronized(this){
      planes.put( (Object)(new Character(id)), (Object)uiplane );
    }

    if(ATC.debug_flag) System.out.println( "p.n.4" ); //DEBUG
    PlaneUpdate( p );
    if(ATC.debug_flag) System.out.println( "p.n.5" ); //DEBUG
  }

  public void PlaneUpdate( Plane p )
  {
    if(ATC.debug_flag) System.out.println( "p.u.1" ); //DEBUG
    char id = p.getIdChar();
    UIPlane uiplane = null;
    synchronized(this){
      uiplane = (UIPlane)(planes.get( (Object)(new Character(id)) ));
    }
    if(ATC.debug_flag) System.out.println( "p.u.1 il=" +uiplane.info_label ); //DEBUG
    if( uiplane == null ) return;
    if(ATC.debug_flag) System.out.println( "p.u.2" ); //DEBUG

    if( p.takeoff_flag )
    {
      if( uiplane.radar_label == null )
      {
    if(ATC.debug_flag) System.out.println( "p.u.3" ); //DEBUG
        uiplane.radar_label = 
          new JLabel( getPlaneText(p), dirToPlaneIcon(p.dir), JLabel.CENTER );
        uiplane.radar_label.setVerticalTextPosition(JLabel.TOP);
        uiplane.radar_label.setHorizontalTextPosition(JLabel.CENTER);
        uiplane.radar_label.setIconTextGap(text_gap);
        uiplane.radar_label.setForeground(text_color);
        radarArea.add( uiplane.radar_label, new Integer(2) );
    if(ATC.debug_flag) System.out.println( "p.u.4" ); //DEBUG
      }
      else
      {
    if(ATC.debug_flag) System.out.println( "p.u.5" ); //DEBUG
        uiplane.radar_label.setText( getPlaneText(p) );
        uiplane.radar_label.setIcon( dirToPlaneIcon(p.dir) ); 
    if(ATC.debug_flag) System.out.println( "p.u.6" ); //DEBUG
      }

      uiplane.radar_label.setBounds(
          convPos( p.pos.x ) - plane_width/2,
          convPos( p.pos.y ) - plane_height + icon_size/2,
          plane_width,
          plane_height
          );
    if(ATC.debug_flag) System.out.println( "p.u.7" ); //DEBUG
    }

    uiplane.info_label.setText( getPlaneInfoText(p) );
    if(ATC.debug_flag) System.out.println( "p.u.8" ); //DEBUG
    infoArea.validate();
    //uiplane.info_label.revalidate();
    //uiplane.info_label.repaint();
    if(ATC.debug_flag) System.out.println( "p.u.9" ); //DEBUG
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

    if( uiplane.radar_label != null )
      radarArea.remove( uiplane.radar_label );
    if( uiplane.info_label != null )
      infoArea.remove( uiplane.info_label );
    synchronized(this) {
      planes.remove( (Object)(new Character(id)) );
    }

    radarArea.repaint();
    infoArea.repaint();
  }

  public void InfoUpdate( int tick_count, int safe_count )
  {
    infoTopLine.setText( " Time: " + tick_count + ", Safe: " + safe_count );
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
    synchronized(this){
    if( ! keyListenerAdded )
    {
      addKeyListener( this );
      keyListenerAdded = true;
    }
    }// end synchronized
  }

  public void gameOver( String gameOverMessage )
  {
    JOptionPane.showMessageDialog( null, gameOverMessage );

    newButton.setEnabled( true );
    synchronized(this){
    if( keyListenerAdded )
    {
      removeKeyListener( this );
      keyListenerAdded = false;
    }
    }// end synchronized
  }

  public void refresh()
  {
    //setVisible( true );
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

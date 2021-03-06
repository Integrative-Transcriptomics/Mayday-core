/**
 *  Filename: $RCSfile: DLabel.java,v $
 *  Purpose:  Possibility to set String labels to an DElement
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.2 $
 *            $Date: 2010/01/28 16:47:28 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 */
/**
 * Class DLabel makes it possible, to set an String label to a DElement.
 * The position of the label is determined by the position of the element it
 * belongs to and four double values:
 * pos_x and pos_y describe, where the anchor of the label relativly to the
 * rectangle of the DElement lies,
 * align_x and align_y describe the place of the anchor relativly to the size
 * of the text.
 *
 * Example:
 *
 * You want to put the label on top of the upper right corner of the displayed
 * element, in that way that it ends together with the right side of the
 * rectangle, then the values are:
 * pos_x = RIGHT,
 * pos_y = TOP,
 * align_x = RIGHT,
 * align_y = BOTTOM
 */
public class DLabel extends DComponent{

	public static final double TOP    = 1.0;
	public static final double BOTTOM = 0.0;
	public static final double LEFT   = 0.0;
	public static final double RIGHT  = 1.0;

	private Font font;
	private double pos_x;
	private double pos_y;
	private double align_x;
	private double align_y;
	private String text;
	private DElement elt;

	public DLabel( String text, DElement e ){
		this( text, e, LEFT, TOP, LEFT, TOP );
	}

	public DLabel( String text, DElement e,
			double pos_x, double pos_y,
			double align_x, double align_y ){
		this.text = text;
		elt = e;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.align_x = align_x;
		this.align_y = align_y;
	}

	@Override
	public DRectangle getRectangle(){
		return elt.getRectangle();
	}

	public void paint( Graphics g, DMeasures m ){
		if( !parent.contains( elt ) ) {
			parent.removeDElement( this );
			return;
		}
		if( !elt.isVisible() ) return;

//		Graphics g = m.getGraphics();
		if( color != null ) g.setColor( color );
		Font old_font = g.getFont();
		if( font != null ) g.setFont( font );
		FontMetrics fm = g.getFontMetrics();
		DRectangle rect = elt.getRectangle();

		int str_w = fm.stringWidth( text ),
		str_a = fm.getAscent();

		Point sw = m.getPoint(rect.x, rect.y),
		ne = m.getPoint(rect.x + rect.width, rect.y + rect.height),
		anchor = m.getPoint( rect.x + pos_x * rect.width, rect.y + pos_y * rect.height ),
		p = new Point(anchor.x - (int)(align_x * str_w ),
				anchor.y + (int)(align_y * str_a ) );

		setDBorder( new DBorder( ne.y + fm.getAscent() - p.y,
				sw.x - p.x,
				p.y - sw.y + fm.getDescent(),
				p.x + str_w - ne.x) );
		g.drawString( text, p.x, p.y );
		g.setFont( old_font );
	}

	/**
	 */
	 public void setFont( Font f ){
		boolean changed = ( font == null && f != null ) || !font.equals( f );
		font = f;
		if( changed ) repaint();
	}

	/**
	 */
	 public Font getFont(){ return font; }

	@Override
	public String toString(){
		return "wsi.ra.chart2d.DLabel[text=\"" + text + "\", font="+font+"]";
	}
}
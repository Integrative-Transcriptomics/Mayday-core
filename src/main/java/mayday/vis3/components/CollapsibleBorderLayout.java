package mayday.vis3.components;

import static java.awt.BorderLayout.AFTER_LAST_LINE;
import static java.awt.BorderLayout.AFTER_LINE_ENDS;
import static java.awt.BorderLayout.BEFORE_FIRST_LINE;
import static java.awt.BorderLayout.BEFORE_LINE_BEGINS;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.LINE_END;
import static java.awt.BorderLayout.LINE_START;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.PAGE_END;
import static java.awt.BorderLayout.PAGE_START;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

@SuppressWarnings("serial")
public class CollapsibleBorderLayout implements LayoutManager2, java.io.Serializable {

	int hgap;
	int vgap;
	Component north;
	Component west;
	Component east;
	Component south;
	Component center;

	Component firstLine;
	Component lastLine;
	Component firstItem;
	Component lastItem;
	
	protected boolean collapsed = false;
	
	public void setCollapsed(boolean c) {
		collapsed=c;
	}
	
	public boolean isCollapsed() {
		return collapsed;
	}

	public CollapsibleBorderLayout() {
		this(0, 0);
	}

	public CollapsibleBorderLayout(int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		synchronized (comp.getTreeLock()) {
			if ((constraints == null) || (constraints instanceof String)) {
				addLayoutComponent((String)constraints, comp);
			} else {
				throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
			}
		}
	}

	/**
	 * @deprecated  replaced by <code>addLayoutComponent(Component, Object)</code>.
	 */
	@Deprecated
	public void addLayoutComponent(String name, Component comp) {
		synchronized (comp.getTreeLock()) {
			/* Special case:  treat null the same as "Center". */
			if (name == null) {
				name = "Center";
			}

			/* Assign the component to one of the known regions of the layout.
			 */
			if ("Center".equals(name)) {
				center = comp;
			} else if ("North".equals(name)) {
				north = comp;
			} else if ("South".equals(name)) {
				south = comp;
			} else if ("East".equals(name)) {
				east = comp;
			} else if ("West".equals(name)) {
				west = comp;
			} else if (BEFORE_FIRST_LINE.equals(name)) {
				firstLine = comp;
			} else if (AFTER_LAST_LINE.equals(name)) {
				lastLine = comp;
			} else if (BEFORE_LINE_BEGINS.equals(name)) {
				firstItem = comp;
			} else if (AFTER_LINE_ENDS.equals(name)) {
				lastItem = comp;
			} else {
				throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
			}
		}
	}
	
	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			if (comp == center) {
				center = null;
			} else if (comp == north) {
				north = null;
			} else if (comp == south) {
				south = null;
			} else if (comp == east) {
				east = null;
			} else if (comp == west) {
				west = null;
			}
			if (comp == firstLine) {
				firstLine = null;
			} else if (comp == lastLine) {
				lastLine = null;
			} else if (comp == firstItem) {
				firstItem = null;
			} else if (comp == lastItem) {
				lastItem = null;
			}
		}
	}


	public Component getLayoutComponent(Object constraints) {
		if (CENTER.equals(constraints)) {
			return center;
		} else if (NORTH.equals(constraints)) {
			return north;
		} else if (SOUTH.equals(constraints)) {
			return south;
		} else if (WEST.equals(constraints)) {
			return west;
		} else if (EAST.equals(constraints)) {
			return east;
		} else if (PAGE_START.equals(constraints)) {
			return firstLine;
		} else if (PAGE_END.equals(constraints)) {
			return lastLine;
		} else if (LINE_START.equals(constraints)) {
			return firstItem;
		} else if (LINE_END.equals(constraints)) {
			return lastItem;
		} else {
			throw new IllegalArgumentException("cannot get component: unknown constraint: " + constraints);
		}
	}

	public Component getLayoutComponent(Container target, Object constraints) {
		boolean ltr = target.getComponentOrientation().isLeftToRight();
		Component result = null;

		if (NORTH.equals(constraints)) {
			result = (firstLine != null) ? firstLine : north;
		} else if (SOUTH.equals(constraints)) {
			result = (lastLine != null) ? lastLine : south;
		} else if (WEST.equals(constraints)) {
			result = ltr ? firstItem : lastItem;
			if (result == null) {
				result = west;
			}
		} else if (EAST.equals(constraints)) {
			result = ltr ? lastItem : firstItem;
			if (result == null) {
				result = east;
			}
		} else if (CENTER.equals(constraints)) {
			result = center;
		} else {
			throw new IllegalArgumentException("cannot get component: invalid constraint: " + constraints);
		}

		return result;
	}

	public Object getConstraints(Component comp) {
		//fix for 6242148 : API method java.awt.BorderLayout.getConstraints(null) should return null
		if (comp == null){
			return null;
		}
		if (comp == center) {
			return CENTER;
		} else if (comp == north) {
			return NORTH;
		} else if (comp == south) {
			return SOUTH;
		} else if (comp == west) {
			return WEST;
		} else if (comp == east) {
			return EAST;
		} else if (comp == firstLine) {
			return PAGE_START;
		} else if (comp == lastLine) {
			return PAGE_END;
		} else if (comp == firstItem) {
			return LINE_START;
		} else if (comp == lastItem) {
			return LINE_END;
		}
		return null;
	}

	public Dimension minimumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);

			boolean ltr = target.getComponentOrientation().isLeftToRight();
			Component c = null;

			if ((c=getChild(EAST,ltr)) != null) {
				Dimension d = c.getMinimumSize();
				dim.width += d.width + hgap;
				dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(WEST,ltr)) != null) {
				Dimension d = c.getMinimumSize();
				dim.width += d.width + hgap;
				dim.height = Math.max(d.height, dim.height);
			}
			if (!collapsed && (c=getChild(CENTER,ltr)) != null) {
				Dimension d = c.getMinimumSize();
				dim.width += d.width;
				dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(NORTH,ltr)) != null) {
				Dimension d = c.getMinimumSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height += d.height + vgap;
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
				Dimension d = c.getMinimumSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height += d.height + vgap;
			}

			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;

			return dim;
		}
	}

	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);

			boolean ltr = target.getComponentOrientation().isLeftToRight();
			Component c = null;

			if ((c=getChild(EAST,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width += d.width + hgap;
				dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(WEST,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width += d.width + hgap;
				dim.height = Math.max(d.height, dim.height);
			}
			if (!collapsed && (c=getChild(CENTER,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width += d.width;
				dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(NORTH,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height += d.height + vgap;
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height += d.height + vgap;
			}

			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;

			return dim;
		}
	}

	public Dimension maximumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			if (!collapsed)
				return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

			Dimension dim = new Dimension(0, 0);

			boolean ltr = target.getComponentOrientation().isLeftToRight();
			Component c = null;

			if ((c=getChild(EAST,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width += Math.max(dim.width, d.width + hgap);
			}
			if ((c=getChild(WEST,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.width += Math.max(dim.width, d.width + hgap);
			}
			if ((c=getChild(NORTH,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.height = Math.max(dim.height, d.height + vgap);
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
				Dimension d = c.getPreferredSize();
				dim.height = Math.max(dim.height, d.height + vgap);
			}

			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;

			return dim;
		}
	}

	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}

	public void invalidateLayout(Container target) {
	}

	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int top = insets.top;
			int bottom = target.getHeight() - insets.bottom;
			int left = insets.left;
			int right = target.getWidth() - insets.right;

			boolean ltr = target.getComponentOrientation().isLeftToRight();
			Component c = null;

			if ((c=getChild(NORTH,ltr)) != null) {
				c.setSize(right - left, c.getHeight());
				Dimension d = c.getPreferredSize();
				c.setBounds(left, top, right - left, d.height);
				top += d.height + vgap;
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
				c.setSize(right - left, c.getHeight());
				Dimension d = c.getPreferredSize();
				c.setBounds(left, bottom - d.height, right - left, d.height);
				bottom -= d.height + vgap;
			}
			if ((c=getChild(EAST,ltr)) != null) {
				c.setSize(c.getWidth(), bottom - top);
				Dimension d = c.getPreferredSize();
				c.setBounds(right - d.width, top, d.width, bottom - top);
				right -= d.width + hgap;
			}
			if ((c=getChild(WEST,ltr)) != null) {
				c.setSize(c.getWidth(), bottom - top);
				Dimension d = c.getPreferredSize();
				c.setBounds(left, top, d.width, bottom - top);
				left += d.width + hgap;
			}
			if ((c=getChild(CENTER,ltr)) != null) {
				if (collapsed)
					c.setBounds(left, top, 0, 0);
				else
					c.setBounds(left, top, right - left, bottom - top);
				
			}
		}
	}

	private Component getChild(String key, boolean ltr) {
		Component result = null;

		if (key == NORTH) {
			result = (firstLine != null) ? firstLine : north;
		}
		else if (key == SOUTH) {
			result = (lastLine != null) ? lastLine : south;
		}
		else if (key == WEST) {
			result = ltr ? firstItem : lastItem;
			if (result == null) {
				result = west;
			}
		}
		else if (key == EAST) {
			result = ltr ? lastItem : firstItem;
			if (result == null) {
				result = east;
			}
		}
		else if (key == CENTER) {
			result = center;
		}
		if (result != null && !result.isVisible()) {
			result = null;
		}
		return result;
	}

	public String toString() {
		return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
	}
}

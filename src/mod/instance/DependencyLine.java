package mod.instance;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Define.AreaDefine;
import Pack.DragPack;
import bgWork.handler.CanvasPanelHandler;
import mod.IFuncComponent;
import mod.ILinePainter;
import java.lang.Math;

public class DependencyLine extends JPanel
		implements IFuncComponent, ILinePainter
{
	public JPanel				from;
	public int					fromSide;
	public Point				fp				= new Point(0, 0);
	public JPanel				to;
	public int					toSide;
	public Point				tp				= new Point(0, 0);
	public int					arrowSize		= 6;
	public int					panelExtendSize	= 10;
	public boolean				isSelect		= false;
	public int					selectBoxSize	= 5;
	public CanvasPanelHandler	cph;
	private Boolean highlight = false;

	public DependencyLine(CanvasPanelHandler cph)
	{
		this.setOpaque(false);
		this.setVisible(true);
		this.setMinimumSize(new Dimension(1, 1));
		this.cph = cph;
	}

	@Override
	public void paintComponent(Graphics g)
	{
        Graphics2D g2d = (Graphics2D) g.create();
		Point fpPrime;
		Point tpPrime;
        float[] dash1 = {2f, 0f, 2f};
        BasicStroke bs1 = new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 1.0f, dash1, 2f);
		renewConnect();
		fpPrime = new Point(fp.x - this.getLocation().x,
				fp.y - this.getLocation().y);
		tpPrime = new Point(tp.x - this.getLocation().x,
				tp.y - this.getLocation().y);
		if(highlight){
			g.setColor(Color.red);
		}
		else{
			g.setColor(Color.BLACK);
		}
        g2d.setStroke(bs1);
		g2d.drawLine(fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);
		paintArrow(g, tpPrime);
		if (isSelect == true)
		{
			paintSelect(g);
		}
	}

	@Override
	public void reSize()
	{
		Dimension size = new Dimension(
				Math.abs(fp.x - tp.x) + panelExtendSize * 2,
				Math.abs(fp.y - tp.y) + panelExtendSize * 2);
		this.setSize(size);
		this.setLocation(Math.min(fp.x, tp.x) - panelExtendSize,
				Math.min(fp.y, tp.y) - panelExtendSize);
	}

	@Override
	public void paintArrow(Graphics g, Point point)
	{
        Point start = new Point(fp.x - this.getLocation().x,
				fp.y - this.getLocation().y);
        float arrowAngle = 60;
        float arrowLength = 10;
		double vecLen = Math.sqrt(Math.pow((point.x - start.x), 2) + Math.pow((point.y - start.y), 2));
        double radiant = Math.toRadians(90 + arrowAngle);
        double vecX = (double)(point.x - start.x) / vecLen;
        double vecY = (double)(point.y - start.y) / vecLen;
        int x1 = point.x + (int)(arrowLength * (vecX*Math.cos(radiant) - vecY*Math.sin(radiant)));
        int y1 = point.y + (int)(arrowLength * (vecX*Math.sin(radiant) + vecY*Math.cos(radiant)));
        int x2 = point.x + (int)(arrowLength * (vecX*Math.cos(-radiant) - vecY*Math.sin(-radiant)));
        int y2 = point.y + (int)(arrowLength * (vecX*Math.sin(-radiant) + vecY*Math.cos(-radiant)));
        g.drawLine(point.x, point.y, x1, y1);
        g.drawLine(point.x, point.y, x2, y2);
	}

	@Override
	public void setConnect(DragPack dPack)
	{
		Point mfp = dPack.getFrom();
		Point mtp = dPack.getTo();
		from = (JPanel) dPack.getFromObj();
		to = (JPanel) dPack.getToObj();
		fromSide = new AreaDefine().getArea(from.getLocation(), from.getSize(),
				mfp);
		toSide = new AreaDefine().getArea(to.getLocation(), to.getSize(), mtp);
		renewConnect();
		System.out.println("from side " + fromSide);
		System.out.println("to side " + toSide);
	}

	void renewConnect()
	{
		try
		{
			fp = getConnectPoint(from, fromSide);
			tp = getConnectPoint(to, toSide);
			this.reSize();
		}
		catch (NullPointerException e)
		{
			this.setVisible(false);
			cph.removeComponent(this);
		}
	}

	Point getConnectPoint(JPanel jp, int side)
	{
		Point temp = new Point(0, 0);
		Point jpLocation = cph.getAbsLocation(jp);
		if (side == new AreaDefine().TOP)
		{
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
			temp.y = jpLocation.y;
		}
		else if (side == new AreaDefine().RIGHT)
		{
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth());
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
		}
		else if (side == new AreaDefine().LEFT)
		{
			temp.x = jpLocation.x;
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
		}
		else if (side == new AreaDefine().BOTTOM)
		{
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight());
		}
		else
		{
			temp = null;
			System.err.println("getConnectPoint fail:" + side);
		}
		return temp;
	}

	@Override
	public void paintSelect(Graphics gra)
	{
		gra.setColor(Color.RED);
		Point fpPrime = new Point(fp.x - this.getLocation().x,
				fp.y - this.getLocation().y);
		Point tpPrime = new Point(tp.x - this.getLocation().x,
				tp.y - this.getLocation().y);
		gra.drawLine(fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);
	}

	public boolean isSelect()
	{
		return isSelect;
	}

	public void setSelect(boolean isSelect)
	{
		this.isSelect = isSelect;
	}

	public JPanel getFromJPanel(){
		return this.from;
	}

	public JPanel getToJPanel(){
		return this.to;
	}

	public int getFromSide(){
		return this.fromSide;
	}

	public int getToSide(){
		return this.toSide;
	}
	
	public void highlight()
	{
		this.highlight = true;
	}
	public void unhighlight()
	{
		this.highlight = false;
	}
}

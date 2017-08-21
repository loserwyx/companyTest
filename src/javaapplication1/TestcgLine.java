package com.interactive.jcarnac2d.model.shapes;

import com.interactive.jcarnac2d.edit.geometry.cgGeometryEditor;
import com.interactive.jcarnac2d.edit.geometry.cgLineShapeEditor;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.edit.manipulator.palette.cgManipulatorAttributePalette;
import com.interactive.jcarnac2d.model.cgTransformable;
import com.interactive.jcarnac2d.model.interfaces.cgAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgVisualHandleGroupFactory;
import com.interactive.jcarnac2d.model.scenegraph.cg2DNodeFactory;
import com.interactive.jcarnac2d.model.scenegraph.cgNode;
import com.interactive.jcarnac2d.model.scenegraph.cgPointSetShapeNode;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.delegate.cgRenderingEngine;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class cgLine
  extends cgCommonShape
  implements cgTransformable
{
  private Line2D.Double _line;
  private cgPointSetShapeNode _shapeNode;
  private cgRect _bbox = new cgRect(0.0D, 0.0D, 0.0D, 0.0D);
  
  public cgLine()
  {
    this._line = new Line2D.Double();
  }
  
  public cgLine(double x1, double y1, double x2, double y2)
  {
    this._line = new Line2D.Double(x1, y1, x2, y2);
  }
  
  public cgNode getNode()
  {
    if (this._shapeNode == null)
    {
      cg2DNodeFactory factory = (cg2DNodeFactory)cgRenderingEngine.getInstance().getNodeFactory(cg2DNodeFactory.class);
      
      double[] xcoord = { this._line.x1, this._line.x2 };
      double[] ycoord = { this._line.y1, this._line.y2 };
      
      this._shapeNode = factory.createPolylineShapeNode(xcoord, ycoord, 2, false);
      
      this._shapeNode.rotate(getRotationAngle());
      this._shapeNode.setVisible(isVisible());
      this._shapeNode.setAttribute(getAttribute());
    }
    return this._shapeNode;
  }
  
  public void setAttribute(cgAttribute attr)
  {
    if (this._shapeNode != null) {
      this._shapeNode.setAttribute(attr);
    }
    super.setAttribute(attr);
  }
  
  public void setVisible(boolean visible)
  {
    if (this._shapeNode != null) {
      this._shapeNode.setVisible(visible);
    }
    super.setVisible(visible);
  }
  
  public void rotate(float angle)
  {
    if (this._shapeNode != null) {
      this._shapeNode.rotate(angle);
    }
    super.rotate(angle);
  }
  
  public Object clone()
  {
    cgLine myClone = null;
    myClone = (cgLine)super.clone();
    myClone._line = ((Line2D.Double)myClone.getLine());
    myClone._bbox = new cgRect(this._bbox);
    
    myClone._shapeNode = null;
    
    return myClone;
  }
  
  public void setLine(double x1, double y1, double x2, double y2)
  {
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._line.setLine(x1, y1, x2, y2);
    if (this._shapeNode != null) {
      this._shapeNode.setCoordinates(0, new double[] { x1, x2 }, new double[] { y1, y2 }, 2);
    }
    _setVisible(ov);
    invalidateShape();
  }
  
  public Line2D getLine()
  {
    return new Line2D.Double(this._line.x1, this._line.y1, this._line.x2, this._line.y2);
  }
  
  public Point2D getRotationCenter(cgTransformation tr)
  {
    cgRect r = getBoundingBoxWithoutRotation(tr);
    if (r == null) {
      return new Point2D.Double();
    }
    return new Point2D.Double(r.getCenterX(), r.getCenterY());
  }
  
  public cgRect getBoundingBox(cgTransformation tr)
  {
    return getRotatedBBox();
  }
  
  public void render(cgShapeRenderer fp, cgRect bbox)
  {
    if ((isVisible()) && (getAttribute() != null))
    {
      fp.setAttribute(getAttribute());
      Shape rs = getRotatedShape();
      if (rs != null) {
        fp.draw(rs);
      }
    }
  }
  
  public Point2D getP1()
  {
    return this._line.getP1();
  }
  
  public Point2D getP2()
  {
    return this._line.getP2();
  }
  
  public void setP1(Point2D p1)
  {
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._line.setLine(p1, getP2());
    if (this._shapeNode != null) {
      this._shapeNode.setCoordinates(0, new double[] { p1.getX() }, new double[] { p1.getY() }, 1);
    }
    _setVisible(ov);
    invalidateShape();
  }
  
  public void setP2(Point2D p2)
  {
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._line.setLine(getP1(), p2);
    if (this._shapeNode != null) {
      this._shapeNode.setCoordinates(1, new double[] { p2.getX() }, new double[] { p2.getY() }, 1);
    }
    _setVisible(ov);
    invalidateShape();
  }
  
  protected cgRect getRotatedBBox()
  {
    cgRect bbox = findBBox();
    if (getRotationAngle() == 0.0D) {
      return bbox;
    }
    cgTransformation tr = new cgTransformation();
    tr.rotate(getRotationAngle(), bbox.getCenterX(), bbox.getCenterY());
    return tr.transformRect(bbox);
  }
  
  protected Shape getRotatedShape()
  {
    if (getRotationAngle() == 0.0D) {
      return new Line2D.Double(this._line.getX1(), this._line.getY1(), this._line.getX2(), this._line.getY2());
    }
    cgRect bbox = findBBox();
    cgTransformation tr = new cgTransformation();
    tr.rotate(getRotationAngle(), bbox.getCenterX(), bbox.getCenterY());
    return tr.createTransformedShape(this._line);
  }
  
  protected void render(cgShapeRenderer fp, cgRect bbox, double angle, Point2D anchor)
  {
    if ((isVisible()) && (getAttribute() != null))
    {
      fp.setAttribute(getAttribute());
      Shape rs = getRotatedShape(angle, anchor);
      if (rs != null) {
        fp.draw(rs);
      }
    }
  }
  
  protected Shape getRotatedShape(double angle, Point2D anchor)
  {
    if (angle == 0.0D) {
      return getLine();
    }
    cgTransformation tr = new cgTransformation();
    tr.rotate(angle, anchor.getX(), anchor.getY());
    return tr.createTransformedShape(this._line);
  }
  
  private cgRect findBBox()
  {
    double x1 = this._line.getX1();
    double x2 = this._line.getX2();
    double y1 = this._line.getY1();
    double y2 = this._line.getY2();
    













    this._bbox.setFrameFromDiagonal(x1, y1, x2, y2);
    
    return this._bbox;
  }
  
  public void applyTransformation(cgTransformation tr)
  {
    invalidate();
    boolean v = isVisible();
    if (v) {
      _setVisible(false);
    }
    if ((tr.getType() & 0x18) == 0)
    {
      this._line.setLine(tr.transform(this._line.getP1(), null), tr.transform(this._line.getP2(), null));
    }
    else
    {
      cgTransformation iTr = new cgTransformation();
      
      Point2D pt0 = getRotationCenter(iTr);
      double scaleX = tr.getLengthToXRatioAt(pt0);
      double scaleY = tr.getLengthToYRatioAt(pt0);
      
      Point2D pt = tr.transform(pt0, null);
      
      cgRect rect = getBoundingBoxWithoutRotation(iTr);
      if (rect == null) {
        rect = new cgRect();
      }
      rect = new cgRect(rect);
      if ((rect.width <= 0.0D) || (rect.height <= 0.0D))
      {
        if (rect.width <= 0.0001D)
        {
          rect.x -= 1.0D;
          rect.width = 2.0D;
        }
        if (rect.height <= 0.0001D)
        {
          rect.y -= 1.0D;
          rect.height = 2.0D;
        }
      }
      cgRect oldrect = new cgRect(rect);
      
      rect.x += pt.getX() - pt0.getX();
      rect.y += pt.getY() - pt0.getY();
      if (0 != (tr.getType() & 0x26))
      {
        double height = rect.height * scaleX;
        double width = rect.width * scaleY;
        

        rect.x += (rect.width - width) / 2.0D;
        rect.y += (rect.height - height) / 2.0D;
        rect.width = width;
        rect.height = height;
      }
      double dangle = 0.0D;
      if ((tr.getType() & 0x18) != 0)
      {
        double cos = tr.getScaleX();
        double sin = tr.getShearX();
        dangle = cos != 0.0D ? -Math.atan(sin / cos) : 1.570796326794897D;
        if (cos < 0.0D) {
          dangle += 3.141592653589793D;
        }
      }
      float angle = getRotationAngle() + (float)dangle;
      rotate(angle);
      

      cgTransformation rectToRect = new cgTransformation(oldrect, rect, false, false);
      if (!rectToRect.isIdentity()) {
        this._line.setLine(rectToRect.transform(this._line.getP1(), null), rectToRect.transform(this._line.getP2(), null));
      }
    }
    if (this._shapeNode != null) {
      this._shapeNode.setCoordinates(0, new double[] { this._line.x1, this._line.x2 }, new double[] { this._line.y1, this._line.y2 }, 2);
    }
    _setVisible(v);
    invalidate();
  }
  
  public int getAllowedTransformationType()
  {
    return -1;
  }
  
  protected cgRect getBoundingBoxWithoutRotation(cgTransformation tr)
  {
    return findBBox();
  }
  
  private class VertexHandle1
    extends cgAbstractVisualHandle
  {
    public VertexHandle1(cgVisualHandleGroup g)
    {
      setGroup(g);
    }
    
    public int getHandleBarEnum()
    {
      return 64;
    }
    
    public int getPolyIndex()
    {
      return 1;
    }
    
    public void setPoint(Point2D pt)
    {
      setHandlesShapesVisible(false);
      
      pt = unRotatePoint(pt);
      
      cgLine.this.setP1(pt);
      
      setHandlesShapesVisible(true);
    }
    
    public Point2D getPoint()
    {
      return rotatePoint(cgLine.this._line.getP1());
    }
    
    private Point2D unRotatePoint(Point2D pt)
    {
      float angle = cgLine.this.getRotationAngle();
      if (angle != 0.0D)
      {
        cgTransformation tr = new cgTransformation();
        Point2D center = cgLine.this.getRotationCenter(tr);
        tr.rotate(-angle, center.getX(), center.getY());
        pt = tr.transform(pt, null);
      }
      return pt;
    }
    
    private Point2D rotatePoint(Point2D pt)
    {
      float angle = cgLine.this.getRotationAngle();
      if (angle != 0.0D)
      {
        cgTransformation tr = new cgTransformation();
        Point2D center = cgLine.this.getRotationCenter(tr);
        tr.rotate(angle, center.getX(), center.getY());
        pt = tr.transform(pt, null);
      }
      return pt;
    }
  }
  
  private class VertexHandle2
    extends cgAbstractVisualHandle
  {
    public VertexHandle2(cgVisualHandleGroup g)
    {
      setGroup(g);
    }
    
    public int getHandleBarEnum()
    {
      return 64;
    }
    
    public int getPolyIndex()
    {
      return 2;
    }
    
    public void setPoint(Point2D pt)
    {
      setHandlesShapesVisible(false);
      
      pt = unRotatePoint(pt);
      
      cgLine.this.setP2(pt);
      
      setHandlesShapesVisible(true);
    }
    
    public Point2D getPoint()
    {
      return rotatePoint(cgLine.this._line.getP2());
    }
    
    private Point2D unRotatePoint(Point2D pt)
    {
      float angle = cgLine.this.getRotationAngle();
      if (angle != 0.0D)
      {
        cgTransformation tr = new cgTransformation();
        Point2D center = cgLine.this.getRotationCenter(tr);
        tr.rotate(-angle, center.getX(), center.getY());
        pt = tr.transform(pt, null);
      }
      return pt;
    }
    
    private Point2D rotatePoint(Point2D pt)
    {
      float angle = cgLine.this.getRotationAngle();
      if (angle != 0.0D)
      {
        cgTransformation tr = new cgTransformation();
        Point2D center = cgLine.this.getRotationCenter(tr);
        tr.rotate(angle, center.getX(), center.getY());
        pt = tr.transform(pt, null);
      }
      return pt;
    }
  }
  
  private static cgAbstractShape.ClassTable _classTable = new cgAbstractShape.ClassTable(2).add(cgVisualHandleGroupFactory.class, new LineVisualHandleGroupFactory(null)).add(cgGeometryEditor.class, new cgLineShapeEditor());
  
  public Object queryInterface(Class<?> param)
  {
    Object o = _classTable.get(param);
    if (o != null) {
      return o;
    }
    return super.queryInterface(param);
  }
  
  private static class LineVisualHandleGroupFactory
    extends cgAbstractShape.VisualHandleGroupFactory
  {
    public cgVisualHandleGroup getHandleGroup(cgShape shape, int param, cgManipulatorAttributePalette palette)
    {
      if (((param & 0x2) != 0) && ((shape instanceof cgLine)))
      {
        cgLine line = (cgLine)shape;
        cgAbstractVisualHandle h1 = line.getVisualHandle(64, 1, null);
        cgAbstractVisualHandle h2 = line.getVisualHandle(64, 2, null);
        if ((h1 != null) && (h2 != null)) {
          return new cgVerticesShapeList(line, 3.0D, 3.0D, palette, h1, h2);
        }
      }
      return super.getHandleGroup(shape, param, palette);
    }
  }
  
  protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group)
  {
    if (id == 64)
    {
      if (num == 1) {
        return new VertexHandle1(group);
      }
      if (num == 2) {
        return new VertexHandle2(group);
      }
    }
    return super.getVisualHandle(id, num, group);
  }
}

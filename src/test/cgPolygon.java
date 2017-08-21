package com.interactive.jcarnac2d.model.shapes;

import com.interactive.jcarnac2d.edit.geometry.cgGeometryEditor;
import com.interactive.jcarnac2d.edit.geometry.cgPolyPointShapeEditor;
import com.interactive.jcarnac2d.model.interfaces.cgAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgShapeRepresentativeFactory;
import com.interactive.jcarnac2d.model.scenegraph.cg2DNodeFactory;
import com.interactive.jcarnac2d.model.scenegraph.cgNode;
import com.interactive.jcarnac2d.model.scenegraph.cgPointSetShapeNode;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.delegate.cgRenderingEngine;
import java.awt.Shape;
import java.awt.geom.Point2D;

public class cgPolygon
  extends cgPointSet
{
  private cgPointSetShapeNode _shapeNode;
  
  public cgPolygon()
  {
    setClosed(true);
  }
  
  public cgPolygon(int npts, double[] xpts, double[] ypts)
  {
    setCoordinates(npts, xpts, ypts);
    
    setClosed(true);
  }
  
  public cgPolygon(double[] xpts, double[] ypts, int npts)
  {
    double[] _xpts = new double[npts];
    double[] _ypts = new double[npts];
    
    int i = npts - 1;
    int j = 0;
    for (; j < npts; i--)
    {
      _xpts[j] = xpts[i];
      _ypts[j] = ypts[i];j++;
    }
    setCoordinates(npts, _xpts, _ypts);
    
    setClosed(true);
  }
  
  public cgNode getNode()
  {
    if (this._shapeNode == null)
    {
      cg2DNodeFactory factory = (cg2DNodeFactory)cgRenderingEngine.getInstance().getNodeFactory(cg2DNodeFactory.class);
      
      this._shapeNode = factory.createPolygonShapeNode(getXCoordinates(), getYCoordinates(), getSize(), true);
      
      this._shapeNode.setVisible(isVisible());
      this._shapeNode.setAttribute(getAttribute());
    }
    return this._shapeNode;
  }
  
  public Object clone()
  {
    cgPolygon myClone = (cgPolygon)super.clone();
    myClone._shapeNode = null;
    return myClone;
  }
  
  public void render(cgShapeRenderer fp, cgRect bbox)
  {
    if ((!isVisible()) || (getAttribute() == null)) {
      return;
    }
    fp.setAttribute(getAttribute());
    Shape rs = getRotatedShape();
    if (rs != null)
    {
      fp.fill(rs);
      


      fp.draw(rs);
    }
  }
  
  protected void render(cgShapeRenderer fp, cgRect bbox, double angle, Point2D anchor)
  {
    if ((!isVisible()) || (getAttribute() == null)) {
      return;
    }
    fp.setAttribute(getAttribute());
    Shape rs = getRotatedShape(angle, anchor);
    if (rs != null)
    {
      fp.fill(rs);
      

      fp.draw(rs);
    }
  }
  
  private static cgAbstractShape.ClassTable _classTable = new cgAbstractShape.ClassTable(2).add(cgShapeRepresentativeFactory.class, new ShapeRepresentativeFactory(null)).add(cgGeometryEditor.class, new cgPolyPointShapeEditor());
  
  public Object queryInterface(Class<?> param)
  {
    Object o = _classTable.get(param);
    if (o != null) {
      return o;
    }
    return super.queryInterface(param);
  }
  
  public void applyTransformation(cgTransformation tr)
  {
    boolean isNotificationEnabled = isNotificationEnabled();
    setNotification(false);
    super.applyTransformation(tr);
    setNotification(isNotificationEnabled);
    if (this._shapeNode != null) {
      this._shapeNode.setCoordinates(0, getXCoordinates(), getYCoordinates(), getSize());
    }
    invalidate();
  }
  
  public void setCoordinates(int npts, double[] xpts, double[] ypts)
  {
    if (this._shapeNode != null)
    {
      int size = getSize();
      if (size < npts)
      {
        double[] coord = new double[npts - size];
        this._shapeNode.insertCoordinates(size, coord, coord, npts - size);
      }
      else if (size > npts)
      {
        this._shapeNode.removeCoordinates(0, size - npts);
      }
      this._shapeNode.setCoordinates(0, xpts, ypts, npts);
    }
    super.setCoordinates(npts, xpts, ypts);
  }
  
  public boolean setPoint(int index, double x, double y)
  {
    if (this._shapeNode != null) {
      this._shapeNode.setCoordinates(index, new double[] { x }, new double[] { y }, 1);
    }
    return super.setPoint(index, x, y);
  }
  
  public boolean insert(int index, double x, double y)
  {
    if (this._shapeNode != null) {
      this._shapeNode.insertCoordinates(index, new double[] { x }, new double[] { y }, 1);
    }
    return super.insert(index, x, y);
  }
  
  public boolean insert(int index, int npts, double[] x, double[] y)
  {
    if (this._shapeNode != null) {
      this._shapeNode.insertCoordinates(index, x, y, npts);
    }
    return super.insert(index, npts, x, y);
  }
  
  public boolean remove(int index, int npts)
  {
    if (this._shapeNode != null) {
      this._shapeNode.removeCoordinates(index, npts);
    }
    return super.remove(index, npts);
  }
  
  public void rotate(float angle)
  {
    if (this._shapeNode != null) {
      this._shapeNode.rotate(angle);
    }
    super.rotate(angle);
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
  
  
  private static class ShapeRepresentativeFactory
    implements cgShapeRepresentativeFactory
  {
    public cgShape getRepresentative(cgShape shape, cgRect rect)
    {
      cgRectangle r = new cgRectangle(rect);
      r.setAttribute(shape.getAttribute());
      return r;
    }
  }
}

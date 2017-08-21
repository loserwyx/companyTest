package com.interactive.jcarnac2d.model.shapes;

import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.model.cgTransformable;
import com.interactive.jcarnac2d.model.interfaces.cgPointGroupShape;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.util.PolyShape;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class cgPointSet
  extends cgCommonShape
  implements cgPointGroupShape, cgTransformable
{
  protected static final PolyShape _polyShape = new PolyShape();
  /**
   * @deprecated
   */
  protected cgRect _bbox = new cgRect(0.0D, 0.0D, 0.0D, 0.0D);
  private boolean _recalculate = true;
  
  protected void _recalculate()
  {
    this._recalculate = true;
  }
  
  /**
   * @deprecated
   */
  protected int _npts = 0;
  /**
   * @deprecated
   */
  protected double[] _xpts = null;
  /**
   * @deprecated
   */
  protected double[] _ypts = null;
  private boolean _closed;
  
  public Object clone()
  {
    cgPointSet myClone = (cgPointSet)super.clone();
    myClone.setBoundingBoxRectangle(new cgRect(this._bbox));
    if ((this._xpts != null) && (this._ypts != null)) {
      myClone.setCoordinates(this._npts, (double[])this._xpts.clone(), (double[])this._ypts.clone());
    }
    return myClone;
  }
  
  public Point2D getRotationCenter(cgTransformation tr)
  {
    findBBox(tr);
    if (this._bbox != null) {
      return new Point2D.Double(this._bbox.getCenterX(), this._bbox.getCenterY());
    }
    return new Point2D.Double();
  }
  
  protected void setBoundingBoxRectangle(cgRect rect)
  {
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._bbox = rect;
    if (this._bbox == null)
    {
      this._recalculate = true;
      findBBox(null);
    }
    _setVisible(ov);
    invalidateShape();
  }
  
  public boolean isClosed()
  {
    return this._closed;
  }
  
  protected void setClosed(boolean closed)
  {
    this._closed = closed;
    synchronized (_polyShape)
    {
      _polyShape.setCoordinates(this._bbox, this._npts, closed, this._xpts, this._ypts);
    }
  }
  
  public int getSize()
  {
    return this._npts;
  }
  
  public cgRect getBoundingBox(cgTransformation tr)
  {
    return getRotatedBBox(tr);
  }
  
  public Shape getPolyShape()
  {
    return getRotatedShape();
  }
  
  protected cgRect getRotatedBBox(cgTransformation tr)
  {
    findBBox(tr);
    if (getRotationAngle() == 0.0D) {
      return this._bbox;
    }
    cgTransformation t = new cgTransformation();
    t.rotate(getRotationAngle(), this._bbox.getCenterX(), this._bbox.getCenterY());
    
    return t.transformRect(this._bbox);
  }
  
  protected Shape getRotatedShape()
  {
    if (getRotationAngle() == 0.0D)
    {
      findBBox(null);
      synchronized (_polyShape)
      {
        _polyShape.setCoordinates(this._bbox, this._npts, this._closed, this._xpts, this._ypts);
        return new GeneralPath(_polyShape);
      }
    }
    cgTransformation tr = new cgTransformation();
    tr.rotate(getRotationAngle(), this._bbox.getCenterX(), this._bbox.getCenterY());
    synchronized (_polyShape)
    {
      cgRect bbox = getBoundingBox(null);
      if (bbox == null)
      {
        findBBox(null);
        bbox = this._bbox;
      }
      _polyShape.setCoordinates(bbox, this._npts, this._closed, this._xpts, this._ypts);
      return tr.createTransformedShape(_polyShape);
    }
  }
  
  public void setCoordinates(int npts, double[] xpts, double[] ypts)
  {
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._npts = npts;
    
    this._xpts = new double[this._npts];
    this._ypts = new double[this._npts];
    if (this._npts > 0)
    {
      System.arraycopy(xpts, 0, this._xpts, 0, this._npts);
      System.arraycopy(ypts, 0, this._ypts, 0, this._npts);
    }
    this._recalculate = true;
    _setVisible(ov);
    
    invalidateShape();
  }
  
  double[] getXCoordinates()
  {
    return this._xpts;
  }
  
  double[] getYCoordinates()
  {
    return this._ypts;
  }
  
  protected void findBBox(cgTransformation tr)
  {
    if (!this._recalculate) {
      return;
    }
    double minx = 1.7976931348623157E+308D;
    double miny = 1.7976931348623157E+308D;
    double maxx = -1.797693134862316E+308D;
    double maxy = -1.797693134862316E+308D;
    for (int i = 0; i < this._npts; i++)
    {
      double x = this._xpts[i];
      double y = this._ypts[i];
      if (x < minx) {
        minx = x;
      }
      if (x > maxx) {
        maxx = x;
      }
      if (y < miny) {
        miny = y;
      }
      if (y > maxy) {
        maxy = y;
      }
    }
    if (this._npts == 0)
    {
      minx = 0.0D;
      miny = 0.0D;
      maxx = 0.0D;
      maxy = 0.0D;
    }
    if (this._bbox == null) {
      this._bbox = new cgRect(minx, miny, maxx, maxy);
    }
    this._bbox.setFrameFromDiagonal(minx, miny, maxx, maxy);
    
    this._recalculate = false;
  }
  
  public boolean insert(int index, double x, double y)
  {
    if ((index < 0) || (index > this._npts)) {
      return false;
    }
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    double[] xs = new double[this._npts + 1];
    double[] ys = new double[this._npts + 1];
    if (index > 0)
    {
      System.arraycopy(this._xpts, 0, xs, 0, index);
      System.arraycopy(this._ypts, 0, ys, 0, index);
    }
    xs[index] = x;
    ys[index] = y;
    if (index < this._npts)
    {
      System.arraycopy(this._xpts, index, xs, index + 1, this._npts - index);
      System.arraycopy(this._ypts, index, ys, index + 1, this._npts - index);
    }
    this._npts += 1;
    this._xpts = xs;
    this._ypts = ys;
    
    this._recalculate = true;
    
    _setVisible(ov);
    
    invalidateShape();
    
    return true;
  }
  
  public boolean insert(int index, int npts, double[] x, double[] y)
  {
    if ((index > this._npts) || (index < 0)) {
      return false;
    }
    if (npts <= 0) {
      return false;
    }
    if ((x == null) || (y == null)) {
      return false;
    }
    if ((x.length < npts) || (y.length < npts)) {
      return false;
    }
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    double[] xs = new double[this._npts + npts];
    double[] ys = new double[this._npts + npts];
    if (index > 0)
    {
      System.arraycopy(this._xpts, 0, xs, 0, index);
      System.arraycopy(this._ypts, 0, ys, 0, index);
    }
    System.arraycopy(x, 0, xs, index, npts);
    System.arraycopy(y, 0, ys, index, npts);
    if (index < this._npts)
    {
      System.arraycopy(this._xpts, index, xs, index + npts, this._npts - index);
      System.arraycopy(this._ypts, index, ys, index + npts, this._npts - index);
    }
    this._npts += npts;
    this._xpts = xs;
    this._ypts = ys;
    
    this._recalculate = true;
    
    _setVisible(ov);
    
    invalidateShape();
    
    return true;
  }
  
  public boolean remove(int index, int npts)
  {
    if (!validIndex(index)) {
      return false;
    }
    if (!validIndex(index + npts - 1)) {
      return false;
    }
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    double[] xs = new double[this._npts - npts];
    double[] ys = new double[this._npts - npts];
    if (index > 0)
    {
      System.arraycopy(this._xpts, 0, xs, 0, index);
      System.arraycopy(this._ypts, 0, ys, 0, index);
    }
    if (this._npts - npts > index)
    {
      System.arraycopy(this._xpts, index + npts, xs, index, this._npts - npts - index);
      System.arraycopy(this._ypts, index + npts, ys, index, this._npts - npts - index);
    }
    this._npts -= npts;
    this._xpts = xs;
    this._ypts = ys;
    
    this._recalculate = true;
    
    _setVisible(ov);
    
    invalidateShape();
    return true;
  }
  
  public boolean setPoint(int index, double x, double y)
  {
    if (!validIndex(index)) {
      return false;
    }
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._xpts[index] = x;
    this._ypts[index] = y;
    
    this._recalculate = true;
    
    _setVisible(ov);
    
    invalidateShape();
    return true;
  }
  
  public double getXAt(int index)
  {
    if (!validIndex(index)) {
      return 0.0D;
    }
    return this._xpts[index];
  }
  
  public double getYAt(int index)
  {
    if (!validIndex(index)) {
      return 0.0D;
    }
    return this._ypts[index];
  }
  
  private boolean validIndex(int index)
  {
    if ((this._xpts == null) || (this._ypts == null)) {
      return false;
    }
    if ((index >= this._xpts.length) || (index >= this._ypts.length) || (index < 0)) {
      return false;
    }
    return true;
  }
  
  protected Shape getRotatedShape(double angle, Point2D anchor)
  {
    if (angle == 0.0D)
    {
      findBBox(null);
      synchronized (_polyShape)
      {
        _polyShape.setCoordinates(this._bbox, this._npts, this._closed, this._xpts, this._ypts);
        return new GeneralPath(_polyShape);
      }
    }
    cgTransformation tr = new cgTransformation();
    tr.rotate(angle, anchor.getX(), anchor.getY());
    synchronized (_polyShape)
    {
      cgRect bbox = getBoundingBox(null);
      if (bbox == null)
      {
        findBBox(null);
        bbox = this._bbox;
      }
      _polyShape.setCoordinates(bbox, this._npts, this._closed, this._xpts, this._ypts);
      return tr.createTransformedShape(_polyShape);
    }
  }
  
  public void applyTransformation(cgTransformation tr)
  {
    if (tr.getType() == 0) {
      return;
    }
    invalidate();
    
    boolean v = isVisible();
    if (v) {
      _setVisible(false);
    }
    if ((tr.getType() & 0x18) == 0)
    {
      Point2D pt = new Point2D.Double();
      for (int i = 0; i < this._npts; i++)
      {
        pt.setLocation(this._xpts[i], this._ypts[i]);
        pt = tr.transform(pt, null);
        this._xpts[i] = pt.getX();
        this._ypts[i] = pt.getY();
      }
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
      cgTransformation rectToRect = new cgTransformation(oldrect, rect, false, false);
      
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
      if (!rectToRect.isIdentity())
      {
        pt = new Point2D.Double();
        for (int i = 0; i < this._npts; i++)
        {
          pt.setLocation(this._xpts[i], this._ypts[i]);
          pt = rectToRect.transform(pt, pt);
          this._xpts[i] = pt.getX();
          this._ypts[i] = pt.getY();
        }
      }
    }
    this._recalculate = true;
    findBBox(null);
    
    _setVisible(v);
    invalidate();
  }
  
  public int getAllowedTransformationType()
  {
    return -1;
  }
  
  protected cgRect getBoundingBoxWithoutRotation(cgTransformation tr)
  {
    findBBox(tr);
    return this._bbox;
  }
  
  protected void render(cgShapeRenderer fp, cgRect bbox, double angle, Point2D rotationCenter)
  {
    cgTransformation tr = new cgTransformation();
    tr.rotate(angle, rotationCenter.getX(), rotationCenter.getY());
    cgTransformation inverseTr;
    try
    {
      double[] m = new double[6];
      tr.createInverse().getMatrix(m);
      inverseTr = new cgTransformation(m);
    }
    catch (NoninvertibleTransformException e)
    {
      Logger.getLogger(cgPointSet.class.getName()).log(Level.INFO, "NoninvertibleTransformException", e);
      throw new ArithmeticException("NoninvertibleTransformException: " + e.getMessage());
    }
    float oldAngle = getRotationAngle();
    
    rotate(0.0F);
    applyTransformation(tr);
    
    render(fp, bbox);
    
    applyTransformation(inverseTr);
    rotate(oldAngle);
  }
  
  protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group)
  {
    if ((id == 64) && (num > 0)) {
      return new PointGroupVertexHandle(num - 1, this, group);
    }
    return super.getVisualHandle(id, num, group);
  }
}

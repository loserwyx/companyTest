package test;

import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.model.cgTransformable;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgShapeRepresentativeFactory;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public abstract class cgRectangularShape
  extends cgCommonShape
  implements cgTransformable
{
  public abstract void setRectangle(cgRect paramcgRect);
  
  public abstract cgRect getRectangle();
  
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
    Point2D pt0 = getRotationCenter(new cgTransformation());
    double scaleX = tr.getLengthToXRatioAt(pt0);
    double scaleY = tr.getLengthToYRatioAt(pt0);
    cgRect rect = new cgRect(getRectangle());
    

    Point2D pt = tr.transform(pt0, null);
    rect.x += pt.getX() - pt0.getX();
    rect.y += pt.getY() - pt0.getY();
    if (0 != (tr.getType() & 0x26))
    {
      double height = rect.height * scaleY;
      double width = rect.width * scaleX;
      

      rect.x += (rect.width - width) / 2.0D;
      rect.y += (rect.height - height) / 2.0D;
      rect.width = width;
      rect.height = height;
    }
    if ((tr.getType() & 0x18) != 0)
    {
      double cos = tr.getScaleX();
      double sin = tr.getShearX();
      double dangle = cos != 0.0D ? -Math.atan(sin / cos) : 1.570796326794897D;
      if (cos < 0.0D) {
        dangle += 3.141592653589793D;
      }
      float angle = getRotationAngle() + (float)dangle;
      rotate(angle);
    }
    setRectangle(rect);
    _setVisible(v);
    invalidate();
  }
  
  public int getAllowedTransformationType()
  {
    return 95;
  }
  
  private static cgAbstractShape.ClassTable _classTable = new cgAbstractShape.ClassTable(1).add(cgShapeRepresentativeFactory.class, new ShapeRepresentativeFactory(null));
  
  public Object queryInterface(Class<?> param)
  {
    Object o = _classTable.get(param);
    if (o != null) {
      return o;
    }
    return super.queryInterface(param);
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
  
  private class RectVisualHandle
    extends cgAbstractVisualHandle
  {
    int _id;
    
    RectVisualHandle(int id, cgVisualHandleGroup group)
    {
      this._id = id;
      setGroup(group);
    }
    
    public int getHandleBarEnum()
    {
      return this._id;
    }
    
    public int getPolyIndex()
    {
      return this._id;
    }
    
    public void setPoint(Point2D pt)
    {
      Point2D pt0 = getOppositePoint();
      if (cgRectangularShape.this.getRotationAngle() != 0.0F)
      {
        cgTransformation tr = new cgTransformation();
        Point2D pc = cgRectangularShape.this.getRotationCenter(tr);
        tr.rotate(-cgRectangularShape.this.getRotationAngle(), pc.getX(), pc.getY());
        pt = tr.transform(pt, null);
      }
      cgRect bbox = new cgRect(cgRectangularShape.this.getRectangle());
      double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();
      double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
      setHandlesShapesVisible(false);
      
      boolean v = cgRectangularShape.this.isVisible();
      if (v) {
        cgRectangularShape.this._setVisible(false);
      }
      if (((this._id & 0x2) != 0) && 
        (bbox.width + pt.getX() - x > 0.0D)) {
        bbox.width += pt.getX() - x;
      }
      if (((this._id & 0x8) != 0) && 
        (bbox.height + pt.getY() - y > 0.0D)) {
        bbox.height += pt.getY() - y;
      }
      if (((this._id & 0x1) != 0) && 
        (bbox.width + x - pt.getX() > 0.0D)) {
        bbox.width += x - pt.getX();
      }
      if (((this._id & 0x4) != 0) && 
        (bbox.height + y - pt.getY() > 0.0D)) {
        bbox.height += y - pt.getY();
      }
      cgRectangularShape.this.setRectangle(bbox);
      Point2D pn = getOppositePoint();
      bbox.x += pt0.getX() - pn.getX();
      bbox.y += pt0.getY() - pn.getY();
      cgRectangularShape.this.setRectangle(bbox);
      if (v) {
        cgRectangularShape.this._setVisible(true);
      }
      setHandlesShapesVisible(true);
    }
    
    private Point2D getOppositePoint()
    {
      cgRect bbox = cgRectangularShape.this.getRectangle();
      double x = (this._id & 0x2) != 0 ? bbox.getLeft() : (this._id & 0x1) != 0 ? bbox.getRight() : bbox.getCenterX();
      

      double y = (this._id & 0x4) != 0 ? bbox.getTop() : (this._id & 0x8) != 0 ? bbox.getBottom() : bbox.getCenterY();
      

      Point2D pt = new Point2D.Double(x, y);
      if (cgRectangularShape.this.getRotationAngle() != 0.0F)
      {
        cgTransformation tr = new cgTransformation();
        Point2D pc = cgRectangularShape.this.getRotationCenter(tr);
        tr.rotate(cgRectangularShape.this.getRotationAngle(), pc.getX(), pc.getY());
        return tr.transform(pt, null);
      }
      return pt;
    }
    
    private Point2D pt = null;
    
    public Point2D getPoint()
    {
      cgRect bbox = cgRectangularShape.this.getRectangle();
      double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();
      

      double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
      if (this.pt == null) {
        this.pt = new Point2D.Double(x, y);
      } else {
        this.pt.setLocation(x, y);
      }
      if ((cgRectangularShape.this.getRotationAngle() != 0.0F) && ((this._id & 0x40) != 0))
      {
        cgTransformation tr = new cgTransformation();
        Point2D pc = cgRectangularShape.this.getRotationCenter(tr);
        tr.rotate(cgRectangularShape.this.getRotationAngle(), pc.getX(), pc.getY());
        return tr.transform(this.pt, null);
      }
      return this.pt;
    }
  }
  
  protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group)
  {
    if ((id & 0x40) == 0) {
      return super.getVisualHandle(id, num, group);
    }
    RectVisualHandle h = new RectVisualHandle(id, group);
    return h;
  }
}

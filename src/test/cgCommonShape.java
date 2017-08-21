package com.interactive.jcarnac2d.model.shapes;

import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.edit.manipulator.palette.cgManipulatorAttributePalette;
import com.interactive.jcarnac2d.model.interfaces.cgRotatedShape;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.interfaces.cgShapeInvalidater;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgVisualHandleGroupFactory;
import com.interactive.jcarnac2d.model.scenegraph.cgNode;
import com.interactive.jcarnac2d.model.scenegraph.cgNodeProxy;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public abstract class cgCommonShape
  extends cgAbstractShape
  implements cgRotatedShape, cgShapeInvalidater, cgNodeProxy
{
  private float _rotationAngle = 0.0F;
  
  public cgNode getNode()
  {
    return null;
  }
  
  public void rotate(float angle)
  {
    while (angle < 0.0F) {
      angle = (float)(angle + 6.283185307179586D);
    }
    while (angle > 6.283185307179586D) {
      angle = (float)(angle - 6.283185307179586D);
    }
    this._rotationAngle = angle;
  }
  
  public float getRotationAngle()
  {
    return this._rotationAngle;
  }
  
  public void invalidateShape(double x1, double y1, double x2, double y2)
  {
    notifyShapeEventListeners(2, new cgRect(x1, y1, x2, y2));
  }
  
  public void invalidateShape(cgRect bbox)
  {
    notifyShapeEventListeners(2, bbox);
  }
  
  public void invalidateShape()
  {
    invalidateShape(this);
  }
  
  public void invalidateShape(cgShape shape)
  {
    notifyShapeEventListeners(2, shape);
  }
  
  protected cgRect getBoundingBoxWithoutRotation(cgTransformation tr)
  {
    if (getRotationAngle() == 0.0D) {
      return getBoundingBox(tr);
    }
    float angle = getRotationAngle();
    rotate(0.0F);
    
    cgRect ibbox = getBoundingBox(tr);
    
    rotate(angle);
    return ibbox;
  }
  
  private class ImplVisualHandle
    extends cgAbstractVisualHandle
  {
    int _id;
    
    ImplVisualHandle(int id, cgVisualHandleGroup group)
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
      return 0;
    }
    
    public void setPoint(Point2D pt) {}
    
    public Point2D getPoint()
    {
      cgTransformation tr = new cgTransformation();
      
      cgRect bbox = (cgCommonShape.this.getRotationAngle() != 0.0F) && ((this._id & 0x40) != 0) ? cgCommonShape.this.getBoundingBoxWithoutRotation(tr) : cgCommonShape.this.getBoundingBox(tr);
      if (bbox == null) {
        return new Point2D.Double();
      }
      double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();
      


      double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
      


      Point2D pt = new Point2D.Double(x, y);
      if ((cgCommonShape.this.getRotationAngle() != 0.0F) && ((this._id & 0x40) != 0))
      {
        Point2D pc = cgCommonShape.this.getRotationCenter(tr);
        tr.rotate(cgCommonShape.this.getRotationAngle(), pc.getX(), pc.getY());
        return tr.transform(pt, null);
      }
      return pt;
    }
  }
  
  protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group)
  {
    if ((id & 0x40) != 0)
    {
      ImplVisualHandle h = new ImplVisualHandle(id, group);
      return h;
    }
    return super.getVisualHandle(id, num, group);
  }
  
  private static cgAbstractShape.ClassTable _classTable = new cgAbstractShape.ClassTable(1).add(cgVisualHandleGroupFactory.class, new CommonVisualHandleGroupFactory(null));
  
  public Object queryInterface(Class<?> param)
  {
    Object o = _classTable.get(param);
    if (o != null) {
      return o;
    }
    return super.queryInterface(param);
  }
  
  public final void setRotationAngle(float rotationAngle)
  {
    rotate(rotationAngle);
  }
  
  protected void setRotationAngleField(float rotationAngle)
  {
    this._rotationAngle = rotationAngle;
  }
  
  private static class CommonVisualHandleGroupFactory
    extends cgAbstractShape.VisualHandleGroupFactory
  {
    public cgVisualHandleGroup getHandleGroup(cgShape shape, int param, cgManipulatorAttributePalette palette)
    {
      if ((shape instanceof cgAbstractShape)) {
        switch (param)
        {
        case 3: 
          return new cgVerticesShapeList(shape, 4.0D, 4.0D, 3.0D, 3.0D, true, true, true, palette);
        case 2: 
          return new cgVerticesShapeList(shape, 4.0D, 4.0D, 3.0D, 3.0D, false, true, true, palette);
        }
      }
      return super.getHandleGroup(shape, param, palette);
    }
  }
}

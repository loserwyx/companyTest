package com.interactive.jcarnac2d.model.shapes;

import com.interactive.jcarnac2d.edit.geometry.cgGeometryEditor;
import com.interactive.jcarnac2d.edit.geometry.cgRectShapeEditor;
import com.interactive.jcarnac2d.model.interfaces.cgAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.model.scenegraph.cg2DNodeFactory;
import com.interactive.jcarnac2d.model.scenegraph.cgNode;
import com.interactive.jcarnac2d.model.scenegraph.cgRectangleShapeNode;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.delegate.cgRenderingEngine;
import java.awt.Shape;

public class cgRectangle
  extends cgRectangularShape
{
  private cgRect _bbox;
  private cgRectangleShapeNode _shapeNode;
  
  public cgRectangle()
  {
    this._bbox = new cgRect(0.0D, 0.0D, 1.0D, 1.0D);
  }
  
  public cgRectangle(double x1, double y1, double x2, double y2)
  {
    this._bbox = new cgRect(x1, y1, x2, y2);
  }
  
  public cgRectangle(cgRect rect)
  {
    if (rect != null) {
      this._bbox = new cgRect(rect);
    } else {
      this._bbox = new cgRect(0.0D, 0.0D, 0.0D, 0.0D);
    }
  }
  
  public cgNode getNode()
  {
    if ((this._shapeNode == null) && (this._bbox != null))
    {
      cg2DNodeFactory factory = (cg2DNodeFactory)cgRenderingEngine.getInstance().getNodeFactory(cg2DNodeFactory.class);
      
      this._shapeNode = factory.createRectangleNode(this._bbox.x, this._bbox.y, this._bbox.width, this._bbox.height);
      
      this._shapeNode.setVisible(isVisible());
      this._shapeNode.setAttribute(getAttribute());
      this._shapeNode.rotate(getRotationAngle());
    }
    return this._shapeNode;
  }
  
  public void setAttribute(cgAttribute attr)
  {
    if (this._shapeNode != null) {
      this._shapeNode.setAttribute(getAttribute());
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
    cgRectangle myClone = null;
    myClone = (cgRectangle)super.clone();
    if (this._bbox != null) {
      myClone.setRectangle(new cgRect(this._bbox));
    }
    myClone._shapeNode = null;
    
    return myClone;
  }
  
  public void setRectangle(cgRect rect)
  {
    boolean ov = isVisible();
    if (ov)
    {
      _setVisible(false);
      invalidateShape();
    }
    this._bbox = rect;
    if (this._shapeNode != null) {
      this._shapeNode.setRectangle(rect.x, rect.y, rect.width, rect.height);
    }
    _setVisible(ov);
    invalidateShape();
  }
  
  public cgRect getRectangle()
  {
    return this._bbox != null ? this._bbox : new cgRect(0.0D, 0.0D, 0.0D, 0.0D);
  }
  
  public cgRect getBoundingBox(cgTransformation tr)
  {
    cgRect bbox = getRotatedBBox();
    return bbox;
  }
  
  private cgRect getRotatedBBox()
  {
    if ((getRotationAngle() == 0.0D) || (this._bbox == null)) {
      return this._bbox;
    }
    cgTransformation tr = new cgTransformation();
    tr.rotate(getRotationAngle(), this._bbox.getCenterX(), this._bbox.getCenterY());
    return tr.transformRect(this._bbox);
  }
  
  private Shape getRotatedShape()
  {
    if ((getRotationAngle() == 0.0D) || (this._bbox == null)) {
      return this._bbox;
    }
    cgTransformation tr = new cgTransformation();
    tr.rotate(getRotationAngle(), this._bbox.getCenterX(), this._bbox.getCenterY());
    return tr.createTransformedShape(this._bbox);
  }
  
  public void render(cgShapeRenderer fp, cgRect bbox)
  {
    if ((isVisible()) && (getAttribute() != null))
    {
      fp.setAttribute(getAttribute());
      Shape r = getRotatedShape();
      if (r != null)
      {
        fp.fill(r);
        fp.draw(r);
      }
    }
  }
  
  protected cgRect getBoundingBoxWithoutRotation(cgTransformation tr)
  {
    return this._bbox;
  }
  
  private static cgAbstractShape.ClassTable _classTable = new cgAbstractShape.ClassTable(1).add(cgGeometryEditor.class, new cgRectShapeEditor());
  
  public Object queryInterface(Class<?> param)
  {
    Object o = _classTable.get(param);
    if (o != null) {
      return o;
    }
    return super.queryInterface(param);
  }
}

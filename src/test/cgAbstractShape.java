package com.interactive.jcarnac2d.model.shapes;

import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.edit.manipulator.palette.cgManipulatorAttributePalette;
import com.interactive.jcarnac2d.event.cgEventCreator;
import com.interactive.jcarnac2d.event.cgShapeEvent;
import com.interactive.jcarnac2d.event.cgShapeEventListener;
import com.interactive.jcarnac2d.model.attributes.cgGraphicAttribute;
import com.interactive.jcarnac2d.model.cgArea;
import com.interactive.jcarnac2d.model.cgInvalidatableArea;
import com.interactive.jcarnac2d.model.cgTransformable;
import com.interactive.jcarnac2d.model.interfaces.cgAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgPointGroupShape;
import com.interactive.jcarnac2d.model.interfaces.cgSelectableShape;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgDynamicInterface;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgShapeProperties;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgVisualHandleGroupFactory;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.EventListenerList;

public abstract class cgAbstractShape
  implements cgShape, Cloneable, cgInvalidatableArea, cgSelectableShape, cgDynamicInterface
{
  private cgAttribute _attr;
  private Object _userHandle;
  protected static final byte VISIBLE = 1;
  protected static final byte SELECTABLE = 2;
  protected static final byte EDITABLE = 4;
  protected static final byte NOTIFIABLE = 8;
  protected static final int VISIBLE_SHAPE_EVENT = 0;
  protected static final int INVISIBLE_SHAPE_EVENT = 1;
  protected static final int INVALIDATE_SHAPE_EVENT = 2;
  private int _flags;
  private EventListenerList _listenerList;
  
  public cgAbstractShape()
  {
    this._attr = null;
    



    this._userHandle = null;
    












    this._flags = 15;
    

    this._listenerList = new EventListenerList();
  }
  
  public Object clone()
  {
    cgAbstractShape myClone = null;
    try
    {
      myClone = (cgAbstractShape)super.clone();
      cgAttribute oldAttr = getAttribute();
      if ((oldAttr != null) && 
        ((oldAttr instanceof cgGraphicAttribute))) {
        myClone.setAttribute(((cgGraphicAttribute)oldAttr).duplicate());
      }
      cgShapeEventListener[] listeners = getShapeEventListeners();
      this._listenerList = new EventListenerList();
      for (int i = 0; i < listeners.length; i++) {
        addShapeEventListener(listeners[i]);
      }
      myClone.removeAllShapeEventListeners();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new UnsupportedOperationException(ex);
    }
    return myClone;
  }
  
  public void addShapeEventListener(cgShapeEventListener ls)
  {
    this._listenerList.add(cgShapeEventListener.class, ls);
  }
  
  public void removeShapeEventListener(cgShapeEventListener listener)
  {
    if (listener == null) {
      throw new IllegalArgumentException("listener not allowed to be null");
    }
    this._listenerList.remove(cgShapeEventListener.class, listener);
  }
  
  public cgShapeEventListener[] getShapeEventListeners()
  {
    cgShapeEventListener[] listeners = (cgShapeEventListener[])this._listenerList.getListeners(cgShapeEventListener.class);
    return listeners;
  }
  
  /**
   * @deprecated
   */
  public cgShapeEventListener getShapeEventListener()
  {
    cgShapeEventListener[] listeners = getShapeEventListeners();
    if (listeners == null) {
      return null;
    }
    if (listeners.length > 1) {
      throw new RuntimeException("more than one listener");
    }
    if (listeners.length == 0) {
      return null;
    }
    return listeners[0];
  }
  
  public void removeAllShapeEventListeners()
  {
    cgShapeEventListener[] listeners = getShapeEventListeners();
    if (listeners == null) {
      return;
    }
    for (int i = 0; i < listeners.length; i++) {
      removeShapeEventListener(listeners[i]);
    }
  }
  
  public cgAttribute getAttribute()
  {
    return this._attr;
  }
  
  public void setAttribute(cgAttribute attr)
  {
    if (this._attr == attr) {
      return;
    }
    if ((this._attr != null) && (attr != null) && (this._attr.equals(attr))) {
      return;
    }
    if (isNotificationEnabled())
    {
      if (this._attr != null) {
        this._attr.attributeInvalidated(cgEventCreator.createAttributeDisconnectedEvent(this, this._attr));
      }
      if (attr != null) {
        attr.attributeInvalidated(cgEventCreator.createAttributeConnectedEvent(this, attr));
      }
    }
    this._attr = attr;
    invalidate();
  }
  
  public Object getUserHandle()
  {
    if ((this._userHandle instanceof Properties)) {
      return ((Properties)this._userHandle).get(Properties.class);
    }
    return this._userHandle;
  }
  
  public void setUserHandle(Object handle)
  {
    if ((this._userHandle instanceof Properties))
    {
      if (handle == null) {
        ((Properties)this._userHandle).remove(Properties.class);
      } else {
        ((Properties)this._userHandle).put(Properties.class, handle);
      }
    }
    else {
      this._userHandle = handle;
    }
  }
  
  public boolean isFixedSize()
  {
    return false;
  }
  
  public final boolean isVisible()
  {
    return (this._flags & 0x1) != 0;
  }
  
  protected final void _setVisible(boolean vis)
  {
    if (vis) {
      this._flags |= 0x1;
    } else {
      this._flags &= 0xFFFFFFFE;
    }
  }
  
  public final boolean isSelectable()
  {
    return (this._flags & 0x2) != 0;
  }
  
  public final void setSelectable(boolean sel)
          
  {
    if (sel) {
      this._flags |= 0x2;
    } else {
      this._flags &= 0xFFFFFFFD;
    }
  }
  
  public void setVisible(boolean vis)
  {
    _setVisible(vis);
    int eventType;
    int eventType;
    if (vis) {
      eventType = 0;
    } else {
      eventType = 1;
    }
    notifyShapeEventListeners(eventType, null);
  }
  
  protected final void notifyShapeEventListeners(int eventType, cgArea eventArea)
  {
    if (!isNotificationEnabled()) {
      return;
    }
    cgShapeEventListener[] listeners = getShapeEventListeners();
    cgShapeEvent event = null;
    for (int i = 0; i < listeners.length; i++) {
      if ((listeners[i] instanceof cgInvalidatableArea))
      {
        if (eventArea == null) {
          ((cgInvalidatableArea)listeners[i]).invalidate();
        } else {
          ((cgInvalidatableArea)listeners[i]).invalidate(eventArea);
        }
      }
      else
      {
        if (event == null) {
          event = createShapeEvent(eventType, eventArea);
        }
        listeners[i].shapeInvalidated(event);
      }
    }
  }
  
  private cgShapeEvent createShapeEvent(int eventType, cgArea eventArea)
  {
    switch (eventType)
    {
    case 1: 
      return cgEventCreator.createShapeInvisibleEvent(this, eventArea);
    case 0: 
      return cgEventCreator.createShapeVisibleEvent(this, eventArea);
    case 2: 
      return cgEventCreator.createShapeInvalidatedEvent(this, eventArea);
    }
    throw new RuntimeException("Unknown event type " + eventType);
  }
  
  public boolean isNotificationEnabled()
  {
    return (this._flags & 0x8) != 0;
  }
  
  public void setNotification(boolean note)
  {
    if (note) {
      this._flags |= 0x8;
    } else {
      this._flags &= 0xFFFFFFF7;
    }
  }
  
  public void invalidate()
  {
    invalidateListeners(this);
  }
  
  public void invalidate(cgArea area)
  {
    invalidateListeners(area);
  }
  
  protected final void invalidateListeners(cgArea area)
  {
    notifyShapeEventListeners(2, area);
  }
  
  public abstract cgRect getBoundingBox(cgTransformation paramcgTransformation);
  
  public abstract void render(cgShapeRenderer paramcgShapeRenderer, cgRect paramcgRect);
  
  private class BoxVisualHandle
    extends cgAbstractVisualHandle
  {
    int _id;
    
    BoxVisualHandle(int id, cgVisualHandleGroup g)
    {
      this._id = id;
      setGroup(g);
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
      
      cgRect bbox = getGroup().getBoundingBox(null);
      if (bbox == null) {
        return;
      }
      double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();
      double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
      setHandlesShapesVisible(false);
      if (((this._id & 0x2) != 0) && 
        (bbox.width + pt.getX() - x > 0.0D)) {
        bbox.width += pt.getX() - x;
      }
      if (((this._id & 0x8) != 0) && 
        (bbox.height + pt.getY() - y > 0.0D)) {
        bbox.height += pt.getY() - y;
      }
      if (((this._id & 0x1) != 0) && 
        (bbox.width + x - pt.getX() > 0.0D))
      {
        bbox.width += x - pt.getX();
        bbox.x -= x - pt.getX();
      }
      if (((this._id & 0x4) != 0) && 
        (bbox.height + y - pt.getY() > 0.0D))
      {
        bbox.height += y - pt.getY();
        bbox.y -= y - pt.getY();
      }
      cgRect orig = getGroup().getBoundingBox(null);
      if ((bbox.isEmpty()) || (orig == null) || (orig.isEmpty()))
      {
        setHandlesShapesVisible(true);
        return;
      }
      cgTransformation tr = new cgTransformation(orig, bbox, false, false);
      Iterator<cgShape> itr = getGroup().getOwners();
      while (itr.hasNext())
      {
        cgShape o = (cgShape)itr.next();
        if ((o instanceof cgTransformable)) {
          ((cgTransformable)o).applyTransformation(tr);
        }
      }
      Point2D pn = getOppositePoint();
      tr = new cgTransformation();
      tr.translate(pt0.getX() - pn.getX(), pt0.getY() - pn.getY());
      itr = getGroup().getOwners();
      while (itr.hasNext())
      {
        cgShape o = (cgShape)itr.next();
        if (!(o instanceof cgPointGroupShape)) {
          if ((o instanceof cgTransformable)) {
            ((cgTransformable)o).applyTransformation(tr);
          }
        }
      }
      setHandlesShapesVisible(true);
    }
    
    private Point2D getOppositePoint()
    {
      cgRect bbox = cgAbstractShape.this.getBoundingBox(new cgTransformation());
      if (bbox == null) {
        return new Point2D.Double();
      }
      double x = (this._id & 0x2) != 0 ? bbox.getLeft() : (this._id & 0x1) != 0 ? bbox.getRight() : bbox.getCenterX();
      double y = (this._id & 0x4) != 0 ? bbox.getTop() : (this._id & 0x8) != 0 ? bbox.getBottom() : bbox.getCenterY();
      Point2D pt = new Point2D.Double(x, y);
      return pt;
    }
    
    public Point2D getPoint()
    {
      cgRect bbox = cgAbstractShape.this.getBoundingBox(new cgTransformation());
      if (bbox == null) {
        return new Point2D.Double();
      }
      double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();
      double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
      Point2D pt = new Point2D.Double(x, y);
      return pt;
    }
  }
  
  protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group)
  {
    BoxVisualHandle h = new BoxVisualHandle(id, group);
    return h;
  }
  
  public Point2D getRotationCenter(cgTransformation tr)
  {
    cgRect r = getBoundingBox(tr);
    if (r == null) {
      return new Point2D.Double();
    }
    return new Point2D.Double(r.getCenterX(), r.getCenterY());
  }
  
  private static ClassTable _classTable = new ClassTable(2).add(cgVisualHandleGroupFactory.class, new VisualHandleGroupFactory()).add(cgShapeProperties.class, new ShapeProperties());
  
  protected static class ClassTable
    extends Hashtable<Class<?>, Object>
  {
    private static final long serialVersionUID = -542461237772095142L;
    
    public ClassTable() {}
    
    public ClassTable(int i)
    {
      super(1.0F);
    }
    
    public ClassTable add(Class<?> type, Object obj)
    {
      put(type, obj);
      return this;
    }
  }
  
  public Object queryInterface(Class<?> param)
  {
    return _classTable.get(param);
  }
  
  static class VisualHandleGroupFactory
    implements cgVisualHandleGroupFactory
  {
    public cgVisualHandleGroup getHandleGroup(cgShape shape, int param, cgManipulatorAttributePalette palette)
    {
      if ((param & 0x1) != 0) {
        return new cgVerticesShapeList(shape, 4.0D, 4.0D, palette);
      }
      return null;
    }
  }
  
  private static class Properties
    extends Hashtable<Class<?>, Object>
  {
    private static final long serialVersionUID = -3813973206768196934L;
    
    public Properties()
    {
      super();
    }
    
    public Properties(Object user, Class<?> key, Object value)
    {
      super();
      if (user != null) {
        put(Properties.class, user);
      }
      put(key, value);
    }
  }
  
  static class ShapeProperties
    implements cgShapeProperties<Class<?>, Object>
  {
    public int size(cgShape shape)
    {
      if ((shape instanceof cgAbstractShape))
      {
        cgAbstractShape a = (cgAbstractShape)shape;
        if (a._userHandle == null) {
          return 0;
        }
        if ((a._userHandle instanceof cgAbstractShape.Properties)) {
          return ((cgAbstractShape.Properties)a._userHandle).size();
        }
      }
      return 1;
    }
    
    public boolean isEmpty(cgShape shape)
    {
      return size(shape) == 0;
    }
    
    public Object get(cgShape shape, Class<?> key)
    {
      if ((shape instanceof cgAbstractShape))
      {
        cgAbstractShape a = (cgAbstractShape)shape;
        if (a._userHandle == null) {
          return null;
        }
        if ((a._userHandle instanceof cgAbstractShape.Properties)) {
          return ((cgAbstractShape.Properties)a._userHandle).get(key);
        }
      }
      return null;
    }
    
    public Object put(cgShape shape, Class<?> key, Object value)
    {
      if ((shape instanceof cgAbstractShape))
      {
        cgAbstractShape a = (cgAbstractShape)shape;
        if ((a._userHandle == null) || (!(a._userHandle instanceof cgAbstractShape.Properties)))
        {
          if (key == null) {
            return null;
          }
          a._userHandle = new cgAbstractShape.Properties(a._userHandle, key, value);
          return null;
        }
        cgAbstractShape.Properties p = (cgAbstractShape.Properties)a._userHandle;
        if (value == null)
        {
          Object o = p.remove(key);
          if (p.size() > 1) {
            return o;
          }
          if (p.size() == 0) {
            a._userHandle = null;
          } else if (p.containsKey(cgAbstractShape.Properties.class)) {
            a._userHandle = p.get(cgAbstractShape.Properties.class);
          }
          return o;
        }
        return p.put(key, value);
      }
      return null;
    }
    
    public void clear(cgShape shape)
    {
      if ((shape instanceof cgAbstractShape))
      {
        cgAbstractShape a = (cgAbstractShape)shape;
        a._userHandle = null;
      }
    }
    
    public Set<Class<?>> getPropertyKeys(cgShape shape)
    {
      if ((shape instanceof cgAbstractShape))
      {
        cgAbstractShape a = (cgAbstractShape)shape;
        if ((a._userHandle instanceof cgAbstractShape.Properties)) {
          return ((cgAbstractShape.Properties)a._userHandle).keySet();
        }
      }
      return null;
    }
    
    public Collection<Object> getPropertyValues(cgShape shape)
    {
      if ((shape instanceof cgAbstractShape))
      {
        cgAbstractShape a = (cgAbstractShape)shape;
        if ((a._userHandle instanceof cgAbstractShape.Properties)) {
          return ((cgAbstractShape.Properties)a._userHandle).values();
        }
      }
      return null;
    }
  }
  
  protected void setAttributeNoNotification(cgAttribute attr)
  {
    this._attr = attr;
  }
}

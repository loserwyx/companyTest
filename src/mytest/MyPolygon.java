/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytest;

import com.interactive.jcarnac2d.edit.geometry.cgGeometryEditor;
import com.interactive.jcarnac2d.edit.geometry.cgPolyPointShapeEditor;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.model.interfaces.cgAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.model.interfaces.dynamic.cgShapeRepresentativeFactory;
import com.interactive.jcarnac2d.model.scenegraph.cg2DNodeFactory;
import com.interactive.jcarnac2d.model.scenegraph.cgNode;
import com.interactive.jcarnac2d.model.scenegraph.cgPointSetShapeNode;
import com.interactive.jcarnac2d.model.shapes.cgAbstractShape;
import com.interactive.jcarnac2d.model.shapes.cgPointSet;
import com.interactive.jcarnac2d.model.shapes.cgRectangle;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.delegate.cgRenderingEngine;
import java.awt.Shape;
import java.awt.geom.Point2D;
import mytest.PointGroupVertexHandle;

/**
 *
 * @author wangyanxin
 */
public class MyPolygon extends cgPointSet {

    private cgPointSetShapeNode _shapeNode;

    public MyPolygon() {
        setClosed(true);
    }

    public MyPolygon(int npts, double[] xpts, double[] ypts) {
        setCoordinates(npts, xpts, ypts);

        setClosed(true);
    }

    public MyPolygon(double[] xpts, double[] ypts, int npts) {
        double[] _xpts = new double[npts];
        double[] _ypts = new double[npts];

        int i = npts - 1;
        int j = 0;
        for (; j < npts; i--) {
            _xpts[j] = xpts[i];
            _ypts[j] = ypts[i];
            j++;
        }
        setCoordinates(npts, _xpts, _ypts);

        setClosed(true);
    }

    public cgNode getNode() {
        if (this._shapeNode == null) {
            cg2DNodeFactory factory = (cg2DNodeFactory) cgRenderingEngine.getInstance().getNodeFactory(cg2DNodeFactory.class);

            this._shapeNode = factory.createPolygonShapeNode(getXCoordinates(), getYCoordinates(), getSize(), true);

            this._shapeNode.setVisible(isVisible());
            this._shapeNode.setAttribute(getAttribute());
        }
        return this._shapeNode;
    }

    public Object clone() {
        MyPolygon myClone = (MyPolygon) super.clone();
        myClone._shapeNode = null;
        return myClone;
    }

    public void render(cgShapeRenderer fp, cgRect bbox) {
        if ((!isVisible()) || (getAttribute() == null)) {
            return;
        }
        fp.setAttribute(getAttribute());
        Shape rs = getRotatedShape();
        if (rs != null) {
            fp.fill(rs);

            fp.draw(rs);
        }
    }

    protected void render(cgShapeRenderer fp, cgRect bbox, double angle, Point2D anchor) {
        if ((!isVisible()) || (getAttribute() == null)) {
            return;
        }
        fp.setAttribute(getAttribute());
        Shape rs = getRotatedShape(angle, anchor);
        if (rs != null) {
            fp.fill(rs);

            fp.draw(rs);
        }
    }

    private static cgAbstractShape.ClassTable _classTable = new cgAbstractShape.ClassTable(2).add(cgShapeRepresentativeFactory.class, new ShapeRepresentativeFactory(null)).add(cgGeometryEditor.class, new cgPolyPointShapeEditor());

    public Object queryInterface(Class<?> param) {
        Object o = _classTable.get(param);
        if (o != null) {
            return o;
        }
        return super.queryInterface(param);
    }

    public void applyTransformation(cgTransformation tr) {
        boolean isNotificationEnabled = isNotificationEnabled();
        setNotification(false);
        super.applyTransformation(tr);
        setNotification(isNotificationEnabled);
        if (this._shapeNode != null) {
            this._shapeNode.setCoordinates(0, getXCoordinates(), getYCoordinates(), getSize());
        }
        invalidate();
    }

    public void setCoordinates(int npts, double[] xpts, double[] ypts) {
        if (this._shapeNode != null) {
            int size = getSize();
            if (size < npts) {
                double[] coord = new double[npts - size];
                this._shapeNode.insertCoordinates(size, coord, coord, npts - size);
            } else if (size > npts) {
                this._shapeNode.removeCoordinates(0, size - npts);
            }
            this._shapeNode.setCoordinates(0, xpts, ypts, npts);
        }
        super.setCoordinates(npts, xpts, ypts);
    }

    public boolean setPoint(int index, double x, double y) {
        if (this._shapeNode != null) {
            this._shapeNode.setCoordinates(index, new double[]{x}, new double[]{y}, 1);
        }
        return super.setPoint(index, x, y);
    }

    public boolean insert(int index, double x, double y) {
        if (this._shapeNode != null) {
            this._shapeNode.insertCoordinates(index, new double[]{x}, new double[]{y}, 1);
        }
        return super.insert(index, x, y);
    }

    public boolean insert(int index, int npts, double[] x, double[] y) {
        if (this._shapeNode != null) {
            this._shapeNode.insertCoordinates(index, x, y, npts);
        }
        return super.insert(index, npts, x, y);
    }

    public boolean remove(int index, int npts) {
        if (this._shapeNode != null) {
            this._shapeNode.removeCoordinates(index, npts);
        }
        return super.remove(index, npts);
    }

    public void rotate(float angle) {
        if (this._shapeNode != null) {
            this._shapeNode.rotate(angle);
        }
        super.rotate(angle);
    }

    public void setAttribute(cgAttribute attr) {
        if (this._shapeNode != null) {
            this._shapeNode.setAttribute(attr);
        }
        super.setAttribute(attr);
    }

    public void setVisible(boolean visible) {
        if (this._shapeNode != null) {
            this._shapeNode.setVisible(visible);
        }
        super.setVisible(visible);
    }

    private static class ShapeRepresentativeFactory
            implements cgShapeRepresentativeFactory {

        public cgShape getRepresentative(cgShape shape, cgRect rect) {
            cgRectangle r = new cgRectangle(rect);
            r.setAttribute(shape.getAttribute());
            return r;
        }

        public ShapeRepresentativeFactory(cgShape shape) {

        }
    }

    double[] getXCoordinates() {
        return this._xpts;
    }

    double[] getYCoordinates() {
        return this._ypts;
    }

//    protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group) {
//        System.out.println("调用了");
//        return super.getVisualHandle(id, num, group);
//    }
    // cgAbstractVisualHandle中的getVisualHandle方法就是最终的方法了
    protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group) {
        System.out.println("调用getVisualHandle   " + id + "  " + num);
//        if ((id == 64) && (num > 0)) {
//            System.out.println("调用了啊");
//            return new PointGroupVertexHandle((2 * (num - 1)) % 10, this, group);
//        }
        return super.getVisualHandle(id, num, group);
//        return new RectVisualHandle(id, group);
    }

    private class RectVisualHandle
            extends cgAbstractVisualHandle {

        int _id;

        RectVisualHandle(int id, cgVisualHandleGroup group) {
            this._id = id;
            setGroup(group);
        }

        public int getHandleBarEnum() {
            return this._id;
        }

        public int getPolyIndex() {
            return this._id;
        }

        public void setPoint(Point2D pt) {
            Point2D pt0 = getOppositePoint();
            if (MyPolygon.this.getRotationAngle() != 0.0F) {
                cgTransformation tr = new cgTransformation();
                Point2D pc = MyPolygon.this.getRotationCenter(tr);
                tr.rotate(-MyPolygon.this.getRotationAngle(), pc.getX(), pc.getY());
                pt = tr.transform(pt, null);
            }
            cgRect bbox = new cgRect(MyPolygon.this.getRectangle());
            double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();
            double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
            setHandlesShapesVisible(false);

            boolean v = MyPolygon.this.isVisible();
            if (v) {
                MyPolygon.this._setVisible(false);
            }
            if (((this._id & 0x2) != 0)
                    && (bbox.width + pt.getX() - x > 0.0D)) {
                bbox.width += pt.getX() - x;
            }
            if (((this._id & 0x8) != 0)
                    && (bbox.height + pt.getY() - y > 0.0D)) {
                bbox.height += pt.getY() - y;
            }
            if (((this._id & 0x1) != 0)
                    && (bbox.width + x - pt.getX() > 0.0D)) {
                bbox.width += x - pt.getX();
            }
            if (((this._id & 0x4) != 0)
                    && (bbox.height + y - pt.getY() > 0.0D)) {
                bbox.height += y - pt.getY();
            }
//            MyPolygon.this.setRectangle(bbox);
            Point2D pn = getOppositePoint();
            bbox.x += pt0.getX() - pn.getX();
            bbox.y += pt0.getY() - pn.getY();
//            MyPolygon.this.setRectangle(bbox);
            if (v) {
                MyPolygon.this._setVisible(true);
            }
            setHandlesShapesVisible(true);
        }

        private Point2D getOppositePoint() {
            cgRect bbox = MyPolygon.this.getRectangle();
            double x = (this._id & 0x2) != 0 ? bbox.getLeft() : (this._id & 0x1) != 0 ? bbox.getRight() : bbox.getCenterX();

            double y = (this._id & 0x4) != 0 ? bbox.getTop() : (this._id & 0x8) != 0 ? bbox.getBottom() : bbox.getCenterY();

            Point2D pt = new Point2D.Double(x, y);
            if (MyPolygon.this.getRotationAngle() != 0.0F) {
                cgTransformation tr = new cgTransformation();
                Point2D pc = MyPolygon.this.getRotationCenter(tr);
                tr.rotate(MyPolygon.this.getRotationAngle(), pc.getX(), pc.getY());
                return tr.transform(pt, null);
            }
            return pt;
        }

        private Point2D pt = null;

        public Point2D getPoint() {
            cgRect bbox = MyPolygon.this.getRectangle();
            double x = (this._id & 0x2) != 0 ? bbox.getRight() : (this._id & 0x1) != 0 ? bbox.getLeft() : bbox.getCenterX();

            double y = (this._id & 0x4) != 0 ? bbox.getBottom() : (this._id & 0x8) != 0 ? bbox.getTop() : bbox.getCenterY();
            if (this.pt == null) {
                this.pt = new Point2D.Double(x, y);
            } else {
                this.pt.setLocation(x, y);
            }
            if ((MyPolygon.this.getRotationAngle() != 0.0F) && ((this._id & 0x40) != 0)) {
                cgTransformation tr = new cgTransformation();
                Point2D pc = MyPolygon.this.getRotationCenter(tr);
                tr.rotate(MyPolygon.this.getRotationAngle(), pc.getX(), pc.getY());
                return tr.transform(this.pt, null);
            }
            return this.pt;
        }
    }

    public cgRect getRectangle() {
        return this._bbox != null ? this._bbox : new cgRect(0.0D, 0.0D, 0.0D, 0.0D);
    }
}

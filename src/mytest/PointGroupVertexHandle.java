package mytest;

import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.model.interfaces.cgPointGroupShape;
import com.interactive.jcarnac2d.model.interfaces.cgRotatedShape;
import com.interactive.jcarnac2d.util.cgTransformation;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

class PointGroupVertexHandle
        extends cgAbstractVisualHandle {

    private int _idx;
    private cgPointGroupShape _ps;

    PointGroupVertexHandle(int idx, cgPointGroupShape ps, cgVisualHandleGroup group) {
        this._idx = idx;
        this._ps = ps;
        setGroup(group);
    }

    public int getHandleBarEnum() {
        System.out.println("getHandleBarEnum");
        return 64;
    }

    public int getPolyIndex() {
        System.out.println("getPolyIndex");
        return this._idx + 1;
    }

    public void setPoint(Point2D pt) {
        System.out.println("setPoint");
        setHandlesShapesVisible(false);
        pt = unRotatePoint(pt);
        int index = this._idx != 0 ? 0 : 1;

        Point2D p = null;
        if (index < this._ps.getSize()) {
            p = rotatePoint(new Point2D.Double(this._ps.getXAt(index), this._ps.getYAt(index)));
        }
        this._ps.setPoint(this._idx, pt.getX(), pt.getY());
        if (index < this._ps.getSize()) {
            Point2D p1 = rotatePoint(new Point2D.Double(this._ps.getXAt(index), this._ps.getYAt(index)));

            double dx = p.getX() - p1.getX();
            double dy = p.getY() - p1.getY();
            for (int i = 0; i < this._ps.getSize(); i++) {
                this._ps.setPoint(i, this._ps.getXAt(i) + dx, this._ps.getYAt(i) + dy);
            }
        }
        setHandlesShapesVisible(true);
    }

    public Point2D getPoint() {
        System.out.println("getPoint");
        System.out.println("x的位置为" + this._ps.getXAt(this._idx));
        return rotatePoint(new Point2D.Double(this._ps.getXAt(this._idx), this._ps.getYAt(this._idx)));
    }

    private Point2D unRotatePoint(Point2D pt) {
        System.out.println("unRotatePoint");
        if ((this._ps instanceof cgRotatedShape)) {
            cgRotatedShape rotatedShape = (cgRotatedShape) this._ps;
            float angle = rotatedShape.getRotationAngle();
            if (angle != 0.0D) {
                cgTransformation tr = new cgTransformation();
                Point2D center = rotatedShape.getRotationCenter(tr);
                tr.rotate(-angle, center.getX(), center.getY());
                pt = tr.transform(pt, null);
            }
        }
        return pt;
    }

    private Point2D rotatePoint(Point2D pt) {
        System.out.println("rotatePoint");
        if ((this._ps instanceof cgRotatedShape)) {
            cgRotatedShape rotatedShape = (cgRotatedShape) this._ps;
            float angle = rotatedShape.getRotationAngle();
            if (angle != 0.0D) {
                cgTransformation tr = new cgTransformation();
                Point2D center = rotatedShape.getRotationCenter(tr);
                tr.rotate(angle, center.getX(), center.getY());
                pt = tr.transform(pt, null);
            }
        }
        return pt;
    }
}

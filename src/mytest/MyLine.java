/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytest;

import com.interactive.jcarnac2d.edit.manipulator.handles.cgAbstractVisualHandle;
import com.interactive.jcarnac2d.edit.manipulator.handles.cgVisualHandleGroup;
import com.interactive.jcarnac2d.model.cgTransformable;
import com.interactive.jcarnac2d.model.interfaces.cgShapeRenderer;
import com.interactive.jcarnac2d.model.scenegraph.cgPointSetShapeNode;
import com.interactive.jcarnac2d.model.shapes.cgCommonShape;
import com.interactive.jcarnac2d.model.shapes.cgLine;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author wangyanxin
 */
public class MyLine extends cgCommonShape
        implements cgTransformable {

    private Line2D.Double _line;

    private cgRect _bbox = new cgRect(0.0D, 0.0D, 0.0D, 0.0D);

    private cgPointSetShapeNode _shapeNode;

    public MyLine() {
        this._line = new Line2D.Double();
    }

    public MyLine(double x1, double y1, double x2, double y2) {
        this._line = new Line2D.Double(x1, y1, x2, y2);
    }

    @Override
    public cgRect getBoundingBox(cgTransformation paramcgTransformation) {
        return getRotatedBBox();
    }

    protected cgRect getRotatedBBox() {
        cgRect bbox = findBBox();
        if (getRotationAngle() == 0.0D) {
            return bbox;
        }
        cgTransformation tr = new cgTransformation();
        tr.rotate(getRotationAngle(), bbox.getCenterX(), bbox.getCenterY());
        return tr.transformRect(bbox);
    }

    private cgRect findBBox() {
        double x1 = this._line.getX1();
        double x2 = this._line.getX2();
        double y1 = this._line.getY1();
        double y2 = this._line.getY2();

        this._bbox.setFrameFromDiagonal(x1, y1, x2, y2);

        return this._bbox;
    }

    @Override
    public void render(cgShapeRenderer fp, cgRect paramcgRect) {
        if ((isVisible()) && (getAttribute() != null)) {
            fp.setAttribute(getAttribute());
            Shape rs = getRotatedShape();
            if (rs != null) {
                fp.draw(rs);
            }
        }
    }

    protected Shape getRotatedShape() {
        if (getRotationAngle() == 0.0D) {
            return new Line2D.Double(this._line.getX1(), this._line.getY1(), this._line.getX2(), this._line.getY2());
        }
        cgRect bbox = findBBox();
        cgTransformation tr = new cgTransformation();
        tr.rotate(getRotationAngle(), bbox.getCenterX(), bbox.getCenterY());
        return tr.createTransformedShape(this._line);
    }

    @Override
    public void applyTransformation(cgTransformation tr) {
        invalidate();
        boolean v = isVisible();
        if (v) {
            _setVisible(false);
        }
        if ((tr.getType() & 0x18) == 0) {
            this._line.setLine(tr.transform(this._line.getP1(), null), tr.transform(this._line.getP2(), null));
        } else {
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
            if ((rect.width <= 0.0D) || (rect.height <= 0.0D)) {
                if (rect.width <= 0.0001D) {
                    rect.x -= 1.0D;
                    rect.width = 2.0D;
                }
                if (rect.height <= 0.0001D) {
                    rect.y -= 1.0D;
                    rect.height = 2.0D;
                }
            }
            cgRect oldrect = new cgRect(rect);

            rect.x += pt.getX() - pt0.getX();
            rect.y += pt.getY() - pt0.getY();
            if (0 != (tr.getType() & 0x26)) {
                double height = rect.height * scaleX;
                double width = rect.width * scaleY;

                rect.x += (rect.width - width) / 2.0D;
                rect.y += (rect.height - height) / 2.0D;
                rect.width = width;
                rect.height = height;
            }
            double dangle = 0.0D;
            if ((tr.getType() & 0x18) != 0) {
                double cos = tr.getScaleX();
                double sin = tr.getShearX();
                dangle = cos != 0.0D ? -Math.atan(sin / cos) : 1.570796326794897D;
                if (cos < 0.0D) {
                    dangle += 3.141592653589793D;
                }
            }
            float angle = getRotationAngle() + (float) dangle;
            rotate(angle);

            cgTransformation rectToRect = new cgTransformation(oldrect, rect, false, false);
            if (!rectToRect.isIdentity()) {
                this._line.setLine(rectToRect.transform(this._line.getP1(), null), rectToRect.transform(this._line.getP2(), null));
            }
        }
        if (this._shapeNode != null) {
            this._shapeNode.setCoordinates(0, new double[]{this._line.x1, this._line.x2}, new double[]{this._line.y1, this._line.y2}, 2);
        }
        _setVisible(v);
        invalidate();
    }

    @Override
    public int getAllowedTransformationType() {
        return -1;
    }

    protected cgAbstractVisualHandle getVisualHandle(int id, int num, cgVisualHandleGroup group) {
//        if (id == 64) {
//            if (num == 1) {
//                return new VertexHandle1(group);
//            }
//            if (num == 2) {
//                return new VertexHandle2(group);
//            }
//        }
//        System.out.println("num=" + num + " , " + "id=" + id);
//        if (num == 2) {
            return new VertexHandle1(group);
//        }
//        return super.getVisualHandle(id, num, group);
    }

    private class VertexHandle1
            extends cgAbstractVisualHandle {

        public VertexHandle1(cgVisualHandleGroup g) {
            setGroup(g);
        }

        public int getHandleBarEnum() {
            return 64;
        }

        public int getPolyIndex() {
            return 1;
        }

        public void setPoint(Point2D pt) {
            setHandlesShapesVisible(false);

            pt = unRotatePoint(pt);

            MyLine.this.setP1(pt);

            setHandlesShapesVisible(true);
        }

        public Point2D getPoint() {
            return rotatePoint(MyLine.this._line.getP1());
        }

        private Point2D unRotatePoint(Point2D pt) {
            float angle = MyLine.this.getRotationAngle();
            if (angle != 0.0D) {
                cgTransformation tr = new cgTransformation();
                Point2D center = MyLine.this.getRotationCenter(tr);
                tr.rotate(-angle, center.getX(), center.getY());
                pt = tr.transform(pt, null);
            }
            return pt;
        }

        private Point2D rotatePoint(Point2D pt) {
            float angle = MyLine.this.getRotationAngle();
            if (angle != 0.0D) {
                cgTransformation tr = new cgTransformation();
                Point2D center = MyLine.this.getRotationCenter(tr);
                tr.rotate(angle, center.getX(), center.getY());
                pt = tr.transform(pt, null);
            }
            return pt;
        }
    }

    public void setP1(Point2D p1) {
        boolean ov = isVisible();
        if (ov) {
            _setVisible(false);
            invalidateShape();
        }
        this._line.setLine(p1, getP2());
        if (this._shapeNode != null) {
            this._shapeNode.setCoordinates(0, new double[]{p1.getX()}, new double[]{p1.getY()}, 1);
        }
        _setVisible(ov);
        invalidateShape();
    }

    public Point2D getP2() {
        return this._line.getP2();
    }

    private Point2D rotatePoint(Point2D pt) {
        float angle = MyLine.this.getRotationAngle();
        if (angle != 0.0D) {
            cgTransformation tr = new cgTransformation();
            Point2D center = MyLine.this.getRotationCenter(tr);
            tr.rotate(angle, center.getX(), center.getY());
            pt = tr.transform(pt, null);
        }
        return pt;
    }
}


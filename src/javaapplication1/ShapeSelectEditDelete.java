/*
 * @(#)ShapeSelectionEditionDeletion.java
 *
 * Copyright (c) 2000. INT, Inc.
 * 2901 Wilcrest Suite 100. Houston, Texas 77042, U.S.A.
 * All rights reserved
 */
package javaapplication1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.interactive.jcarnac2d.edit.manipulator.cgDefaultMouseManipulator;
import com.interactive.jcarnac2d.edit.manipulator.cgManipulatorFactory;
import com.interactive.jcarnac2d.edit.manipulator.cgMovableManipulator;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgSimpleListManipulator;
import com.interactive.jcarnac2d.edit.manipulator.palette.cgManipulatorAttributePalette;
import com.interactive.jcarnac2d.model.attributes.cgGraphicAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.layers.cgShapeListLayer;
import com.interactive.jcarnac2d.model.models.cgContainerModel;
import com.interactive.jcarnac2d.model.shapes.cgEllipse;
import com.interactive.jcarnac2d.model.shapes.cgLine;
import com.interactive.jcarnac2d.model.shapes.cgPolygon;
import com.interactive.jcarnac2d.model.shapes.cgRectangle;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.cgPlot;
import com.interactive.jcarnac2d.view.cgPlotView;
import com.interactive.jcarnac2d.view.cgStackedPlotView;
import com.interactive.util.gui.UILookAndFeel;
import com.interactive.jcarnac2d.model.shapes.cgLine;
import mytest.MyLine;
import mytest.MyPolygon;

/**
 * This tutorial shows how to use manipulators for selecting, editing and
 * deleting of objects interactively.
 */
final public class ShapeSelectEditDelete extends JFrame {

    private cgStackedPlotView _view;
    private cgPlotView _dataView;
    private cgContainerModel _dataContainer;
    private cgShapeListLayer _dataLayer;

    private final int WIDTH = 600;
    private final int HEIGHT = 400;

    /**
     * Constructs a new ShapeSelectEditDelete object.
     */
    public ShapeSelectEditDelete() {
    }

    /**
     * Starts this program
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        UILookAndFeel.setLAF();
        ShapeSelectEditDelete thisObj = new ShapeSelectEditDelete();
        thisObj.init();
        thisObj.addShapesToLayer();
        thisObj.setVisible(true);
    }

    /**
     * Initializes the object.
     */
    public void init() {
        _view = buildView();

        // construct one plot based on _view
        cgPlot plot = new cgPlot(_view);
        getContentPane().add(plot);
        setSize(new Dimension(WIDTH, HEIGHT));
        setTitle(" Shape Select Edit Delete:  Click on the shape for editing or click w/ Ctrl down on it for removal");

        // attach window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Constructs J/Carnac architecture objects and builds the view system. The
     * plot views are attached to a cgStackedPlotView object with attached
     * manipulator for shape selection, edit and deletion.
     *
     * @return cgStackedPlotView
     */
    private cgStackedPlotView buildView() {
        // Create the data layer/model
        _dataLayer = new cgShapeListLayer();
        _dataContainer = new cgContainerModel();
        _dataContainer.setBoundingBox(new cgRect(0, 0, WIDTH, HEIGHT));
        _dataContainer.addLayer(_dataLayer);

        cgShapeListLayer selectLayer = new cgShapeListLayer();
        _dataContainer.addLayer(selectLayer);

        // Creates the view attached to the model (_dataContainer)
        _dataView = new cgPlotView(_dataContainer);

        // Creates a stacked plot view object.
        cgStackedPlotView pv = new cgStackedPlotView();
        pv.setTransformation(new cgTransformation()); // provide transformation to avoid NPE in scenegraph
        pv.setCacheMode(cgStackedPlotView.CG_VIEW_SIZE_CACHE);

        // Adds the cgPlotView
        pv.add(_dataView);

        // Registers manipulators with the view for selection, edit and deletion.
        cgManipulatorAttributePalette palette = new cgManipulatorAttributePalette();
        cgSimpleListManipulator list = new cgSimpleListManipulator(selectLayer);
        cgMovableManipulator manipulator = cgManipulatorFactory.
                createSelectAndScaleManipulator(3.0, 3.0, palette, _dataLayer,
                        _dataContainer,
                        pv, selectLayer, list);
        cgDefaultMouseManipulator mouse = new MyMouseManipulator(pv, list, selectLayer);
        mouse.setMovableManipulator(manipulator);
        pv.addMouseListener(mouse);
        pv.addMouseMotionListener(mouse);

        return pv;
    }

    /**
     * This sub class implements additional shape deletion when the shape is
     * picked with Ctrl key pressed down
     */
    class MyMouseManipulator extends cgDefaultMouseManipulator {

        private cgSimpleListManipulator _list;
        private cgShapeListLayer _selectLayer;

        public MyMouseManipulator(cgStackedPlotView views,
                cgSimpleListManipulator list,
                cgShapeListLayer selectLayer) {
            super(views);
            _list = list;
            _selectLayer = selectLayer;
        }

        public void mousePressed(MouseEvent me) {
            super.mousePressed(me);

            if (me.isControlDown()) {
                for (int i = 0; i < _list.getListShapes().size(); i++) {
                    if (_dataLayer.containsShape((cgShape) _list.getListShapes().get(i))) {
                        _dataLayer.removeShape((cgShape) _list.getListShapes().get(i));
                    }
                }
                _selectLayer.removeAllShapes();
            }
        }
    }

    /**
     * Creates the shapes and adds them to the data layer.
     */
    public void addShapesToLayer() {

        // Creates an ellipse shape
        cgEllipse ball = new cgEllipse(400, 270, 50, 50);

        // Creates a graphic attribute object and attach it to the ellipse object
        // We want it to have a green solid fill
        cgGraphicAttribute attr = new cgGraphicAttribute();
        attr.setFillColor(Color.green);
        ball.setAttribute(attr);

        // Creates a rectangle shape
        cgRectangle rectangle = new cgRectangle(100, 100, 250, 200);
        attr = new cgGraphicAttribute();
        attr.setFillColor(Color.yellow);
        rectangle.setAttribute(attr);

        // Creates a triangle shape
        double m[] = new double[]{100, 200, 200, 100};
        double n[] = new double[]{100, 100, 200 , 200};
        cgPolygon triangle2 = new cgPolygon(4, m, n);
        triangle2.setAttribute(attr);
        
        
        
        int R = 50;
        double x[] = new double[10];    
        double y[] = new double[10];
        for (int i = 0; i < 5; i++) {
            x[2 * i] = R * Math.cos(72 * i * Math.PI / 180);
            y[2 * i] = R * Math.sin(72 * i * Math.PI / 180);
        }
        double r = R * Math.sin(18* Math.PI / 180) / Math.sin(126* Math.PI / 180);
        for (int i = 0; i < 5; i++) {
            x[2 * i + 1] = r * Math.cos((72 * i + 36) * Math.PI / 180);
            y[2 * i + 1] = r * Math.sin((72 * i + 36) * Math.PI / 180);
        }
        cgGraphicAttribute attr2 = new cgGraphicAttribute();
        attr2.setFillColor(Color.white);
        MyPolygon triangle = new MyPolygon(10, x, y);
//        attr.setFillColor(Color.blue);
        triangle.setAttribute(attr2);

        double a[] = new double[]{150, 220, 270};
        double b[] = new double[]{320, 270, 320};
        MyPolygon triangle1 = new MyPolygon(3, a, b);
        attr = new cgGraphicAttribute();
        attr.setFillColor(Color.blue);
        triangle1.setAttribute(attr);

        MyLine myLine = new MyLine(50, 50, 150, 150);
        myLine.setAttribute(attr);
        // 设置旋转的度数不同，线条的位置也不一样
//        myLine.setRotationAngle(2);

//        cgLine line = new cgLine(50, 50, 150, 150);
//        line.setAttribute(attr);
//           
//        _dataLayer.addShape(line);
        _dataLayer.addShape(myLine);
         _dataLayer.addShape(triangle2);
        _dataLayer.addShape(ball);
        _dataLayer.addShape(rectangle);
        _dataLayer.addShape(triangle);
        _dataLayer.addShape(triangle1);
    }
}

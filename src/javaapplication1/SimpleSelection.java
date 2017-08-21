/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.interactive.jcarnac2d.model.attributes.cgGraphicAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgLayer;
import com.interactive.jcarnac2d.model.interfaces.cgShape;
import com.interactive.jcarnac2d.model.layers.cgShapeListLayer;
import com.interactive.jcarnac2d.model.models.cgContainerModel;
import com.interactive.jcarnac2d.model.shapes.cgRectangle;
import com.interactive.jcarnac2d.selection.cgDeviceSelection;
import com.interactive.jcarnac2d.selection.cgSelectorCallback;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.cgGenericPlotLayout;
import com.interactive.jcarnac2d.view.cgPlot;
import com.interactive.jcarnac2d.view.cgPlotView;
import com.interactive.util.gui.UILookAndFeel;


// ==========================================================
// This sample shows how to implement selection function.
// ==========================================================
public class SimpleSelection extends JFrame{

    private cgPlotView _view;

    private cgRect _bbox;
    private cgTransformation _tr;
    private cgContainerModel _containerScene;

    final int _xvs = 600;
    final int _yvs = 400;

    // ==================
    // Constructor
    // ==================
    private SimpleSelection() {
    }

    // ==================
    // Starting point
    // ==================
    public static void main(String[] args) {
        UILookAndFeel.setLAF();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    SimpleSelection test = new SimpleSelection();
                    test.init();
                    test.setVisible(true);
                    test.repaint();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =================
    // Initialization
    // =================
    public void init() {

        _view = viewBuild();
        guiBuild();
        setSize(new Dimension(_xvs, _yvs));
        setTitle("J/CarnacPro - Simple Selection");
    }

    // =================
    // Create view
    // =================
    public cgPlotView viewBuild() {

        // Create model
        _bbox = new cgRect(0,0,_xvs,_yvs);
        _containerScene = new cgContainerModel();
        _containerScene.setBoundingBox(_bbox);

        cgShapeListLayer layer = new cgShapeListLayer();
        _containerScene.addLayer(layer);

        //Create rectangles
        cgGraphicAttribute attr = new cgGraphicAttribute(_containerScene);
        attr.setLineColor(Color.red);
        attr.setFillColor(Color.black);
        for (int i = 0; i < 100; i++) {
            double x = Math.random() * _xvs;
            double y = Math.random() * _yvs;
            cgRectangle rect = new cgRectangle( x, y, x + 40, y + 40);
            rect.setAttribute(attr);
            layer.addShape(rect);
        }

        // set transformation for view
        _tr = new cgTransformation();
        cgPlotView view = new cgPlotView(_containerScene);
        view.setTransformation(_tr);
        return view;
    }

    // =========================
    // Create GUI
    // =========================
    public void guiBuild() {

        cgPlot plot = new cgPlot(_view);
        plot.addScrollbar(cgGenericPlotLayout.SOUTH);
        plot.addScrollbar(cgGenericPlotLayout.EAST);
        getContentPane().add(plot);

        // Attach mouse listener
        _view.addMouseListener(new MouseInputAdapter(){
                                   public void mouseClicked(MouseEvent me) {
                                       Point p = _view.getViewPosition();
                                       int mx = me.getX() + p.x;
                                       int my = me.getY() + p.y;
                                       
                                       System.out.println(p.x + " : " + p.y + "mx" + mx + "my" + my);

                                       //initiate picking here
                                       cgDeviceSelection.selectByPoint(_containerScene, _tr, mx, my, 1, 1,new Selector(),null);
                                   }
                               });

        // Add window listener
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }
                          });

    }

    // =================================================================
    // Invoked by cgDeviceSelection.selectByPoint()
    // Only the shape sitting on the top will be selected to change color
    // ==================================================================
    private class Selector implements cgSelectorCallback {

        cgShape _lastSelected;
        
        public boolean shapeSelected(cgLayer shape) {

            return true;
        }
        
        public boolean shapeSelected(Shape shape) {

            return true;
        }

        public boolean shapeSelected(cgShape shape) {

            // Could mean no shapes selected, or the end of
            // the selection.
            if (shape == null) {
                
                System.out.println("Shape is NULL");

                if (_lastSelected != null) {
                    System.out.println("Shape Selected");
                    shape = _lastSelected;

                    cgGraphicAttribute attr = new cgGraphicAttribute(_containerScene);
                    attr.setFillColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
                    attr.setLineColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
                    shape.setAttribute(attr);

                    _lastSelected = null;

                    // Stops selection
                    return false;
                }

                // No shapes selected, stop
                return false;
            }
            _lastSelected = shape;

            // Continue to get the next one
            // until the top shape is retrieved.
            return true;
        }

    }

}

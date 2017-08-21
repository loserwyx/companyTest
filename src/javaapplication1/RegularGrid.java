/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import com.interactive.jcarnac2d.axes.cgAxisShape;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.interactive.jcarnac2d.axes.cgGridShape;
import com.interactive.jcarnac2d.axes.renderers.cgAxisRenderer;
import com.interactive.jcarnac2d.axes.renderers.cgFixedSizeAxisRenderer;
import com.interactive.jcarnac2d.axes.renderers.cgGridRenderer;
import com.interactive.jcarnac2d.axes.renderers.cgRegularGridRenderer;
import com.interactive.jcarnac2d.model.layers.cgShapeListLayer;
import com.interactive.jcarnac2d.model.models.cgContainerModel;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.util.cgTransformation;
import com.interactive.jcarnac2d.view.cgGenericPlotLayout;
import com.interactive.jcarnac2d.view.cgPlot;
import com.interactive.jcarnac2d.view.cgPlotView;
import com.interactive.util.axes.NumericTickGenerator;
import com.interactive.util.axes.TickGenerator;
import com.interactive.util.gui.UILookAndFeel;
import java.text.NumberFormat;

// ========================================================
// Given tutorial demonstrates how to create a regular grid
// using cgRegularGridRenderer.
// ========================================================
public class RegularGrid
        extends JFrame {

    // ======================================================
    //   Constructor - does nothing
    // ======================================================
    public RegularGrid() {
    }

    // ======================================================
    //   Starting point of application
    // ======================================================
    public static void main(String[] args) {
        UILookAndFeel.setLAF();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    RegularGrid app = new RegularGrid();
                    app.init();
                    app.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    //   Initializes application
    // ======================================================
    private void init() {

        createGUI();

        setSize(new Dimension(600, 600));
        setTitle("Grid - Regular Grid Renderer");
    }

    // ======================================================
    //   Creates the application GUI
    // ======================================================
    private void createGUI() {

        cgRect modelSpace = new cgRect(0, 0, 11, 11);
        cgRect deviceSpace = new cgRect(0, 0, 600, 600);

        cgTransformation tr = new cgTransformation(modelSpace, deviceSpace, false, false);

        /**
         *
         */
        // The formatter of axis' labels will use default locale and pattern
        NumberFormat nf = NumberFormat.getInstance();

        // We will use "fixed size" axis renderer for all axes
        
        // 参数一：标签刻度长度大小  参数二：标签数字长度大小  参数三：数字和刻度之间的距离大小，长度为像素
        cgAxisRenderer ar = new cgFixedSizeAxisRenderer(5, 15, 50, nf);

        // ----------------------------------------------------
        // First axis - values match the model coordinates,
        // model origin is at 0.0
        // ----------------------------------------------------
        
        // 第三个参数为显示的数字数
        cgRect bbox1 = new cgRect(0, 0, 11, 1);
        TickGenerator tg1 = new NumericTickGenerator(1.0);
        cgAxisShape axis1 = new cgAxisShape(cgAxisShape.NORTH, bbox1, tg1, ar);

        cgShapeListLayer layer = new cgShapeListLayer();

        layer.addShape(axis1);

        /**
         *
         */
        // ----------------------------------------------------
        //   Create grid using regular grid renderer
        // ----------------------------------------------------
        cgRect bbox = new cgRect(0, 0, 10, 10);
        TickGenerator htg = new NumericTickGenerator(1.0);
        TickGenerator vtg = new NumericTickGenerator(1.0);
        cgGridRenderer gr = new cgRegularGridRenderer();
        cgGridShape grid = new cgGridShape(bbox, htg, vtg, gr);

        // ----------------------------------------------------
        //   Create view
        // ----------------------------------------------------
//        cgShapeListLayer layer = new cgShapeListLayer();
//        layer.addShape( grid );
        cgContainerModel model = new cgContainerModel();
        model.setBoundingBox(modelSpace);
        model.addLayer(layer);

        cgPlotView view = new cgPlotView(model, tr);

        // ----------------------------------------------------
        //   Create plot
        // ----------------------------------------------------
        cgPlot plot = new cgPlot(view);

        plot.addScrollbar(cgGenericPlotLayout.SOUTH);
        plot.addScrollbar(cgGenericPlotLayout.EAST);

        getContentPane().add(plot);

        // ----------------------------------------------------
        //   Set up listeners
        // ----------------------------------------------------
        // Set window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

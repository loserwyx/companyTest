/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.interactive.jcarnac2d.axes.cgAxisShape;
import com.interactive.jcarnac2d.axes.renderers.cgAxisRenderer;
import com.interactive.jcarnac2d.axes.renderers.cgFixedSizeAxisRenderer;
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

// ========================================================
// Given tutorial demonstrates different usages of cgNumericTickGenerator:
//   - when the label values match or don't match the model coordinates
//   - when the model (value) origin match or don't match the origin of
//     axis' bounding box
//   - when the value step is negative
// It also demonstrates how to use cgFixedSizeAxisRenderer
// ========================================================
public class NumericGenerator
            extends JFrame {

    // ======================================================
    //   Constructor - does nothing
    // ======================================================
    public NumericGenerator() {}

    // ======================================================
    //   Starting point of application
    // ======================================================
    public static void main(String[] args) {
        UILookAndFeel.setLAF();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    NumericGenerator app = new NumericGenerator();
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

        setSize( new Dimension( 600, 600 ));
        setTitle("Axes - Numeric Tick Generator");
    }

    // ======================================================
    //   Creates the application GUI
    // ======================================================
    private void createGUI() {

        cgRect modelSpace  = new cgRect( -1, -1, 11, 11 );
        cgRect deviceSpace = new cgRect( 0, 0, 600, 600 );

        cgTransformation tr = new cgTransformation( modelSpace, deviceSpace, false, false );

        // ----------------------------------------------------
        //   Create axes
        // ----------------------------------------------------

        // The formatter of axis' labels will use default locale and pattern
        NumberFormat   nf = NumberFormat.getInstance();

        // We will use "fixed size" axis renderer for all axes
        cgAxisRenderer ar = new cgFixedSizeAxisRenderer( 5, 15, 3, nf );

        // ----------------------------------------------------
        // First axis - values match the model coordinates,
        // model origin is at 0.0
        // ----------------------------------------------------
        cgRect        bbox1 = new cgRect( 0, 0, 10, 1 );
        TickGenerator tg1   = new NumericTickGenerator( 1.0 );
        cgAxisShape   axis1 = new cgAxisShape( cgAxisShape.NORTH, bbox1, tg1, ar );

        cgShapeListLayer layer = new cgShapeListLayer();

        layer.addShape( axis1 );

        cgContainerModel model = new cgContainerModel();
        model.setBoundingBox( modelSpace );
        model.addLayer( layer );

        cgPlotView view = new cgPlotView( model, tr );

        // ----------------------------------------------------
        //   Create plot
        // ----------------------------------------------------

        cgPlot plot = new cgPlot( view );

        plot.addScrollbar( cgGenericPlotLayout.SOUTH );
        plot.addScrollbar( cgGenericPlotLayout.EAST );

        getContentPane().add( plot );

        // ----------------------------------------------------
        //   Set up listeners
        // ----------------------------------------------------

        // Set window listener
        addWindowListener( new WindowAdapter() {
                               public void windowClosing( WindowEvent e ) {
                                   System.exit( 0 );
                               }
                           });
    }
}

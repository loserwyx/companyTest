/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytest;

import com.interactive.jcarnac2d.model.attributes.cgGraphicAttribute;
import com.interactive.jcarnac2d.model.models.cgCommonModel;
import com.interactive.jcarnac2d.model.models.cgSimpleModel;
import com.interactive.jcarnac2d.util.cgRect;
import com.interactive.jcarnac2d.view.cgPlotView;
import com.interactive.util.gui.UILookAndFeel;
import java.awt.Color;
import java.awt.HeadlessException;
import javaapplication1.NumericGenerator;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author wangyanxin
 */
public class TestMyLine extends JFrame{

    public TestMyLine() {
        MyLine myLine = new MyLine(100, 100, 200, 200);
        
        cgGraphicAttribute attr = new cgGraphicAttribute();
        attr.setLineColor(Color.red);
        
        myLine.setAttribute(attr);
        
        cgRect modelSpace = new cgRect(0, 0, 200, 300);
        
        cgSimpleModel model = new cgSimpleModel();
        model.addShape(myLine);
        model.setBoundingBox(modelSpace);
        
        cgPlotView view = new cgPlotView(model);
        
        getContentPane().add(view);
    }
    
    public static void main(String[] args) {
        UILookAndFeel.setLAF();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    TestMyLine app = new TestMyLine();
                    app.setSize(500, 400);
                    app.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

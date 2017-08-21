package com.interactive.jcarnac2d.edit.manipulator;

import com.interactive.jcarnac2d.edit.manipulator.adapters.cgMagnifier;
import com.interactive.jcarnac2d.edit.manipulator.adapters.cgShapeControlSelector;
import com.interactive.jcarnac2d.edit.manipulator.adapters.cgShapeCreator;
import com.interactive.jcarnac2d.edit.manipulator.adapters.cgShapeSelector;
import com.interactive.jcarnac2d.edit.manipulator.adapters.cgShapeVisualizer;
import com.interactive.jcarnac2d.edit.manipulator.adapters.cgVertexCreator;
import com.interactive.jcarnac2d.edit.manipulator.creation.cgArrowCreationManipulator;
import com.interactive.jcarnac2d.edit.manipulator.creation.cgEllipseCreationManipulator;
import com.interactive.jcarnac2d.edit.manipulator.creation.cgLineCreationManipulator;
import com.interactive.jcarnac2d.edit.manipulator.creation.cgOneClickCreationManipulator;
import com.interactive.jcarnac2d.edit.manipulator.creation.cgPointSetCreationManipulator;
import com.interactive.jcarnac2d.edit.manipulator.creation.cgRectangleCreationManipulator;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgEditAdapterFactory;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgEditorManipulator;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgHandleMoveManipulator;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgRotateManipulator;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgScaleManipulator;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgTransformationAdapter;
import com.interactive.jcarnac2d.edit.manipulator.geometry.cgTranslateManipulator;
import com.interactive.jcarnac2d.edit.manipulator.palette.cgManipulatorAttributePalette;
import com.interactive.jcarnac2d.model.interfaces.cgAttribute;
import com.interactive.jcarnac2d.model.interfaces.cgLayeredModel;
import com.interactive.jcarnac2d.model.interfaces.cgRenderableContainer;
import com.interactive.jcarnac2d.model.interfaces.cgShapeCollection;
import com.interactive.jcarnac2d.selection.strategy.cgIntersectShapeSelectionStrategy;
import com.interactive.jcarnac2d.view.cgScaleableComponent;
import com.interactive.jcarnac2d.view.cgView;

public class cgManipulatorFactory
{
  public static cgMovableManipulator createTranslateManipulator(double width, double height, cgManipulatorAttributePalette palette, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgEditAdapterFactory editor, boolean isGroup)
  {
    cgTranslateManipulator tranManipulator = new cgTranslateManipulator(editor.getTransformationAdapter());
    

    tranManipulator.setGroup(isGroup);
    cgRectangleCreationManipulator r;
    (r = new cgRectangleCreationManipulator(palette.getRubberBandAttribute())).setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeControlSelector(selectableContainer, view, select, true)).setAdapter(editor.getShapeAdapter());
    



    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, r, view);
    return new cgEditorManipulator(simple, tranManipulator);
  }
  
  public static cgMovableManipulator createScaleManipulator(double width, double height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgEditAdapterFactory editor)
  {
    return createScaleManipulator(width, height, attr, selectableContainer, tmp, view, select, editor.getShapeAdapter(), editor.getTransformationAdapter());
  }
  
  private static cgMovableManipulator createScaleManipulator(double width, double height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgShapeAdapter sadapter, cgTransformationAdapter tadapter)
  {
    cgTranslateManipulator tranManipulator = new cgTranslateManipulator(tadapter);
    

    cgScaleManipulator move = new cgScaleManipulator(tadapter);
    move.setView(view);
    move.setNextManipulator(tranManipulator);
    
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    cgShapeControlSelector cs = new cgShapeControlSelector(selectableContainer, view, select, false);
    cs.setSelectionStrategy(new cgIntersectShapeSelectionStrategy());
    
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(cs).setAdapter(new cgVertexCreator(attr)).setAdapter(sadapter);
    


    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, m, view);
    return new cgEditorManipulator(simple, move);
  }
  
  public static cgMovableManipulator createHandleMoveManipulator(double width, double height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgEditAdapterFactory editor)
  {
    cgHandleMoveManipulator tranManipulator = new cgHandleMoveManipulator(editor.getTransformationAdapter());
    

    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    cgShapeControlSelector cs = new cgShapeControlSelector(selectableContainer, view, select, false);
    cs.setSelectionStrategy(new cgIntersectShapeSelectionStrategy());
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(cs).setAdapter(editor.getShapeAdapter());
    

    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, m, view);
    return new cgEditorManipulator(simple, tranManipulator);
  }
  
  public static cgMovableManipulator createSelectAndScaleManipulator(double width, double height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgEditAdapterFactory editor)
  {
    cgMovableManipulator select2 = createRectangleSelectHandlesManipulator(attr, selectableContainer, tmp, editor.getShapeAdapter(), view);
    cgTranslateManipulator tranManipulator = new cgTranslateManipulator(editor.getTransformationAdapter());
    

    cgScaleManipulator move = new cgScaleManipulator(editor.getTransformationAdapter());
    move.setView(view);
    move.setNextManipulator(tranManipulator);
    
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    
    cgShapeControlSelector cs = new cgShapeControlSelector(selectableContainer, view, select, false);
    cs.setSelectionStrategy(new cgIntersectShapeSelectionStrategy());
    
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(cs).setAdapter(new cgVertexCreator(attr)).setAdapter(editor.getShapeAdapter());
    


    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, m, view);
    cgEditorManipulator e = new cgEditorManipulator(simple, move, select2);
    e.setContainerAdapter(editor.getShapeAdapter());
    return e;
  }
  
  public static cgMovableManipulator createSelectAndRotateManipulator(double width, double height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgEditAdapterFactory editor)
  {
    cgMovableManipulator select2 = createRectangleSelectHandlesManipulator(attr, selectableContainer, tmp, editor.getShapeAdapter(), view);
    cgTranslateManipulator tranManipulator = new cgRotateManipulator(editor.getTransformationAdapter());
    

    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    cgShapeControlSelector cs = new cgShapeControlSelector(selectableContainer, view, select, false);
    cs.setSelectionStrategy(new cgIntersectShapeSelectionStrategy());
    
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(cs).setAdapter(new cgVertexCreator(attr)).setAdapter(editor.getShapeAdapter());
    


    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, m, view);
    cgEditorManipulator e = new cgEditorManipulator(simple, tranManipulator, select2);
    e.setContainerAdapter(editor.getShapeAdapter());
    return e;
  }
  
  public static cgMovableManipulator createRotateManipulator(double width, double height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgView view, cgRenderableContainer select, cgEditAdapterFactory editor, boolean isGroup)
  {
    cgTranslateManipulator tranManipulator = new cgRotateManipulator(editor.getTransformationAdapter());
    
    tranManipulator.setGroup(isGroup);
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    cgShapeControlSelector cs = new cgShapeControlSelector(selectableContainer, view, select, false);
    cs.setSelectionStrategy(new cgIntersectShapeSelectionStrategy());
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(cs).setAdapter(new cgVertexCreator(attr)).setAdapter(editor.getShapeAdapter());
    


    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, m, view);
    return new cgEditorManipulator(simple, tranManipulator);
  }
  
  public static cgMovableManipulator createRectangleManipulator(cgAttribute attr, cgShapeCollection editable, cgLayeredModel tmp)
  {
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(editable));
    
    return m;
  }
  
  public static cgMovableManipulator createRectangleManipulator(cgManipulatorAttributePalette pal, cgAttribute attr, cgShapeCollection editable, cgShapeAdapter adapter, cgLayeredModel tmp)
  {
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(editable)).setAdapter(new cgVertexCreator(pal)).setAdapter(adapter);
    


    return m;
  }
  
  public static cgShapeAdapter createVisualCreator(cgShapeCollection layer, cgLayeredModel tmp)
  {
    cgShapeVisualizer v = new cgShapeVisualizer(tmp);
    v.setAdapter(new cgShapeCreator(layer));
    return v;
  }
  
  public static cgMovableManipulator createLineManipulator(cgAttribute attr, cgShapeCollection editable, cgLayeredModel tmp)
  {
    cgLineCreationManipulator m = new cgLineCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(editable));
    
    return m;
  }
  
  public static cgMovableManipulator createArrowManipulator(cgAttribute attr, cgShapeCollection editable, cgLayeredModel tmp)
  {
    cgArrowCreationManipulator m = new cgArrowCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(editable));
    
    return m;
  }
  
  public static cgMovableManipulator createEllipseManipulator(cgAttribute attr, cgShapeCollection layer, cgLayeredModel tmp)
  {
    cgEllipseCreationManipulator m = new cgEllipseCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(layer));
    
    return m;
  }
  
  public static cgMovableManipulator createPolygonManipulator(cgAttribute attr, cgShapeCollection layer, cgLayeredModel tmp)
  {
    cgPointSetCreationManipulator m = new cgPointSetCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(layer));
    
    m.setStyle(1);
    return m;
  }
  
  public static cgMovableManipulator createPolylineManipulator(cgAttribute attr, cgShapeCollection editable, cgLayeredModel tmp)
  {
    cgPointSetCreationManipulator m = new cgPointSetCreationManipulator(attr);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeCreator(editable));
    
    m.setStyle(0);
    return m;
  }
  
  public static cgMovableManipulator createZoomManipulator(cgManipulatorAttributePalette attr, cgLayeredModel model, cgView view)
  {
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    m.setAdapter(new cgShapeVisualizer(model)).setAdapter(new cgMagnifier(view));
    
    return m;
  }
  
  public static cgMovableManipulator createZoomManipulator(cgManipulatorAttributePalette attr, cgLayeredModel model, cgView view, cgScaleableComponent scale)
  {
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    m.setAdapter(new cgShapeVisualizer(model)).setAdapter(new cgMagnifier(view, scale));
    
    return m;
  }
  
  public static cgMovableManipulator createPanningManipulator(cgView view)
  {
    return new cgPanningManipulator(view);
  }
  
  public static cgMovableManipulator createRectangleSelectHandlesManipulator(cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgShapeAdapter acceptor, cgView view)
  {
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeSelector(selectableContainer, view, false)).setAdapter(new cgVertexCreator(attr)).setAdapter(acceptor);
    





    return m;
  }
  
  public static cgMovableManipulator createRectangleAndToggleSelectHandlesManipulator(int width, int height, cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgShapeContainerAdapter acceptor, cgView view)
  {
    cgMovableManipulator move = createRectangleSelectHandlesManipulator(attr, selectableContainer, tmp, acceptor, view);
    cgRectangleCreationManipulator m = new cgRectangleCreationManipulator(attr.getRubberBandAttribute());
    cgShapeSelector cs = new cgShapeSelector(selectableContainer, view, false);
    cs.setSelectionStrategy(new cgIntersectShapeSelectionStrategy());
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(cs).setAdapter(new cgVertexCreator(attr)).setAdapter(acceptor);
    


    cgOneClickCreationManipulator simple = new cgOneClickCreationManipulator(width, height, m, view);
    cgEditorManipulator e = new cgEditorManipulator(simple, null, move);
    e.setContainerAdapter(acceptor);
    return e;
  }
  
  public static cgMovableManipulator createEllipseSelectHandlesManipulator(cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgShapeAdapter acceptor, cgView view)
  {
    cgEllipseCreationManipulator m = new cgEllipseCreationManipulator(attr.getRubberBandAttribute());
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeSelector(selectableContainer, view, false)).setAdapter(new cgVertexCreator(attr)).setAdapter(acceptor);
    






    return m;
  }
  
  public static cgMovableManipulator createPolygonSelectHandlesManipulator(cgManipulatorAttributePalette attr, cgRenderableContainer selectableContainer, cgLayeredModel tmp, cgShapeAdapter acceptor, cgView view)
  {
    cgPointSetCreationManipulator m = new cgPointSetCreationManipulator(attr.getRubberBandAttribute());
    m.setStyle(1);
    m.setAdapter(new cgShapeVisualizer(tmp)).setAdapter(new cgShapeSelector(selectableContainer, view, false)).setAdapter(new cgVertexCreator(attr)).setAdapter(acceptor);
    




    return m;
  }
}

package de.neemann.digital.plugin;

import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.Observer;
import de.neemann.digital.core.Value;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.PinDescriptions;
import de.neemann.digital.draw.elements.IOState;
import de.neemann.digital.draw.elements.Pin;
import de.neemann.digital.draw.elements.Pins;
import de.neemann.digital.draw.graphics.Graphic;
import de.neemann.digital.draw.graphics.Orientation;
import de.neemann.digital.draw.graphics.Style;
import de.neemann.digital.draw.shapes.InteractorInterface;
import de.neemann.digital.draw.shapes.Shape;

import static de.neemann.digital.draw.graphics.Vector.vec;
import static de.neemann.digital.draw.shapes.GenericShape.SIZE;
import static de.neemann.digital.draw.shapes.GenericShape.SIZE2;

/**
 * A shape for the new Or component
 */
public class MyOrShape implements Shape {
    private final PinDescriptions inputs;
    private final PinDescriptions outputs;
    private final int ellipseSize;
    private ObservableValue output;
    private Value actualOutputValue;

    /**
     * Creates a new instance.
     *
     * @param elementAttributes the attributes of the component
     * @param inputs            the inputs
     * @param outputs           the outputs
     */
    public MyOrShape(ElementAttributes elementAttributes, PinDescriptions inputs, PinDescriptions outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        ellipseSize = elementAttributes.get(MyOr.ELLIPSE_SIZE);
    }

    /**
     * Defines a positions for each input and output.
     * CAUTION: The coordinates needs to be multiple of the grid size!
     *
     * @return the pins to draw
     */
    @Override
    public Pins getPins() {
        return new Pins()
                .add(new Pin(vec(0, 0), inputs.get(0)))
                .add(new Pin(vec(0, SIZE * 2), inputs.get(1)))
                .add(new Pin(vec(SIZE * 3, SIZE), outputs.get(0)));
    }

    /**
     * This method call connects the created model element to the shape which represents the model node.
     * If the look of the shape depends on an input or output state, the shape has to register the
     * guiObserver to all of the input or output ObservableValue instances it depends on.
     * To access the actual state while drawing, the Shape needs to store the IOState or the needed inputs
     * or outputs in a member variable.
     * This shape should reflect the output state, so we have to register the shape to the output value and
     * keep the {@link ObservableValue} representing the output for later use.
     * In this case null is returned because there is no user interaction with the shape.
     *
     * @param ioState     The state of the element. Is never null.
     * @param guiObserver Can be used to update the GUI by calling hasChanged, Is maybe null.
     *                    If the shape depends on a signal value, you can add this observer to
     *                    the signal. In this case a repaint is initiated, if the signal changes.
     * @return The interactor which is used to interact with the shape during the simulation.
     */
    @Override
    public InteractorInterface applyStateMonitor(IOState ioState, Observer guiObserver) {
        output = ioState.getOutput(0);
        output.addObserverToValue(guiObserver);
        return null;
    }

    /**
     * The draw method is not allowed to access the model, thus the draw method can not read the output state
     * of the OR gate. To do so, this method is used to read values from the model.
     * During execution of this method the model is locked. Thus this method should return as fast as possible.
     */
    @Override
    public void readObservableValues() {
        // The output is null if you are in edit mode and therefore no model is running,
        if (output != null)
            actualOutputValue = output.getCopy();
    }

    /**
     * Draw the component.
     * This method is not allowed to access the model!
     * Use the readObservableValues method to pick the necessary values from the model.
     *
     * @param graphic   interface to draw to
     * @param highLight Null means no highlighting at all. If highlight is not null, highlighting is active.
     *                  The given style should be used to highlight the drawing.
     */
    @Override
    public void drawTo(Graphic graphic, Style highLight) {

        // The actualOutputValue is null if you are in edit mode and therefore no model is running,
        if (actualOutputValue != null) {

            // get the color style representing the actual state of the output value
            final Style wireStyle = Style.getWireStyle(actualOutputValue);

            // draw a somewhat smaller ellipse inside the outer one using the wireStyle
            graphic.drawCircle(vec(SIZE2, SIZE2 - SIZE * (ellipseSize - 1)), vec(SIZE * 2 + SIZE2, SIZE2 + SIZE * ellipseSize), wireStyle);
        } else {
            // in edit mode draw a thin ellipse instead
            graphic.drawCircle(vec(SIZE2, SIZE2 - SIZE * (ellipseSize - 1)), vec(SIZE * 2 + SIZE2, SIZE2 + SIZE * ellipseSize), Style.THIN);
        }

        // draw the outer ellipse
        graphic.drawCircle(vec(0, SIZE - SIZE * ellipseSize), vec(SIZE * 3, SIZE + SIZE * ellipseSize), Style.NORMAL);
        // draw the text "Or"
        graphic.drawText(vec(SIZE * 3 / 2, SIZE), vec(1, SIZE), "Or", Orientation.CENTERCENTER, Style.NORMAL);
    }
}

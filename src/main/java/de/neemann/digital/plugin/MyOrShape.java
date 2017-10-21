package de.neemann.digital.plugin;

import de.neemann.digital.core.Observer;
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

/**
 * A shape for the new Or component
 */
public class MyOrShape implements Shape {
    private final PinDescriptions inputs;
    private final PinDescriptions outputs;

    /**
     * Creates a new instance
     *
     * @param elementAttributes the attributes of the component
     * @param inputs            the inputs
     * @param outputs           the outputs
     */
    public MyOrShape(ElementAttributes elementAttributes, PinDescriptions inputs, PinDescriptions outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /**
     * Attach the positions to the inputs and outputs.
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
     * You can return an interactor which allows to interact with the component if the simulation is running.
     * In this case null is returned because there is no interaction.
     *
     * @param ioState     the state of the element, never null
     * @param guiObserver can be used to update the GUI by calling hasChanged, maybe null
     * @return the interactor
     */
    @Override
    public InteractorInterface applyStateMonitor(IOState ioState, Observer guiObserver) {
        return null;
    }

    /**
     * Draw the component.
     *
     * @param graphic   interface to draw to
     * @param highLight true if a highlighted drawing is required
     */
    @Override
    public void drawTo(Graphic graphic, Style highLight) {
        graphic.drawCircle(vec(0, -SIZE), vec(SIZE * 3, SIZE * 3), Style.NORMAL);
        graphic.drawText(vec(SIZE * 3 / 2, SIZE), vec(1, SIZE), "Or", Orientation.CENTERCENTER, Style.NORMAL);
    }
}

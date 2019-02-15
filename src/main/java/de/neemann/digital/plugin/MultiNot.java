package de.neemann.digital.plugin;


import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * Example of an element that consists of three individual NOT gates which
 * are implemented as three nodes which are operating independent of each other.
 */
public class MultiNot implements Element {  // A Element is created if the model is generated

    public static final ElementTypeDescription DESCRIPTION =
            new ElementTypeDescription(MultiNot.class,
                    input("I_0"),
                    input("I_1"),
                    input("I_2"))
                    .addAttribute(Keys.BITS);

    private final int bits;
    private final ObservableValue out0;
    private final ObservableValue out1;
    private final ObservableValue out2;
    private ObservableValues inputs;

    public MultiNot(ElementAttributes attr) {
        bits = attr.getBits();
        out0 = new ObservableValue("O_0", bits);
        out1 = new ObservableValue("O_1", bits);
        out2 = new ObservableValue("O_2", bits);
    }

    /**
     * The element has to provide all the output values of the whole component.
     *
     * @return the outputs available
     */
    @Override
    public ObservableValues getOutputs() {
        return new ObservableValues(out0, out1, out2);
    }

    /**
     * And the element also receives all the required input values.
     *
     * @param inputs the inputs
     * @throws NodeException NodeException
     */
    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        this.inputs = inputs;
        for (ObservableValue i : inputs)
            i.checkBits(bits, null);
    }

    /**
     * If this inputs are set this method is called. This method is allowed to
     * add several nodes to the model.
     * These nodes are operating independent of each other.
     *
     * @param model the mode which is generated
     */
    @Override
    public void registerNodes(Model model) {
        model.add(new MyNotNode(inputs.get(0), out0));
        model.add(new MyNotNode(inputs.get(1), out1));
        model.add(new MyNotNode(inputs.get(2), out2));
    }

    /**
     * The implementation of the NOT gate
     */
    private static class MyNotNode extends Node {
        private final ObservableValue in;
        private final ObservableValue out;
        private long value;

        private MyNotNode(ObservableValue in, ObservableValue out) {
            // the node has to register itself to the input value.
            // Otherwise it is not able to react on an input value change.
            this.in = in.addObserverToValue(this);
            this.out = out;
        }

        @Override
        public void readInputs() {
            value = in.getValue();
        }

        @Override
        public void writeOutputs() {
            out.setValue(~value); // invert the input value
        }

        @Override
        public ObservableValues getOutputs() {
            return out.asList();
        }
    }
}

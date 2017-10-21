package de.neemann.digital.plugin;

import de.neemann.digital.core.Model;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.draw.elements.PinException;
import junit.framework.TestCase;

import static de.neemann.digital.core.ObservableValues.ovs;

/**
 * Simple table driven tests to test the custom MyAnd and MyOr components
 */
public class MyComponentsTest extends TestCase {

    private static class TestTableRow {
        int a;
        int b;
        int myAnd;
        int myOr;

        TestTableRow(int a, int b, int myAnd, int myOr) {
            this.a = a;
            this.b = b;
            this.myAnd = myAnd;
            this.myOr = myOr;
        }
    }

    private static TestTableRow[] table = new TestTableRow[]{
            new TestTableRow(0, 0, 0, 0),
            new TestTableRow(0, 1, 0, 1),
            new TestTableRow(1, 0, 0, 1),
            new TestTableRow(1, 1, 1, 1),
    };


    public void testMyAnd() throws NodeException, PinException {
        ObservableValue a = new ObservableValue("a", 1);
        ObservableValue b = new ObservableValue("b", 1);

        Model model = new Model();
        MyAnd myAnd = model.add(new MyAnd(new ElementAttributes()));
        myAnd.setInputs(ovs(a, b));
        ObservableValue out = myAnd.getOutputs().get(0);
        model.init();

        for (TestTableRow row : table) {
            a.setValue(row.a);
            b.setValue(row.b);
            model.doStep();
            assertEquals(row.myAnd, out.getValue());
        }
    }

    public void testMyOr() throws NodeException, PinException {
        ObservableValue a = new ObservableValue("a", 1);
        ObservableValue b = new ObservableValue("b", 1);

        Model model = new Model();
        MyOr myOr = model.add(new MyOr(new ElementAttributes()));
        myOr.setInputs(ovs(a, b));
        ObservableValue out = myOr.getOutputs().get(0);
        model.init();

        for (TestTableRow row : table) {
            a.setValue(row.a);
            b.setValue(row.b);
            model.doStep();
            assertEquals(row.myOr, out.getValue());
        }
    }

}
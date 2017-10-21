package de.neemann.digital.plugin;

import de.neemann.digital.draw.library.ComponentManager;
import de.neemann.digital.draw.library.ComponentSource;
import de.neemann.digital.draw.library.ElementLibrary;
import de.neemann.digital.draw.library.InvalidNodeException;
import de.neemann.digital.gui.Main;

/**
 * Adds some components to Digital
 */
public class DemoComponentSource implements ComponentSource {

    /**
     * Attach your components to the simulator by calling the add methods
     *
     * @param manager the ComponentManager
     * @throws InvalidNodeException InvalidNodeException
     */
    @Override
    public void registerComponents(ComponentManager manager) throws InvalidNodeException {

        // add a component and use the default shape
        manager.addComponent("my folder/my sub folder", MyAnd.DESCRIPTION);

        // add a component and also provide a custom shape
        manager.addComponent("my folder/my sub folder", MyOr.DESCRIPTION, MyOrShape::new);
    }

    /**
     * Start Digital with this ComponentSource attached to make debugging easier.
     * IMPORTANT: Remove the jar from Digitals settings!!!
     *
     * @param args args
     */
    public static void main(String[] args) {
        new Main.MainBuilder()
                .setLibrary(new ElementLibrary().registerComponentSource(new DemoComponentSource()))
                .openLater();
    }
}

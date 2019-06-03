/*
 * Copyright (c) 2017 Helmut Neemann
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.plugin;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.*;
import de.neemann.digital.core.memory.DataField;
import de.neemann.digital.core.memory.RAMInterface;

import java.util.ArrayList;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * RAM module with different ports to read and write the data
 * and an additional read port. Used to implement graphic card memory.
 * This example shohs, how to implement a component with a customizable
 * number of inputs.
 */
public class MultiPortRAM extends Node implements Element, RAMInterface {

    private static final Key<Integer> WRITE_PORTS =
            new Key.KeyInteger("writePorts", 2)
                    .setMin(1)
                    .setComboBoxValues(1, 2, 3, 4, 5)
                    .setName("Write Ports")
                    .setDescription("Number of write ports.");

    private static final Key<Integer> READ_PORTS =
            new Key.KeyInteger("readPorts", 2)
                    .setMin(1)
                    .setComboBoxValues(1, 2, 3, 4, 5)
                    .setName("Read Ports")
                    .setDescription("Number of read ports.");

    /**
     * The RAMs {@link ElementTypeDescription}
     */
    static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(MultiPortRAM.class) {
        @Override
        public PinDescriptions getInputDescription(ElementAttributes elementAttributes) {
            int writePorts = elementAttributes.get(WRITE_PORTS);
            int readPorts = elementAttributes.get(READ_PORTS);
            PinDescription[] names = new PinDescription[writePorts * 3 + readPorts + 1];
            for (int i = 0; i < writePorts; i++) {
                names[i * 3] = input("WE" + i, "Write enable pin " + i);
                names[i * 3 + 1] = input("WA" + i, "Write Address Bus " + i);
                names[i * 3 + 2] = input("WD" + i, "Write Data Bus " + i);
            }
            for (int i = 0; i < readPorts; i++)
                names[i + writePorts * 3] = input("RA" + i, "Read Address Bus " + i);

            names[names.length - 1] = input("C", "Clock Pin").setClock();
            return new PinDescriptions(names);
        }
    }
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS)
            .addAttribute(Keys.ADDR_BITS)
            .addAttribute(WRITE_PORTS)
            .addAttribute(READ_PORTS)
            .addAttribute(Keys.IS_PROGRAM_MEMORY)
            .addAttribute(Keys.LABEL);

    private final DataField memory;
    private final ObservableValue[] out;
    private final int addrBits;
    private final int bits;
    private final String label;
    private final int size;
    private final boolean isProgramMemory;
    private final int writePortNum;
    private final int readPortNum;
    private boolean lastClk = false;
    private ArrayList<WritePort> writePorts;
    private ArrayList<ReadPort> readPorts;
    private ObservableValue clkIn;

    /**
     * Creates a new instance
     *
     * @param attr the elements attributes
     */
    public MultiPortRAM(ElementAttributes attr) {
        super(true);
        bits = attr.get(Keys.BITS);

        writePortNum = attr.get(WRITE_PORTS);
        readPortNum = attr.get(READ_PORTS);

        out = new ObservableValue[readPortNum];
        for (int i = 0; i < readPortNum; i++)
            out[i] = new ObservableValue("D" + i, bits).setDescription("Read Data Bus " + i);

        addrBits = attr.get(Keys.ADDR_BITS);
        size = 1 << addrBits;
        memory = new DataField(size);
        label = attr.getLabel();
        isProgramMemory = attr.get(Keys.IS_PROGRAM_MEMORY);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        writePorts = new ArrayList<>(writePortNum);
        for (int i = 0; i < writePortNum; i++)
            writePorts.add(new WritePort(inputs.get(i * 3), inputs.get(i * 3 + 1), inputs.get(i * 3 + 2)));

        readPorts = new ArrayList<>(readPortNum);
        for (int i = 0; i < readPortNum; i++)
            readPorts.add(new ReadPort(inputs.get(writePortNum * 3 + i), out[i]));

        clkIn = inputs.get(inputs.size() - 1).checkBits(1, this).addObserverToValue(this);
    }

    @Override
    public ObservableValues getOutputs() {
        return new ObservableValues(out);
    }

    @Override
    public void readInputs() {
        boolean clk = clkIn.getBool();
        if (clk && !lastClk)
            for (WritePort rp : writePorts)
                rp.readInputs();

        lastClk = clk;
        for (ReadPort rp : readPorts)
            rp.readInput();
    }

    @Override
    public void writeOutputs() {
        for (ReadPort rp : readPorts)
            rp.writeOutput();
    }

    @Override
    public DataField getMemory() {
        return memory;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getDataBits() {
        return bits;
    }

    @Override
    public int getAddrBits() {
        return addrBits;
    }

    @Override
    public boolean isProgramMemory() {
        return isProgramMemory;
    }

    @Override
    public void setProgramMemory(DataField dataField) {
        memory.setDataFrom(dataField);
    }

    private final class WritePort {
        private final ObservableValue en;
        private final ObservableValue a;
        private final ObservableValue d;

        private WritePort(ObservableValue en, ObservableValue a, ObservableValue d) throws BitsException {
            this.en = en.checkBits(1, MultiPortRAM.this);
            this.a = a.checkBits(addrBits, MultiPortRAM.this);
            this.d = d.checkBits(bits, MultiPortRAM.this);
        }

        private void readInputs() {
            if (en.getBool()) {
                int addr = (int) a.getValue();
                long data = d.getValue();
                memory.setData(addr, data);
            }
        }
    }

    private final class ReadPort {
        private final ObservableValue a;
        private final ObservableValue d;
        private int addr;

        private ReadPort(ObservableValue a, ObservableValue d) throws BitsException {
            this.a = a.checkBits(addrBits, MultiPortRAM.this).addObserverToValue(MultiPortRAM.this);
            this.d = d.checkBits(bits, MultiPortRAM.this);
        }

        private void readInput() {
            addr = (int) a.getValue();
        }

        private void writeOutput() {
            d.setValue(memory.getDataWord(addr));
        }
    }
}

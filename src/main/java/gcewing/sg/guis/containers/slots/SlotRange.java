package gcewing.sg.guis.containers.slots;

public class SlotRange {

    public int firstSlot;
    public int numSlots;
    public boolean reverseMerge;
    private int inventorySlotSize;
    public SlotRange(int inventorySlotsSize){
        this.inventorySlotSize = inventorySlotsSize;
    }

    public SlotRange() {
        firstSlot = inventorySlotSize;
    }

    public void end() {
        numSlots = inventorySlotSize - firstSlot;
    }

    public boolean contains(int slot) {
        return slot >= firstSlot && slot < firstSlot + numSlots;
    }

    @Override
    public String toString() {
        return String.format("SlotRange(%s to %s)", firstSlot, firstSlot + numSlots - 1);
    }
}
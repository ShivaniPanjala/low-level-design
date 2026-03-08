/*
Functional Requirements
1. Display available items.
2. Accept coins/notes/cards.
3. Allow user to select product.
4. Dispense selected product.
5. Return change if excess money inserted.
6. Handle out-of-stock items.
7. Allow admin to refill items.

uses StatePattern

VendingMachine -> Inventory(Manages all Item slots) -> ItemSlot -> Item


Idle state
  |
  | insert money
  v
HasMoney state
  |
  | select item
  v
Dispensing state
  |
  | done
  v
Idle state


If interviewer asks why we added StateRegistry, say:
"Since states are stateless, we cache and reuse them using a registry instead of creating new objects during every transition."

                 StateRegistry
                      |
          ---------------------------
          |            |           |
       IdleState   HasMoneyState   DispenseState

VendingMachine
      |
      | uses
      v
currentState  ← fetched from registry

VendingMachine
     |
     |--- Inventory
     |--- CashRegistry
     |--- double amount
     |--- currentState



interface PaymentStrategy{}

enum StateType {
IDLE,
HAS_MONEY,
DISPENSE
}

StateRegistry -> think of this like a cache which stores state objects, we create the state objects only once.
---------------------
- Map<StateType, VendingState> cache= new HashMap()
+ static VendingState getState(StateType type)

enum Denominations{
 TEN(value),
 FIVE(5),
 TWO(2),
 ONE(1);
 - int value
 + int getValue();
 }

 CashRegistry
 ----------------------
 - Map<Denominations, Integer> cashMap = new HashMap<>();
 + void add(Denomination d, int count)
 + void remove(Denomination d, int count)
 + int getAvailable(Denominations d)


VendingMachine
-------------------
-Inventory inventory
- double balance
- VendingState currentSate  = StateRegistry.getState(StateType.IDLE)
- CashRegistry cashRegistry
-----------------------------
+ setState(vendingState state)
+ insertMoney()
+ selectItem(slotId)
+ dispenseItem()

Inventory
--------------------
- Map<String, ItemSlot> itemSlots = new HashMap<>();
----------------------------------------
+ void addItem(slotId, item, quantity)
+ ItemSlot getSlot(slotId)
+ boolean isItemAvailable(slotId)
+ void updateQuantity(slotId)

ItemSlot
------------
- string slotId
- Item item
- int quantity
-------------------------
+ boolean isAvailable()
+ RemoveItem()
+ getItem()

Item
-------------
- string ItemName
- double price
----------------------
+ getPrice()
+ getName()


interface VendingState
----------------------
+ insertMoney(VendingMachine machine, double amount)
+ selectItem(VendingMachine machine, String slotId)
+ dispenseItem(VendingMachine machine)

IdleState implements VendingState
----------------------------------------
    + insertMoney(VendingMachine machine, double amount)
    + selectItem(VendingMachine machine, String slotId)   (not allowed)
    + dispenseItem(VendingMachine machine)                (not allowed)


HasMoneyState implements VendingState
----------------------------------------
    + insertMoney(VendingMachine machine, double amount)
    + selectItem(VendingMachine machine, String slotId)
    + dispenseItem(VendingMachine machine)                (not allowed)

DispenseState implements VendingState
----------------------------------------
    + insertMoney(VendingMachine machine, double amount)    (not allowed)
    + selectItem(VendingMachine machine, String slotId)     (not allowed)
    + dispenseItem(VendingMachine machine)



*/

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* ---------------- STATE TYPES ---------------- */
enum StateType {
    IDLE,
    HAS_MONEY,
    DISPENSE
}

/* ---------------- STATE REGISTRY (CACHE) ---------------- */
class StateRegistry {
    private static Map<StateType, VendingState> cache = new HashMap<>();

    static {
        cache.put(StateType.IDLE,  new IdleState());
        cache.put(StateType.HAS_MONEY,  new HasMoneyState());
        cache.put(StateType.DISPENSE,  new DispensingState());
    }

    public static VendingState getState(StateType stateType) {
        return cache.get(stateType);
    }
}

/* ---------------- DENOMINATIONS ---------------- */
enum Denomination {
    TEN(10),
    FIVE(5),
    TWO(2),
    ONE(1);

    private int value;

    Denominations(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

/* ---------------- CASH REGISTRY ---------------- */
class CashRegistry{
    Map<Denomination, Integer> cashMap = new HashMap<>();

    public void add(Denomination d, int count ) {
        cashMap.put(d, cashMap.getOrDefault(d, 0) + count);
    }

    public void remove(Denomination d, int count) {
        cashMap.put(d, cashMap.get(d) - count);
    }

    public int getAvailable(Denomination d) {
        return cashMap.getOrDefault(d, 0);
    }

    public void printCash(){
        System.out.println("Cash in machine: " + cashMap);
    }
}

/* ---------------- VENDING MACHINE ---------------- */
class VendingMachine {
    Inventory inventory;
    double balance;
    ItemSlot selectedSlot;
    CashRegistry cashRegistry;
    VendingState currentState;


    VendingMachine() {
        this.inventory = new Inventory();
        balance = 0;
        this.currentState = StateRegistry.getState(StateType.IDLE);
        this.cashRegistry = new CashRegistry();
    }

    public void setState(VendingState state) {
        this.currentState = state;
    }

    public void insertMoney(Denomination d, int count) {
        currentState.insertMoney(this, d, count);
    }

    public void selectItem(String slotId) {
        currentState.selectItem(this, slotId);
    }

    public void dispenseItem() {
        currentState.dispenseItem(this);
    }
}

/* ---------------- INVENTORY ---------------- */
class Inventory {
    Map<String, ItemSlot> itemSlots = new HashMap<>();

    public void addItem(String slotId, Item item, int quantity) {
        itemSlots.put(slotId, new ItemSlot(slotId, item, quantity));
    }

    public ItemSlot getSlot(String slotId){
        return itemSlots.get(slotId);
    }

    public boolean isItemAvailable(String slotId) {
        ItemSlot slot = getSlot(slotId);
        return slot != null && slot.isAvailable();
    }

}

/* ---------------- ITEM SLOT ---------------- */
class ItemSlot {
    String slotId;
    Item item;
    int quantity;

    ItemSlot(String slotId, Item item, int quantity) {
        this.slotId = slotId;
        this.item = item;
        this.quantity = quantity;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public Item dispenseItem() {
        if(quantity > 0) {
            quantity--;
        }
        return item;
    }

    Item getItem() {
        return item;
    }



}

/* ---------------- ITEM ---------------- */
class Item {
    String itemName;
    double price;
    Item(String itemName, double price) {
        this.itemName = itemName;
        this.price = price;
    }

    double getPrice() {
        return price;
    }

    String getItemName() {
        return itemName;
    }

}

/* ---------------- STATE INTERFACE ---------------- */
interface VendingState {
    void insertMoney(VendingMachine vm, Denomination d,  int  count);
    void selectItem(VendingMachine vm, String slotId);
    void dispenseItem(VendingMachine vm);
}


/* ---------------- IDLE STATE ---------------- */
class IdleState implements VendingState {
    @Override
    public void insertMoney(VendingMachine vm, Denomination d,  int  count) {
        vm.cashRegistry.add(d, count);
        vm.balance += d.getValue() * count;
        System.out.println("Inserted: "+ d +" x "+count);

        vm.setState(StateRegistry.getState(StateType.HAS_MONEY));
    }

    @Override
    public void selectItem(VendingMachine vm, String slotId) {
        System.out.println("Insert money first");

    }

    @Override
    public void dispenseItem(VendingMachine vm) {
        System.out.println("No money inserted");
    }
}
class HasMoneyState implements VendingState {
    @Override
    public void insertMoney(VendingMachine vm, Denomination d,  int  count) {
        vm.cashRegistry.add(d, count);
        vm.balance += d.getValue()*count;
        System.out.println("Added more money");
    }

    @Override
    public void selectItem(VendingMachine vm, String slotId) {
        if(!vm.inventory.isItemAvailable(slotId)){
            System.out.println("Item out of stock");
            return;
        }

        ItemSlot slot = vm.inventory.getSlot(slotId);

        if(vm.balance < slot.getItem().getPrice()){
            System.out.println("Insufficient balance");
            return;
        }

        System.out.println("Item selected: " + slot.getItem().getItemName());

        vm.selectedSlot = slot;
        vm.setState(StateRegistry.getState(StateType.DISPENSE));
    }

    @Override
    public void dispenseItem(VendingMachine vm) {
        System.out.println("Select item first");
    }
}

/* ---------------- DISPENSING STATE ---------------- */
class DispensingState implements VendingState {
    @Override
    public void insertMoney(VendingMachine vm, Denomination d,  int  count) {
        System.out.println("Please wait, dispensing item");
    }

    @Override
    public void selectItem(VendingMachine vm, String slotId) {
        System.out.println("Already dispensing");
    }

    @Override
    public void dispenseItem(VendingMachine vm) {

        Item item = vm.selectedSlot.dispenseItem();

        vm.balance -= item.getPrice();

        System.out.println("Item dispensed: " + item.getItemName());

        if(vm.balance > 0){
            System.out.println("Returning change: " + vm.balance);
            returnChange(vm);
        }

        vm.selectedSlot = null;
        vm.setState(StateRegistry.getState(StateType.IDLE));
    }

    private void returnChange(VendingMachine vm){

        double change = vm.balance;

        System.out.println("Returning change: "+ change);

        for(Denomination d : Denomination.values()){

            int value = d.getValue();

            int needed = (int)(change/value);

            int available = vm.cashRegistry.getAvailable(d);

            int toGive = Math.min(needed,available);

            if(toGive > 0){

                vm.cashRegistry.remove(d,toGive);

                change -= toGive*value;

                System.out.println("Returned "+d+" x "+toGive);
            }
        }

        vm.balance = 0;
    }
}

class Main {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        machine.inventory.addItem("A1", new Item("coke", 20), 20);
        machine.inventory.addItem("A2",new Item( "Pepsi", 30), 5);

        machine.insertMoney(Denomination.TEN, 3);
        machine.selectItem("A1");
        machine.dispenseItem();

    }
}


/*

enum StateType {
IDLE,
HAS_MONEY
DISPENSE
}
StateRegistry
-------------------
Map<StateType, VendingState> cache = new HashMap<>()
+ VendingState getState(StateType stateTpe)

enum Denomination {
TEN(5),
FIVE(20),
TWO(12),
ONE(6);
    private int value;
    Denomination(int value) {
        this.value = value
    }
    public int getValue() {}
}

cashRegistry
-------------------
- Map<Denomination, Integer> = new Hashmap<>();
+ void add(Denomination d, int count);
+ void remove(Denomination d, int count);
+ int getAvailable(Denomination d)

interface VendingState
--------------------------
+ addMoney(VendingMachine vm, double amount)
+ selectItem(VendingMachine vm, string slotId)
+ DispenseItem(VendingMachine vm)


IdleState implements VendingState
HasMoneyState implements VendingState
DispenseState implements VendingState


VendingMachine
---------------------
- Inventory inventory;
- double amount;
- CashRegistry cashRegistry;
- ItemSlot selectedSlot
- VendingState currentSate
-------------------------------
+ setState()
+ addMoney(double amount);
+ selectItem(string slotId);
+ dispenseItem();

Inventory
------------
- Map<String, ItemSlot> itemSlotMap = new HashMap<>()
+ addItem(String slotId, Item item, int quantity)
+ getSlot(String slotId)
+ isItemAvailable(String slotId)

ItemSlot
-------------
- string SlotId
- Item item
- int quantity
----------
+ isAvailable()
+ dispenseItem()
+ getItem()

Item
--------
- string itemName
- double Price
--------------------
+ getPrice()
+ getItemName()

*/
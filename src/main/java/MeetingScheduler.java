import java.util.List;

class TimeSlot {
    String startTime;
    String endTime;

    TimeSlot(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

class Room {
    String roomId;
    Calendar calendar;
     Room( String roomId) {
         this.roomId = roomId;
     }

}

class User {
    String userId;
    Calendar calendar;
    User( String userId) {
        this.userId = userId;
    }

}

class Calendar {
    List<TimeSlot> timeSlot;
    String bookedSlots;

    boolean isAvailable(TimeSlot timeslot) {}

    addSlot(TimeSlot timeSlot) {}
}


interface SchedulingStrategy {
    TimeSlot findAvailableTimeSlot()
}


public class MeetingScheduler {
    public static void Main(String [] args) {
        User user1 = new User("125");
        User user2 = new User("1772");
        Room room1 = new Room("R1");

        MeetingSchedulerStrategy strategy = new FirstAvailableStrategy();
    }
}


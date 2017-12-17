package tracker;

import exceptions.InternalTrackerException;
import exceptions.UnableReadState;
import exceptions.UnableStoreState;

public interface Tracker {
    void start(int threadsNumber) throws UnableReadState, InternalTrackerException;

    void stop() throws InternalTrackerException, UnableStoreState;
}

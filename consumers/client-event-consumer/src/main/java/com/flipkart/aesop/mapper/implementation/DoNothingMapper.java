package com.flipkart.aesop.mapper.implementation;

import com.flipkart.aesop.event.AbstractEvent;
import com.flipkart.aesop.event.implementation.DestinationEvent;
import com.flipkart.aesop.mapper.AbstractMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by aman.gupta on 14/08/15.
 */
public class DoNothingMapper extends AbstractMapper{

    @Override
    public List<AbstractEvent> mapSourceEventToDestinationEvent(AbstractEvent sourceEvent, Set<Integer> destinationGroupSet, int totalDestinationGroups) {
        List<AbstractEvent> l=new ArrayList<AbstractEvent>();
        l.add(sourceEvent);
        return l;
    }
}


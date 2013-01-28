package com.knaptus.poc.disruptor;

import com.lmax.disruptor.*;
import org.junit.Test;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class DisruptorTest {

    public static final int BUFFER_SIZE = 100000;

    @Test
    public void testDisruptor() {
        EchoEventHandler eventHandler = new EchoEventHandler();

        RingBuffer<ValueEvent> ringBuffer =
                new RingBuffer<ValueEvent>(ValueEvent.EVENT_FACTORY,
                        new SingleThreadedClaimStrategy(BUFFER_SIZE),
                        new SleepingWaitStrategy());

        SequenceBarrier barrier =
                ringBuffer.newBarrier();
        BatchEventProcessor<ValueEvent> eventProcessor =
                new BatchEventProcessor<ValueEvent>(ringBuffer, barrier, eventHandler);
        ringBuffer.setGatingSequences(eventProcessor.getSequence());


    }

}

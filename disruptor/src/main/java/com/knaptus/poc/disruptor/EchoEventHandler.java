package com.knaptus.poc.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class EchoEventHandler implements EventHandler<ValueEvent> {

    @Override
    public void onEvent(ValueEvent valueEvent, long l, boolean b) throws Exception {
        System.out.println(valueEvent.getValue());
    }
}

package il.ac.bgu.cs.bp.bpjs.examples;

import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.SingleResourceBProgram;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.listeners.InMemoryEventLoggingListener;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.listeners.StreamLoggerListener;
import il.ac.bgu.cs.bp.bpjs.events.BEvent;
import il.ac.bgu.cs.bp.bpjs.validation.eventpattern.EventPattern;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class ExternalEventsTest {

    
    @Test
    public void superStepTest() throws InterruptedException {
        final BEvent in1a = new BEvent("in1a");
        final BEvent in1b = new BEvent("in1b");
        final BEvent ext1 = new BEvent("ext1");
        final BProgramRunner sut = new BProgramRunner( new SingleResourceBProgram("ExternalEvents.js"));
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        new Thread( ()->sut.getBProgram().enqueueExternalEvent(ext1) ).start();
        
        sut.start();
        
        eventLogger.getEvents().forEach(e->System.out.println(e) );
        
        EventPattern expected = new EventPattern()
                .append(in1a).append(ext1).append(in1b);
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }    
}
/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.bpjs.eventselection;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.BProgram;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.StringBProgram;
import il.ac.bgu.cs.bp.bpjs.events.BEvent;
import il.ac.bgu.cs.bp.bpjs.eventsets.EventSet;
import il.ac.bgu.cs.bp.bpjs.exceptions.BPjsRuntimeException;

/**
 *
 * @author michael
 */
public class PriorityBasedSelectionTest {

	BProgram prog;
	Map<String, BEvent> events = new HashMap<>();
	Map<String, EventSet> eventSets = new HashMap<>();

	@Test
	public void testBadPredicate()  {
		try {

			StringBProgram bProgram = new StringBProgram("" //
					+ "bp.registerBThread('bt1',function(){" //
					+ "  bsync({request:bp.Event('X')});"//
					+ "});"//
					+ "bp.registerBThread('bt2',function(){"//
					+ "  bsync({request:bp.Event('Y')});"//
					+ "});"//
			);
			
			PrioritizedBThreadsEventSelectionStrategy eventSelectionStrategy = new PrioritizedBThreadsEventSelectionStrategy();
			eventSelectionStrategy.setPriority("bt1", 1);

			bProgram.setEventSelectionStrategy(eventSelectionStrategy);

			BProgramRunner bpr = new BProgramRunner(bProgram);
			bpr.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}

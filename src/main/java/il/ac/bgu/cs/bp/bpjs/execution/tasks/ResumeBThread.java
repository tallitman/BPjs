package il.ac.bgu.cs.bp.bpjs.execution.tasks;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BThreadSyncSnapshot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeContinuation;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A task that resumes a BThread from a BSync operation.
 */
public class ResumeBThread extends BPEngineTask {
    private final BEvent event;

    public ResumeBThread( BEvent selectedEvent, BThreadSyncSnapshot aBThread, BPEngineTask.Listener l) {
        super(aBThread, l);
        event = selectedEvent;
    }

    @Override
    BThreadSyncSnapshot callImpl(Context jsContext) {        
        final NativeContinuation continuation = bss.getContinuation();
        final Scriptable topLevelScope = ScriptableObject.getTopLevelScope(continuation);
        Object eventInJS = Context.javaToJS(event, topLevelScope);
        
        jsContext.resumeContinuation(continuation, topLevelScope, eventInJS);
        
        return null;
    }

    @Override
    public String toString() {
        return String.format("[ResumeBThread: %s event:%s]", bss, event);
    }
}

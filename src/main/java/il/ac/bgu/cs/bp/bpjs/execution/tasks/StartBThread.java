package il.ac.bgu.cs.bp.bpjs.execution.tasks;

import il.ac.bgu.cs.bp.bpjs.model.BThreadSyncSnapshot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A task to start a BThread, taking it from its entry point to its first {@code bp.sync}.
 */
public class StartBThread extends BPEngineTask {
    
    private final Scriptable scope;
    
    public StartBThread(Scriptable aScope, BThreadSyncSnapshot aBThread, BPEngineTask.Listener l) {
        super(aBThread, l);
        scope = aScope;
    }

    @Override
    BThreadSyncSnapshot callImpl( Context jsContext ) {
        final ScriptableObject executionScope = bss.createExecutionScope(scope, jsContext);
        jsContext.callFunctionWithContinuations(bss.getEntryPoint(), executionScope, new Object[0]);
        
        return null;
    }
   
    @Override
    public String toString() {
        return "[StartBThread " + bss.getName() + "]";
    }
}

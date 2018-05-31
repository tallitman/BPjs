package il.ac.bgu.cs.bp.bpjs.model;

import java.io.Serializable;
import java.util.Optional;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.NativeContinuation;
import org.mozilla.javascript.Scriptable;

import java.lang.reflect.Method;
import java.util.Objects;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptableObject;

/**
 * The state of a BThread at a synchronization point.
 *
 * @author orelmosheweinstock
 * @author Michael
 */
public class BThreadSyncSnapshot implements Serializable {

    /**
     * Name of the BThread described
     */
    private String name;

    /**
     * The JavaScript function that will be called when {@code this} BThread
     * runs.
     */
    private Function entryPoint;

    /**
     * BThreads may specify a function that runs when they are removed because
     * of a {@code breakUpon} statement.
     */
    private Function interruptHandler = null;

    /**
     * Continuation of the code.
     */
    private NativeContinuation continuation;

    /**
     * BSync statement of the BThread at the time of the snapshot.
     */
    private BSyncStatement bSyncStatement;
    
    public BThreadSyncSnapshot(String aName, Function anEntryPoint) {
        name = aName;
        entryPoint = anEntryPoint;
    }

    /**
     * Convenience constructor with default parameters.
     */
    public BThreadSyncSnapshot() {
        this(BThreadSyncSnapshot.class.getName(), null);
    }

    /**
     * Fully detailed constructor. Mostly useful for getting objects out of
     * serialized forms.
     *
     * @param name
     * @param entryPoint
     * @param interruptHandler
     * @param continuation
     * @param bSyncStatement
     */
    public BThreadSyncSnapshot(String name, Function entryPoint, Function interruptHandler, 
            NativeContinuation continuation, BSyncStatement bSyncStatement) {
        this.name = name;
        this.entryPoint = entryPoint;
        this.interruptHandler = interruptHandler;
        this.continuation = continuation;
        this.bSyncStatement = bSyncStatement;
    }

    /**
     * Creates the next snapshot of the BThread in a given run.
     *
     * @param aContinuation The BThread's continuation for the next sync.
     * @param aStatement The BThread's statement for the next sync.
     * @return a copy of {@code this} with updated continuation and statement.
     */
    public BThreadSyncSnapshot copyWith(NativeContinuation aContinuation, BSyncStatement aStatement) {
        BThreadSyncSnapshot retVal = new BThreadSyncSnapshot(name, entryPoint);
        retVal.continuation = aContinuation;
        retVal.setInterruptHandler(interruptHandler);

        retVal.bSyncStatement = aStatement;
        aStatement.setBthread(retVal);

        return retVal;
    }

    /**
     * Creates a scope for running JS code of {@code this} b-thread.
     * @param programScope Top level scope of the b-program {@code this} is part of.
     * @param ctxt 
     * @return scope for running b-thread code against.
     * TODO: Can be moved to StartBThread task class.
     */
    public ScriptableObject createExecutionScope(Scriptable programScope, Context ctxt) {
        try {
            // FIXME Should add a way for setting this b-thread's interrupt handler.
            ScriptableObject scope = (ScriptableObject)ctxt.newObject(programScope);
            scope.setPrototype(programScope);
            scope.setParentScope(null);
            
            Method setHdlrMethod = getClass().getDeclaredMethod("setInterruptHandler", Function.class);
            FunctionObject func = new FunctionObject("setInterruptHandler", setHdlrMethod, programScope);
            scope.put("setInterruptHandler", scope, "XXX");
                    
            return scope;
            
        } catch (NoSuchMethodException|SecurityException ex) {
            throw new RuntimeException("Cannot get access the setInterruptHandlerMethod", ex);
        }
    }

    public BSyncStatement getBSyncStatement() {
        return bSyncStatement;
    }

    public void setBSyncStatement(BSyncStatement stmt) {
        bSyncStatement = stmt;
        if (bSyncStatement.getBthread() != this) {
            bSyncStatement.setBthread(this);
        }
    }

    public NativeContinuation getContinuation() {
        return continuation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[BThreadSyncSnapshot: " + name + "]";
    }

    public Optional<Function> getInterrupt() {
        return Optional.ofNullable(interruptHandler);
    }

    public void setInterruptHandler(Function anInterruptHandler) {
        interruptHandler = anInterruptHandler;
    }

    public Function getEntryPoint() {
        return entryPoint;
    }
    
    @Override
    public int hashCode() {
        return (31 * Objects.hashCode(getName())) ^ 
                    ((continuation != null ) ? continuation.getImplementation().hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {

        // Quick circuit-breakers
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BThreadSyncSnapshot)) {
            return false;
        }
        BThreadSyncSnapshot other = (BThreadSyncSnapshot) obj;
        if (!Objects.equals(getName(), other.getName())) {
            return false;
        }
        
        // OK, need to delve into the continuations themselves.
        Boolean res = (Boolean)ContextFactory.getGlobal().call(cx -> {
            // Must perform the comparison in a context with a top call, as some
            // standard objects (XML) need ScriptRuntime.getTopCallScope.
            ScriptableObject top = cx.initStandardObjects();
            Boolean internalRes = (Boolean)ScriptRuntime.doTopCall((Callable)(c, scope, thisObj, args) -> {
                return NativeContinuation.equalImplementations(continuation, other.continuation);
            }, cx, top, top, null, false);
            return internalRes;
        });
        
        return res;
        
    }

}

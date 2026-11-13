package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public abstract class ReactionsRoutineWrapper extends RoutineBase {
    // TODO: Replace with the constants from the Reactions language
    protected final String MIR_ROUTINES_PACKAGE_PREFIX = "mir.routines.";
    protected final int MIR_ROUTINES_PACKAGE_PREFIX_LENGTH = MIR_ROUTINES_PACKAGE_PREFIX.length();

    private AbstractRoutinesFacade callerRoutinesFacade;
    private UnnamedParamIt unnamedParamIt;

    /**
     * Builds a direct RoutinesFacadesProvider for the Routine to be wrapped:
     * 
     * If the Routine is called "SomeRoutine", then it builds
     * "SomeRoutinesFacadesProvider".
     */
    private Function<ReactionExecutionState, RoutinesFacadesProvider> roFacProvFunc;
    /**
     * Builds a direct RoutinesFacade for the Routine to be wrapped:
     * 
     * If the Routine is called "SomeRoutine", then it builds "SomeRoutinesFacade".
     */
    private BiFunction<RoutinesFacadesProvider, ReactionsImportPath, RoutinesFacade> roFacFunc;
    /**
     * Builds a Routine by using its constructor.
     */
    private Supplier<Routine> roFunc;

    public ReactionsRoutineWrapper() {
    }

    public RoutinesFacade getCallerRoutinesFacade() {
        return this.callerRoutinesFacade;
    }

    public void setCallerRoutinesFacade(AbstractRoutinesFacade callerRoutineFacade) {
        this.callerRoutinesFacade = callerRoutineFacade;
        setExecutionState(getCallerRoutinesFacade().getExecutionState());
        setCallHierarchyHaving(getCallerRoutinesFacade().getCurrentCaller());
    }

    /**
     * A convenience method to set the Routine call parameters quickly and without
     * type-casting.
     */
    protected <T> T nextUnnamedParam() {
        return this.unnamedParamIt.next();
    }

    protected RoutinesFacadesProvider getProvider() {
        return roFacProvFunc.apply(getExecutionState());
    }

    @SuppressWarnings("unchecked")
    protected <T extends RoutinesFacade> T getFacade() {
        var prov = getProvider();
        var facade = (T) roFacFunc.apply(prov, getImportPath(prov));

        /*
         * Normally the RoutinesFacadesProvider sets the state of the RoutinesFacade.
         * However, since the RoutinesFacade function is also configurable, manually set
         * the state of the generated RoutinesFacade, in order to make sure that it is
         * set.
         */
        facade.setExecutionState(getExecutionState());
        return facade;
    }

    protected ReactionsImportPath getImportPath(RoutinesFacadesProvider provider) {
        return ReactionsImportPath
                .fromPathString(provider.getClass().getPackageName().substring(MIR_ROUTINES_PACKAGE_PREFIX_LENGTH));
    }

    @Override
    protected boolean executeRoutine() throws IOException {
        init();
        return roFunc.get().execute();
    }

    public Function<ReactionExecutionState, RoutinesFacadesProvider> getProviderFunc() {
        return roFacProvFunc;
    }

    /**
     * Sets how the direct RoutinesFacadesProvider of the wrapped Routine is built.
     * Also sets how the direct RoutinesFacade of the wrapped Routine is built, if
     * it has not been set yet.
     */
    public void setProviderFunc(Function<ReactionExecutionState, RoutinesFacadesProvider> roFacProvFunc) {
        this.roFacProvFunc = roFacProvFunc;
        if (this.roFacFunc == null) {
            this.roFacFunc = (prov, imp) -> prov.getRoutinesFacade(imp);
        }
    }

    public BiFunction<RoutinesFacadesProvider, ReactionsImportPath, RoutinesFacade> getFacadeFunc() {
        return roFacFunc;
    }

    public void setFacadeFunc(BiFunction<RoutinesFacadesProvider, ReactionsImportPath, RoutinesFacade> roFacFunc) {
        this.roFacFunc = roFacFunc;
    }

    public Supplier<Routine> getRoutineFunc() {
        return roFunc;
    }

    public void setRoutineFunc(Supplier<Routine> roFunc) {
        this.roFunc = roFunc;
    }

    public void init() {
        this.unnamedParamIt = getUnnamedParamIt();
        setup();
    }

    protected abstract void setup();
}

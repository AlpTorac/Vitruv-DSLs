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

    private Function<ReactionExecutionState, RoutinesFacadesProvider> roFacProvFunc;
    private BiFunction<RoutinesFacadesProvider, ReactionsImportPath, RoutinesFacade> roFacFunc;
    private Supplier<Routine> roFunc;

    public ReactionsRoutineWrapper() {}

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
        return (T) roFacFunc.apply(prov, getImportPath(prov));
    }

    protected ReactionsImportPath getImportPath(RoutinesFacadesProvider provider) {
        return ReactionsImportPath
                .fromPathString(provider.getClass().getPackageName().substring(MIR_ROUTINES_PACKAGE_PREFIX_LENGTH));
    }

    @Override
    protected boolean executeRoutine() throws IOException {
        return roFunc.get().execute();
    }

    public Function<ReactionExecutionState, RoutinesFacadesProvider> getProviderFunc() {
        return roFacProvFunc;
    }

    public void setProviderFunc(Function<ReactionExecutionState, RoutinesFacadesProvider> roFacProvFunc) {
        this.roFacProvFunc = roFacProvFunc;
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

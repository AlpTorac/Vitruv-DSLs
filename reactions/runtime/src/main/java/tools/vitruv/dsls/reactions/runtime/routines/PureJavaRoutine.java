package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;

public abstract class PureJavaRoutine extends RoutineBase {
    protected abstract static class PureJavaMatch extends Match {
        private RoutineBase containingRoutine;

        /*
         * Instead of InputParameters, RetrievedValues and CreatedValues; just declare
         * fields for them in the concrete Java Routine
         */

        public PureJavaMatch(RoutineBase containingRoutine) {
            super(containingRoutine.getExecutionState());
            this.containingRoutine = containingRoutine;
        }

        public RoutineBase getContainingRoutine() {
            return this.containingRoutine;
        }

        /*
         * Declare the correspondence source supplier methods in the concrete Routine:
         * 
         * getCorrespondenceSource<EXPECTED_CORRESPONDENT_TYPE><NUMBER_OF_METHOD>
         * getCorrepondenceSourceEObject
         * 
         * If this scheme would lead to duplicate method names, just add
         * <NUMBER_OF_METHOD> to the end:
         * 
         * getCorrepondenceSourceEObject2
         */

        public abstract boolean match() throws IOException;
    }

    protected abstract static class PureJavaCreate extends Create {
        private RoutineBase containingRoutine;

        public PureJavaCreate(RoutineBase containingRoutine) {
            super(containingRoutine.getExecutionState());
            this.containingRoutine = containingRoutine;
        }

        public RoutineBase getContainingRoutine() {
            return this.containingRoutine;
        }

        /*
         * Use createObject(() -> {return ...;}) to create new EObjects
         */
        public abstract void createElements();
    }

    protected abstract static class PureJavaUpdate extends Update {
        private RoutineBase containingRoutine;

        public PureJavaUpdate(RoutineBase containingRoutine) {
            super(containingRoutine.getExecutionState());
            this.containingRoutine = containingRoutine;
        }

        public RoutineBase getContainingRoutine() {
            return this.containingRoutine;
        }

        public abstract void updateModels();
    }

    protected boolean executeRoutine() throws IOException {
        if (!getMatchBlock().match())
            return false;

        getCreateBlock().createElements();

        getUpdateBlock().updateModels();
        return true;
    }

    protected abstract PureJavaMatch getMatchBlock();

    protected abstract PureJavaCreate getCreateBlock();

    protected abstract PureJavaUpdate getUpdateBlock();
}

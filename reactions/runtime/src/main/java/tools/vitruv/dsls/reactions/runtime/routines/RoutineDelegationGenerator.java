package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.List;
import java.util.function.Function;

public class RoutineDelegationGenerator {
    private Function<List<?>, RoutineDelegation> delGenFunc;
    public RoutineDelegationGenerator(Function<List<?>, RoutineDelegation> delGenFunc) {
        this.delGenFunc = delGenFunc;
    }
    public RoutineDelegation generateRoutineDelegation(List<?> inputParameters) {
        return delGenFunc.apply(inputParameters);
    }
}

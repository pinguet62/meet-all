import {Observable, OperatorFunction} from "rxjs";
import {mergeMap} from "rxjs/operators";

/**
 * Like {@link rxjs/finalize} but with support for {@link Observable} parameter.
 */
export function finalize<I, O>(action: Observable<O>): OperatorFunction<I, I> {
    return (source: Observable<I>) =>
        action.pipe(
            mergeMap(() => source)
        );
}

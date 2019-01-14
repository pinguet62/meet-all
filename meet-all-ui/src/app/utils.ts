import {Observable, OperatorFunction, throwError} from 'rxjs';
import {catchError, mapTo, mergeMap} from 'rxjs/operators';

/**
 * Like {@link rxjs/finalize} but with support for {@link Observable} parameter.
 */
export function finalize<I, O>(action: Observable<O>): OperatorFunction<I, I> {
    return (source: Observable<I>) =>
        source
            .pipe(mergeMap(sourceResult => action.pipe(mapTo(sourceResult))))
            .pipe(catchError(error => action.pipe(mergeMap(() => throwError(error)))));
}

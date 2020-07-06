import {Observable, OperatorFunction, throwError} from 'rxjs';
import {catchError, mapTo, mergeMap} from 'rxjs/operators';

export interface ApiError {
    timestamp: string; // "2020-07-05T11:37:05.194+00:00"
    path: string;
    status: number;
    error: string;
    message: string | null | undefined;
    requestId: string;
}

export function noOp() {
}

/**
 * Like {@link rxjs/finalize} but with support for {@link Observable} parameter.
 */
export function finalize<I, O>(action: Observable<O>): OperatorFunction<I, I> {
    return (source: Observable<I>) =>
        source
            .pipe(mergeMap(sourceResult => action.pipe(mapTo(sourceResult))))
            .pipe(catchError(error => action.pipe(mergeMap(() => throwError(error)))));
}

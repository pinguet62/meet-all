import {LoadingController} from "@ionic/angular";
import {defer, from, Observable} from "rxjs";
import {mergeMap} from "rxjs/operators";
import {finalize} from "./utils";

export function processLoading<T>(loadingController: LoadingController, action: Observable<T>): Observable<T> {
    return from(loadingController.create())
        .pipe(mergeMap((loader: HTMLIonLoadingElement) =>
            from(loader.present() as Promise<void>)
                .pipe(mergeMap(() => action
                    .pipe(finalize(defer(() => loader.dismiss() as any as Promise<void>)))))));
}

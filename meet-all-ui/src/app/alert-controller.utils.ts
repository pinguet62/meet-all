import {AlertController} from "@ionic/angular";
import {AlertOptions} from "@ionic/core";
import {from, MonoTypeOperatorFunction, throwError} from "rxjs";
import {catchError, mergeMap} from "rxjs/operators";

export function catchErrorAndPresentAlert<T>(alertController: AlertController, opts?: AlertOptions): MonoTypeOperatorFunction<T> {
    return catchError((err: any) =>
        from(alertController.create(opts))
            .pipe(mergeMap((alerter: HTMLIonAlertElement) => from(alerter.present() as Promise<void>)))
            .pipe(mergeMap(() => throwError(err))));
}

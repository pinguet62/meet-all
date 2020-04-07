import {Type} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {LoadingOptions} from '@ionic/core';

export function buildLocation(url: string): Location {
    const a = document.createElement('a');
    a.href = url;
    return a as any as Location;
}

function createHTMLIonLoadingElement(): HTMLIonLoadingElement {
    return {
        present: () => Promise.resolve(),
        dismiss: () => Promise.resolve(),
    } as any as HTMLIonLoadingElement;
}

export class LoadingControllerFake extends LoadingController {

    create(opts?: LoadingOptions): Promise<HTMLIonLoadingElement> {
        return Promise.resolve(createHTMLIonLoadingElement());
    }

    dismiss(data?: any, role?: string, id?: string): Promise<boolean> {
        return Promise.resolve(true);
    }

    getTop(): Promise<HTMLIonLoadingElement> {
        return Promise.resolve(createHTMLIonLoadingElement());
    }

}

export function allMethodNames<T>(type: Type<T>): ReadonlyArray<keyof T> {
    return Object.getOwnPropertyNames(type.prototype) as any as ReadonlyArray<keyof T>;
}

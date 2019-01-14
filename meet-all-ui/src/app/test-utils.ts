import {Type} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {Components, LoadingOptions} from '@ionic/core';

export function buildLocation(url: string): Location {
    const a = document.createElement('a');
    a.href = url;
    return a as any as Location;
}

interface HTMLIonLoadingElement extends Components.IonLoading, HTMLStencilElement {}
// tslint:disable-next-line
var HTMLIonLoadingElement: {
    prototype: HTMLIonLoadingElement;
    new (): HTMLIonLoadingElement;
};

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

    dismiss(data?: any, role?: string, id?: string): Promise<void> {
        return Promise.resolve();
    }

    getTop(): Promise<HTMLIonLoadingElement> {
        return Promise.resolve(createHTMLIonLoadingElement());
    }

}

export function allMethodNames<T>(type: Type<T>): ReadonlyArray<keyof T> {
    return Object.keys(type.prototype) as any as ReadonlyArray<keyof T>;
}

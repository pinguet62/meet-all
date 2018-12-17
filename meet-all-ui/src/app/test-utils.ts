import {LoadingController} from '@ionic/angular';
import {Components, LoadingOptions} from '@ionic/core';

interface HTMLIonLoadingElement extends Components.IonLoading, HTMLStencilElement {}
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

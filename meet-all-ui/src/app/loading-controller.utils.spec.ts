import {LoadingController} from '@ionic/angular';
import {Observable, Subscriber} from 'rxjs';
import {processLoading} from './loading-controller.utils';

function createSpyPromise<T>(callback: (...args: any[]) => T): Promise<T> {
    const promise = new Promise<T>(resolve => {
        setTimeout(() => { // avoid direct resolve
            resolve(callback());
        }, 0);
    });
    spyOn(promise, 'then').and.callThrough();
    return promise;
}

describe('loading-controller.utils', () => {
    describe('processLoading', () => {
        let callOrder: string[];
        let loaderPresentPresent: Promise<any>;
        let loaderPresentDismiss: Promise<any>;
        let loader: HTMLIonLoadingElement;
        let loadingController: jasmine.SpyObj<LoadingController>;
        beforeEach(() => {
            callOrder = [];
            loaderPresentPresent = createSpyPromise(() => callOrder.push('loaderPresentPresent'));
            loaderPresentDismiss = createSpyPromise(() => callOrder.push('loaderPresentDismiss'));
            loader = {present: () => loaderPresentPresent, dismiss: () => loaderPresentDismiss} as HTMLIonLoadingElement;
            loadingController = jasmine.createSpyObj<LoadingController>(LoadingController.name, ['create']);
            loadingController.create.and.returnValue(Promise.resolve(loader));
        });

        it('success', done => {
            const sourceNext = jasmine.createSpy('source').and.callFake((observer: Subscriber<string>) => {
                callOrder.push('source');
                observer.next('source');
            });
            const observable = new Observable(sourceNext);

            const result = processLoading(loadingController, observable);

            result.subscribe(value => {
                expect(value).toEqual('source');
                expect(loaderPresentPresent.then).toHaveBeenCalled();
                expect(loaderPresentDismiss.then).toHaveBeenCalled();
                expect(callOrder).toEqual(['loaderPresentPresent', 'source', 'loaderPresentDismiss']);
                done();
            });
        });

        it('error', done => {
            const sourceError = jasmine.createSpy('source').and.callFake((observer: Subscriber<string>) => {
                callOrder.push('source');
                observer.error(new Error('source'));
            });
            const observable = new Observable(sourceError);

            const result = processLoading(loadingController, observable);

            result.subscribe(
                () => done.fail(),
                error => {
                    expect(error.message).toEqual('source');
                    expect(loaderPresentPresent.then).toHaveBeenCalled();
                    expect(loaderPresentDismiss.then).toHaveBeenCalled();
                    expect(callOrder).toEqual(['loaderPresentPresent', 'source', 'loaderPresentDismiss']);
                    done();
                });
        });
    });
});

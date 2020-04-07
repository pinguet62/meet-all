import {AlertController} from '@ionic/angular';
import {of, throwError} from 'rxjs';
import {catchErrorAndPresentAlert} from './alert-controller.utils';

describe('alert-controller.utils', () => {
    describe('createAndPresentAlert', () => {
        let alerterPresentPromise: Promise<void>;
        let alerter: HTMLIonAlertElement;
        let alertController: jasmine.SpyObj<AlertController>;
        beforeEach(() => {
            alerterPresentPromise = Promise.resolve();
            spyOn(alerterPresentPromise, 'then').and.callThrough();
            alerter = {present: () => alerterPresentPromise} as HTMLIonAlertElement;
            alertController = jasmine.createSpyObj<AlertController>(AlertController.name, ['create']);
            alertController.create.and.returnValue(Promise.resolve(alerter));
        });

        it('success: doesn\'t call AlertController', done => {
            const operator = catchErrorAndPresentAlert(alertController);

            of('expected')
                .pipe(operator)
                .subscribe(value => {
                    expect(value).toEqual('expected');
                    expect(alerterPresentPromise.then).not.toHaveBeenCalled();
                    done();
                });
        });

        it('error: call AlertController', done => {
            const operator = catchErrorAndPresentAlert(alertController);

            throwError(new Error('expected'))
                .pipe(operator)
                .subscribe(
                    () => done.fail(),
                    error => {
                        expect(error).toEqual(new Error('expected'));
                        expect(alerterPresentPromise.then).toHaveBeenCalled();
                        done();
                    });
        });
    });
});

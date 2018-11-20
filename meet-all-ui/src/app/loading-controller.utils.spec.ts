import {LoadingController} from "@ionic/angular";
import {OverlayBaseController} from "@ionic/angular/dist/util/overlay";
import {of, throwError} from "rxjs";
import {processLoading} from "./loading-controller.utils";

describe("loading-controller.utils", () => {
    describe("processLoading", () => {
        let loaderPresentPromise: Promise<void>;
        let loaderPresentDismiss: Promise<void>;
        let loader: { present: () => Promise<void>, dismiss: () => Promise<void> }; // HTMLIonLoadingElement
        let loadingController: jasmine.SpyObj<LoadingController>;
        beforeEach(() => {
            loaderPresentPromise = Promise.resolve();
            spyOn(loaderPresentPromise, 'then').and.callThrough();
            loaderPresentDismiss = Promise.resolve();
            spyOn(loaderPresentDismiss, 'then').and.callThrough();
            loader = {present: () => loaderPresentPromise, dismiss: () => loaderPresentDismiss};
            loadingController = jasmine.createSpyObj<LoadingController>(LoadingController.name, Object.keys(OverlayBaseController.prototype));
            loadingController.create.and.returnValue(Promise.resolve(loader));
        });

        it("success", (done) => {
            const observable = of("expected");

            const result = processLoading(loadingController, observable);

            result.subscribe(value => {
                expect(value).toEqual("expected");
                expect(loaderPresentPromise.then).toHaveBeenCalled();
                expect(loaderPresentDismiss.then).toHaveBeenCalled();
                done();
            });
        });

        it("error", (done) => {
            const observable = throwError(new Error("expected"));

            const result = processLoading(loadingController, observable);

            result.subscribe(
                () => done.fail(),
                error => {
                    expect(error).toEqual(new Error("expected"));
                    expect(loaderPresentPromise.then).toHaveBeenCalled();
                    expect(loaderPresentDismiss.then).toHaveBeenCalled();
                    done();
                });
        });
    });
});

import {Observable, Subscriber} from "rxjs";
import {finalize} from "./utils";

describe("utils", () => {
    describe("finalize", () => {
        it("success: should execute action and return original result", done => {
            let callOrder = [];
            let sourceNext = jasmine.createSpy("source").and.callFake((observer: Subscriber<string>) => {
                callOrder.push("source");
                observer.next("source");
            });
            let observerNext = jasmine.createSpy("finalize").and.callFake((observer: Subscriber<number>) => {
                callOrder.push("finalize");
                observer.next(42);
            });
            let action = Observable.create(observerNext);
            Observable.create(sourceNext)
                .pipe(finalize(action))
                .subscribe(next => {
                    expect(next).toEqual("source");
                    expect(observerNext).toHaveBeenCalled();
                    expect(callOrder).toEqual(["source", "finalize"]);
                    done()
                });
        });

        it("error: should execute action and throw original error", done => {
            let callOrder = [];
            let sourceError = jasmine.createSpy("source").and.callFake((observer: Subscriber<string>) => {
                callOrder.push("source");
                observer.error(new Error("source"));
            });
            let observerNext = jasmine.createSpy("finalize").and.callFake((observer: Subscriber<number>) => {
                callOrder.push("finalize");
                observer.next(42);
            });
            let action = Observable.create(observerNext);
            Observable.create(sourceError)
                .pipe(finalize(action))
                .subscribe(
                    () => done.fail(),
                    (error: Error) => {
                        expect(error.message).toEqual("source");
                        expect(observerNext).toHaveBeenCalled();
                        expect(callOrder).toEqual(["source", "finalize"]);
                        done();
                    });
        });
    });
});

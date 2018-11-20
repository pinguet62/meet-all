import {Observable, of, Subscriber, throwError} from "rxjs";
import {finalize} from "./utils";

describe("utils", () => {
    describe("finalize", () => {
        it("success: should execute action and return original result", done => {
            let observerNext = jasmine.createSpy("observer").and.callFake((observer: Subscriber<string>) => observer.next("any"));
            let action = Observable.create(observerNext);
            of("expected")
                .pipe(finalize(action))
                .subscribe(next => {
                    expect(next).toEqual("expected");
                    expect(observerNext).toHaveBeenCalled();
                    done()
                });
        });

        it("error: should execute action and throw original error", done => {
            let observerNext = jasmine.createSpy("observer").and.callFake((observer: Subscriber<string>) => observer.next("any"));
            let action = Observable.create(observerNext);
            throwError(new Error("expected"))
                .pipe(finalize(action))
                .subscribe(
                    () => done.fail(),
                    (error: Error) => {
                        expect(error.message).toEqual("expected");
                        expect(observerNext).toHaveBeenCalled();
                        done();
                    });
        });
    });
});

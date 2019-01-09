import {CommonModule} from "@angular/common";
import {Directive, ElementRef, Input, NgModule} from "@angular/core";
import {environment} from "../../environments/environment";

@Directive({selector: 'img[proxifiedSrc],ion-img[proxifiedSrc]'})
export class ProxifiedSrcDirective {

    constructor(private el: ElementRef<HTMLImageElement>) {
    }

    @Input('proxifiedSrc')
    set proxifiedSrc(value: string) {
        this.el.nativeElement.src = `${environment.apiUrl}/photo/${value}`
    }

}

@NgModule({
    imports: [CommonModule],
    declarations: [ProxifiedSrcDirective],
    exports: [ProxifiedSrcDirective],
})
export class ProxifiedSrcModule {
}

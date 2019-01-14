import {CommonModule} from '@angular/common';
import {Directive, ElementRef, Input, NgModule} from '@angular/core';
import {environment} from '../../environments/environment';

@Directive({selector: 'img[appProxifiedSrc],ion-img[appProxifiedSrc]'})
export class ProxifiedSrcDirective {

    constructor(private el: ElementRef<HTMLImageElement>) {
    }

    @Input('appProxifiedSrc')
    set proxifiedSrc(value: string) {
        this.el.nativeElement.src = `${environment.apiUrl}/photo/${value}`;
    }

}

@NgModule({
    imports: [CommonModule],
    declarations: [ProxifiedSrcDirective],
    exports: [ProxifiedSrcDirective],
})
export class ProxifiedSrcModule {
}

import {CommonModule} from '@angular/common';
import {Component, Input, NgModule} from '@angular/core';
import {IonicModule} from '@ionic/angular';

@Component({
    selector: 'app-badge-icon',
    template: `
        <ion-icon [name]="name" [color]="color"></ion-icon>
    `,
    styles: [`
        :host {
            position: relative;
            bottom: 35%;
            left: 65%;
        }
    `],
})
export class BadgeIconComponent {

    @Input()
    name: string;

    @Input()
    color: string;

}

@NgModule({
    imports: [
        // libs
        CommonModule,
        IonicModule,
    ],
    declarations: [BadgeIconComponent],
    exports: [BadgeIconComponent],
})
export class BadgeIconModule {
}

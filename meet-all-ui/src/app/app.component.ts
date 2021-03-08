import {Component} from '@angular/core';

@Component({
    selector: 'app-root',
    template: `
        <ion-app>
            <ion-router-outlet></ion-router-outlet>
            <app-version></app-version>
        </ion-app>`,
})
export class AppComponent {}

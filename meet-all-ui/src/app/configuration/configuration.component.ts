import {Component} from '@angular/core';
import {Geolocation} from '@ionic-native/geolocation/ngx';
import {from, noop} from 'rxjs';
import {flatMap, map} from 'rxjs/operators';
import {ConfigurationService} from './configuration.service';

@Component({
    selector: 'app-configuration',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title i18n="@@configuration.title">Configuration</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-button i18n="@@configuration.sendPosition" (click)="setPosition()">
                <ion-icon slot="start" name="navigate"></ion-icon>
                Send position
            </ion-button>
        </ion-content>`
})
export class ConfigurationComponent {
    constructor(private geolocation: Geolocation, private service: ConfigurationService) {
    }

    setPosition() {
        from(this.geolocation.getCurrentPosition())
            .pipe(map(geoposition => geoposition.coords))
            .pipe(flatMap(({latitude, longitude, altitude}) => this.service.setPosition(latitude, longitude, altitude)))
            .subscribe(noop);
    }
}

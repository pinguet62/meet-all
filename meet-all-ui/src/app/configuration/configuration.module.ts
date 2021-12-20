import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {Geolocation} from '@awesome-cordova-plugins/geolocation/ngx';
import {SecurityModule} from '../security';
import {ConfigurationComponent} from './configuration.component';
import {ConfigurationService} from './configuration.service';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        // app
        RouterModule.forChild([
            {path: '', component: ConfigurationComponent},
        ]),
        SecurityModule,
    ],
    declarations: [
        ConfigurationComponent,
    ],
    providers: [
        Geolocation,
        ConfigurationService,
    ],
})
export class ConfigurationModule {
}

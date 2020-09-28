import {NgModule} from '@angular/core';
import {IonicModule} from '@ionic/angular';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {SecurityModule} from '../security';
import {ConfigurationComponent} from './configuration.component';

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
    providers: [],
})
export class ConfigurationModule {
}

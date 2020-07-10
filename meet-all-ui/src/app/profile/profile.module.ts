import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {ProfileComponent} from './profile.component';
import {ProxifiedSrcModule} from '../shared/proxifiedSrc';
import {SecurityModule} from '../security';
import {ProfileService} from './profile.service';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        // app
        ProxifiedSrcModule,
        RouterModule.forChild([{path: '', component: ProfileComponent}]),
        SecurityModule,
    ],
    declarations: [
        ProfileComponent,
    ],
    providers: [
        ProfileService,
    ],
})
export class ProfileModule {
}

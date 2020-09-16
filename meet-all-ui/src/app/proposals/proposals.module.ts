import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {ProposalsComponent} from './proposals.component';
import {ProposalsService} from './proposals.service';
import {SecurityModule} from '../security';
import {ProxifiedSrcModule} from '../shared/proxifiedSrc';
import {ProfileComponent} from '../profile/profile.component';
import {ProfileModule} from '../profile/profile.module';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        ProfileModule,
        ProxifiedSrcModule,
        RouterModule.forChild([
            {path: '', component: ProposalsComponent},
            {path: ':proposalId/profile/:profileId', component: ProfileComponent}]),
        SecurityModule,
    ],
    declarations: [
        ProposalsComponent,
    ],
    providers: [
        ProposalsService,
    ],
})
export class ProposalsModule {
}

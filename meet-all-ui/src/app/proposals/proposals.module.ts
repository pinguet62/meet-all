import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {ProposalsPage} from './proposals.page';
import {ProposalsService} from './proposals.service';
import {SecurityModule} from '../security';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        RouterModule.forChild([{path: '', component: ProposalsPage}]),
        SecurityModule,
    ],
    declarations: [
        ProposalsPage,
    ],
    providers: [
        ProposalsService,
    ],
})
export class ProposalsModule {
}

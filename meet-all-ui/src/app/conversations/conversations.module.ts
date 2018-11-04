import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {IonicModule} from '@ionic/angular';
import {ConversationsPage} from './conversations.page';
import {Services} from '../services';

@NgModule({
    imports: [
        IonicModule,
        CommonModule,
        FormsModule,
        RouterModule.forChild([{path: '', component: ConversationsPage}]),
    ],
    declarations: [
        ConversationsPage,
    ],
    providers: [
        Services
    ],
})
export class ConversationsPageModule {
}

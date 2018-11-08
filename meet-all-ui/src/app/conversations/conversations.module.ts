import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {IonicModule} from '@ionic/angular';
import {ConversationsPage} from './conversations.page';
import {Services} from '../services';
import {ConversationsRoutingModule} from './conversations-routing.module';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        FormsModule,
        // app
        ConversationsRoutingModule,
    ],
    declarations: [
        ConversationsPage,
    ],
    providers: [
        Services,
    ],
})
export class ConversationsPageModule {
}

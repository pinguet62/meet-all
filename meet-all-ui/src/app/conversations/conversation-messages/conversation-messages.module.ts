import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SecurityModule} from '../../security';
import {ConversationsService} from '../conversations.service';
import {ConversationMessagesComponent} from './conversation-messages.component';
import {ProxifiedSrcModule} from '../../shared/proxifiedSrc';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        ProxifiedSrcModule,
        RouterModule.forChild([{path: '', component: ConversationMessagesComponent}]),
        SecurityModule,
    ],
    declarations: [
        ConversationMessagesComponent,
    ],
    providers: [
        ConversationsService,
    ],
})
export class ConversationMessagesModule {
}

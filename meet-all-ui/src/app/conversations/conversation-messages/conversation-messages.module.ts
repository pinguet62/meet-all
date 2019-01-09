import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SecurityModule} from '../../security';
import {ConversationsService} from '../conversations.service';
import {ConversationMessagesPage} from './conversation-messages.page';
import {ProxifiedSrcModule} from "../../shared/proxifiedSrc";

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        ProxifiedSrcModule,
        RouterModule.forChild([{path: '', component: ConversationMessagesPage}]),
        SecurityModule,
    ],
    declarations: [
        ConversationMessagesPage,
    ],
    providers: [
        ConversationsService,
    ],
})
export class ConversationMessagesModule {
}

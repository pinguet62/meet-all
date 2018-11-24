import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {IonicModule} from '@ionic/angular';
import {ConversationsPage} from './conversations.page';
import {ConversationsRoutingModule} from './conversations-routing.module';
import {ConversationMessagesPage} from './conversation-messages.page';
import {ConversationListPage} from './conversation-list.page';
import {SecurityModule} from '../security';
import {ConversationsService} from './conversations.service';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        ConversationsRoutingModule,
        SecurityModule,
    ],
    declarations: [
        ConversationsPage,
        ConversationListPage,
        ConversationMessagesPage,
    ],
    providers: [
        ConversationsService,
    ],
})
export class ConversationsPageModule {
}

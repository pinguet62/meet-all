import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {ConversationsPage} from './conversations.page';
import {ConversationMessagesPage} from './conversation-messages.page';
import {ConversationListPage} from './conversation-list.page';

@NgModule({
    imports: [
        RouterModule.forChild([{
            path: '', component: ConversationsPage, children: [
                {path: '', component: ConversationListPage},
                {path: ':id', component: ConversationMessagesPage},
            ]
        }]),
    ],
    exports: [RouterModule],
})
export class ConversationsRoutingModule {
}

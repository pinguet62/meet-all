import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SecurityGuard} from './security';

@NgModule({
    imports: [
        RouterModule.forRoot([
            {path: 'login', loadChildren: './login/login.module#LoginModule'},
            {path: '', loadChildren: './tabs/tabs.module#TabsPageModule', canActivate: [SecurityGuard]},
            {path: 'tabs/conversations/:conversationId/:profileId', loadChildren: './conversations/conversation-messages/conversation-messages.module#ConversationMessagesModule'},
            {path: 'tabs/credentials/create', loadChildren: './credentials/credential-create/credential-create.module#CredentialCreateModule'},
        ])
    ],
    exports: [RouterModule],
})
export class AppRoutingModule {
}

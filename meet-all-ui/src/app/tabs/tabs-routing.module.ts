import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {TabsPage} from './tabs.page';

@NgModule({
    imports: [RouterModule.forChild([
        {
            path: '',
            redirectTo: '/tabs/conversations',
            pathMatch: 'full',
        },
        {
            path: 'tabs',
            component: TabsPage,
            children: [
                {
                    path: 'conversations',
                    children: [{
                        path: '',
                        loadChildren: '../conversations/conversation-list/conversation-list.module#ConversationListModule',
                    }]
                },
                {
                    path: 'credentials',
                    children: [{
                        path: '',
                        loadChildren: '../credentials/credential-list/credential-list.module#CredentialListModule',
                    }]
                },
            ],
        },
    ])],
    exports: [RouterModule],
})
export class TabsPageRoutingModule {
}

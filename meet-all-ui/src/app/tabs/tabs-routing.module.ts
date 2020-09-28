import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {TabsComponent} from './tabs.component';

@NgModule({
    imports: [RouterModule.forChild([
        {
            path: '',
            redirectTo: '/tabs/conversations',
            pathMatch: 'full',
        },
        {
            path: 'tabs',
            component: TabsComponent,
            children: [
                {
                    path: 'proposals',
                    children: [{
                        path: '',
                        loadChildren: '../proposals/proposals.module#ProposalsModule',
                    }]
                },
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
                {
                    path: 'configuration',
                    children: [{
                        path: '',
                        loadChildren: '../configuration/configuration.module#ConfigurationModule',
                    }]
                },
            ],
        },
    ])],
    exports: [RouterModule],
})
export class TabsPageRoutingModule {
}

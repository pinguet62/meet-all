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
                        loadChildren: () => import('../proposals/proposals.module').then(m => m.ProposalsModule),
                    }]
                },
                {
                    path: 'conversations',
                    children: [{
                        path: '',
                        loadChildren: () => import('../conversations/conversation-list/conversation-list.module').then(m => m.ConversationListModule),
                    }]
                },
                {
                    path: 'credentials',
                    children: [{
                        path: '',
                        loadChildren: () => import('../credentials/credential-list/credential-list.module').then(m => m.CredentialListModule),
                    }]
                },
                {
                    path: 'configuration',
                    children: [{
                        path: '',
                        loadChildren: () => import('../configuration/configuration.module').then(m => m.ConfigurationModule),
                    }]
                },
            ],
        },
    ])],
    exports: [RouterModule],
})
export class TabsPageRoutingModule {
}

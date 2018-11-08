import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {TabsPage} from './tabs.page';

@NgModule({
    imports: [RouterModule.forChild([
        {
            path: '',
            redirectTo: '/tabs/(conversations:conversations)',
            pathMatch: 'full',
        },
        {
            path: 'tabs',
            component: TabsPage,
            children: [
                {
                    path: '',
                    redirectTo: '/tabs/(conversations:conversations)',
                    pathMatch: 'full',
                },
                {
                    path: 'conversations',
                    outlet: 'conversations',
                    loadChildren: '../conversations/conversations.module#ConversationsPageModule',
                },
                {
                    path: 'credentials',
                    outlet: 'credentials',
                    loadChildren: '../credentials/credentials.module#CredentialsModule',
                },
            ],
        },
    ])],
    exports: [RouterModule],
})
export class TabsPageRoutingModule {
}

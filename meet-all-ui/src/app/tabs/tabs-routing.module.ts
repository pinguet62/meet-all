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
                        loadChildren: '../conversations/conversations.module#ConversationsPageModule',
                    }]
                },
                {
                    path: 'credentials',
                    children: [{
                        path: '',
                        loadChildren: '../credentials/credentials.module#CredentialsModule',
                    }]
                },
            ],
        },
    ])],
    exports: [RouterModule],
})
export class TabsPageRoutingModule {
}

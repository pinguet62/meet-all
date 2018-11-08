import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {CredentialsPage} from './credentials.page';
import {CredentialCreatePage} from './credential-create.page';
import {CredentialListPage} from './credential-list.page';

@NgModule({
    imports: [
        RouterModule.forChild([{
            path: '', component: CredentialsPage, children: [
                {path: '', component: CredentialListPage},
                {path: 'create', component: CredentialCreatePage}
            ]
        }]),
    ],
    exports: [RouterModule],
})
export class CredentialsRoutingModule {
}

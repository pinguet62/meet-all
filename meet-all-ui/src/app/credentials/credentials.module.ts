import {IonicModule} from '@ionic/angular';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Services} from '../services';
import {CredentialCreatePage} from './credential-create.page';
import {CredentialListPage} from './credential-list.page';
import {CredentialsPage} from './credentials.page';
import {CredentialsRoutingModule} from './credentials-routing.module';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        FormsModule,
        // app
        CredentialsRoutingModule,
    ],
    declarations: [
        CredentialsPage,
        CredentialListPage,
        CredentialCreatePage,
    ],
    providers: [
        Services,
    ],
})
export class CredentialsModule {
}

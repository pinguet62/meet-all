import {IonicModule} from '@ionic/angular';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {CredentialCreatePage} from './credential-create.page';
import {CredentialListPage} from './credential-list.page';
import {CredentialsPage} from './credentials.page';
import {CredentialsRoutingModule} from './credentials-routing.module';
import {CredentialService} from './credential.service';
import {SecurityModule} from '../security';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        CredentialsRoutingModule,
        SecurityModule,
    ],
    declarations: [
        CredentialsPage,
        CredentialListPage,
        CredentialCreatePage,
    ],
    providers: [
        CredentialService,
    ],
})
export class CredentialsModule {
}

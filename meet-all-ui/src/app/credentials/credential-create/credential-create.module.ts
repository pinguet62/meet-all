import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SecurityModule} from '../../security';
import {CredentialService} from '../credential.service';
import {CredentialCreateComponent} from './credential-create.component';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        SecurityModule,
        RouterModule.forChild([{path: '', component: CredentialCreateComponent}]),
    ],
    declarations: [
        CredentialCreateComponent,
    ],
    providers: [
        CredentialService,
    ],
})
export class CredentialCreateModule {
}

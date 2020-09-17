import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SecurityModule} from '../../security';
import {CredentialModule} from '../credential.module';
import {CredentialListComponent} from './credential-list.component';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        SecurityModule,
        CredentialModule,
        RouterModule.forChild([
            {path: '', component: CredentialListComponent},
            {path: 'create', loadChildren: '../credential-create/credential-create.module#CredentialCreateModule'}]),
    ],
    declarations: [
        CredentialListComponent,
    ],
})
export class CredentialListModule {
}

import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {ModuleWithProviders, NgModule} from '@angular/core';
import {CredentialClient, CredentialService} from './credential.service';

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
    ],
    providers: [
        CredentialClient,
    ]
})
export class CredentialModule {

    static forRoot(): ModuleWithProviders {
        return {
            ngModule: CredentialModule,
            providers: [
                CredentialService, // app singleton
            ]
        };
    }
}

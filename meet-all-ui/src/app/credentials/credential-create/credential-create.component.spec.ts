import {HttpErrorResponse} from '@angular/common/http';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {ComponentFixture, fakeAsync, flush, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule, LoadingController, NavController, ToastController} from '@ionic/angular';
import {throwError} from 'rxjs';
import {allMethodNames, createHTMLIonToastElement, LoadingControllerFake} from '../../test-utils';
import {CredentialService, Provider} from '../credential.service';
import {CredentialCreateComponent} from './credential-create.component';

describe('credential-create.component', () => {
    let navController: jasmine.SpyObj<NavController>;
    let toastController: jasmine.SpyObj<ToastController>;
    let credentialService: jasmine.SpyObj<CredentialService>;
    beforeEach(() => {
        navController = jasmine.createSpyObj<NavController>(NavController.name, allMethodNames(NavController));
        toastController = jasmine.createSpyObj<ToastController>(ToastController.name, ['create']);
        credentialService = jasmine.createSpyObj<CredentialService>(CredentialService.name, allMethodNames(CredentialService));
    });

    let fixture: ComponentFixture<CredentialCreateComponent>;
    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [CredentialCreateComponent],
            providers: [
                {provide: LoadingController, useClass: LoadingControllerFake},
                {provide: NavController, useValue: navController},
                {provide: ToastController, useValue: toastController},
                {provide: CredentialService, useValue: credentialService},
            ],
            imports: [
                IonicModule.forRoot(),
                RouterModule.forRoot([]),
                FormsModule,
            ],
            schemas: [NO_ERRORS_SCHEMA],
        });
        fixture = TestBed.createComponent(CredentialCreateComponent);
    });

    it('should display toast message when Facebook account locked', fakeAsync(() => {
        fixture.componentInstance.provider = Provider.TINDER;
        fixture.componentInstance.facebookEmail = 'example@test.org';
        fixture.componentInstance.facebookPassword = 'aZeRtY';
        fixture.componentInstance.label = 'Sample';

        const toast = spyOnAllFunctions(createHTMLIonToastElement());
        toastController.create.and.returnValue(Promise.resolve(toast));
        credentialService.registerFacebookCredential.and.returnValue(throwError(new HttpErrorResponse({
            error: {
                timestamp: '2020-07-05T13:06:29.823+00:00',
                path: '/credential/facebook',
                status: 423,
                error: 'Locked',
                message: 'Facebook locked your account, because the server IP is not (yet) authorized... Please login to your Facebook account, and authorize the server IP!',
                requestId: 'd752cc9c-6'
            },
            headers: null,
            status: 423,
            statusText: 'Locked',
            url: 'http://localhost:8080/credential/facebook'
        })));

        fixture.componentInstance.onCreate();

        flush(); // wait: ".subscribe()"
        flush(); // wait: ".subscribe()"

        expect(toastController.create).toHaveBeenCalledWith(jasmine.objectContaining({message: 'Facebook locked your account, because the server IP is not (yet) authorized... Please login to your Facebook account, and authorize the server IP!'}));
        expect(toast.present).toHaveBeenCalled();
    }));
});

import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {environment} from '../../environments/environment';
import {noOp} from '../utils';

/** The icon name. */
export enum Provider {
    HAPPN = 'HAPPN',
    ONCE = 'ONCE',
    TINDER = 'TINDER',
}

export interface RegisteredCredential {
    id: number;
    provider: Provider;
    label: string;
    ok: boolean;
}

@Injectable()
export class CredentialService {

    private registeredCredential: BehaviorSubject<RegisteredCredential[]> = null;

    constructor(private client: CredentialClient) {
    }

    public registeredCredentials(): Observable<RegisteredCredential[]> {
        if (this.registeredCredential === null) {
            this.registeredCredential = new BehaviorSubject<RegisteredCredential[]>([]);
            this.refreshRegisteredCredentialsAsync();
        }
        return this.registeredCredential
            .asObservable(); // subscribe only to last value
    }

    public registerFacebookCredential(provider: Provider, facebookEmail: string, facebookPassword: string, label: string): Observable<RegisteredCredential> {
        return this.client.registerFacebookCredential(provider, facebookEmail, facebookPassword, label)
            .pipe(tap(() => this.refreshRegisteredCredentialsAsync()));
    }

    public deleteCredential(credentialId: number): Observable<RegisteredCredential> {
        return this.client.deleteCredential(credentialId)
            .pipe(tap(() => this.refreshRegisteredCredentialsAsync()));
    }

    private refreshRegisteredCredentialsAsync() {
        this.refreshRegisteredCredentials().subscribe(noOp);
    }

    public refreshRegisteredCredentials() {
        return this.client.getRegisteredCredentials()
            .pipe(tap(it => this.registeredCredential.next(it)));
    }

}


@Injectable()
export class CredentialClient {

    constructor(private http: HttpClient) {
    }

    public getRegisteredCredentials(): Observable<RegisteredCredential[]> {
        return this.http.get<RegisteredCredential[]>(environment.apiUrl + '/credential');
    }

    public registerFacebookCredential(provider: Provider, facebookEmail: string, facebookPassword: string, label: string): Observable<RegisteredCredential> {
        const formData = new FormData();
        formData.append('provider', provider);
        formData.append('email', facebookEmail);
        formData.append('password', facebookPassword);
        formData.append('label', label);
        return this.http.post<RegisteredCredential>(environment.apiUrl + '/credential/facebook', formData);
    }

    public deleteCredential(credentialId: number): Observable<RegisteredCredential> {
        return this.http.delete<RegisteredCredential>(environment.apiUrl + `/credential/${encodeURIComponent(credentialId.toString())}`);
    }

}

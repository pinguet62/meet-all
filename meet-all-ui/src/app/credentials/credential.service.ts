import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

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

    constructor(private http: HttpClient) {
    }

    public getRegisteredCredential(): Observable<RegisteredCredential[]> {
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

import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

/** The icon name. */
export enum Provider {
    TINDER = 'TINDER',
    HAPPN = 'HAPPN',
}

export interface RegisteredCredential {
    id: number;
    provider: Provider;
    label: string;
}

@Injectable()
export class CredentialService {

    constructor(private http: HttpClient) {
    }

    public getRegisteredCredential(): Observable<RegisteredCredential[]> {
        return this.http.get<RegisteredCredential[]>(environment.apiUrl + '/credential');
    }

    public registerCredential(provider: Provider, credential: string, label: string): Observable<RegisteredCredential> {
        const formData = new FormData();
        formData.append('provider', provider);
        formData.append('credential', credential);
        formData.append('label', label);
        return this.http.post<RegisteredCredential>(environment.apiUrl + '/credential', formData);
    }

    public deleteCredential(id: number): Observable<RegisteredCredential> {
        return this.http.delete<RegisteredCredential>(environment.apiUrl + `/credential/${encodeURIComponent(id.toString())}`);
    }

}

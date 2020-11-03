import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

// TODO move to shared module
export interface Profile {
    id: string;
    name: string;
    age: number;
    description: string | null;
    avatars: string[];
}

@Injectable()
export class ProfileService {

    constructor(private http: HttpClient) {
    }

    public getProfile(profileId: string): Observable<Profile> {
        return this.http.get<Profile>(environment.apiUrl + `/profile/${encodeURIComponent(profileId)}`);
    }

}

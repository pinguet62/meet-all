import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {environment} from '../../environments/environment';
import {SecurityService} from '../security';

@Injectable()
export class LoginService {

    constructor(private http: HttpClient, private securityService: SecurityService) {
    }

    public static buildFacebookOAuthLoginUrl(currentLocation: Location): string {
        const paramBuilder: HttpParams = new HttpParams()
            .append('client_id', '370320447067710')
            .append('response_type', 'token')
            .append('redirect_uri', `${currentLocation.protocol}//${currentLocation.host}/login/oauth`);
        return `https://www.facebook.com/dialog/oauth?${paramBuilder.toString()}`;
    }

    public static extractOAuthAccessTokenFromUrl(url: string): string | null {
        const fragment: string = url.split('#')[1];
        const paramParser: HttpParams = new HttpParams({fromString: fragment});
        return paramParser.get('access_token');
    }

    public login(facebookAccessToken: string): Observable<any> {
        return this.http.post(environment.apiUrl + '/login', null, {params: {facebook_token: facebookAccessToken}, responseType: 'text'})
            .pipe(tap(it => this.securityService.token = it));
    }

}

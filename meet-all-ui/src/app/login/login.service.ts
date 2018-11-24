import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {environment} from '../../environments/environment';
import {SecurityService} from '../security';

export type Token = string;

@Injectable()
export class LoginService {

    constructor(private http: HttpClient, private securityService: SecurityService) {
    }

    public createAccount(email: string, password: string): Observable<Token> {
        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);
        return this.http.post<Token>(environment.apiUrl + '/user', formData);
    }

    public login(email: string, password: string): Observable<Token> {
        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);
        return this.http.post<Token>(environment.apiUrl + '/login', formData)
            .pipe(tap(it => this.securityService.token = it));
    }

}

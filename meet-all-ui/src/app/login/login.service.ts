import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {SecurityService} from "../security/security.service";

export type Token = string;

@Injectable()
export class LoginService {

    constructor(private http: HttpClient, private securityService: SecurityService) {
    }

    public createAccount(email: string, password: string): Observable<Token> {
        const formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);
        return this.http.post<Token>(environment.apiUrl + '/login', formData, {headers: {'Authorization': this.securityService.token}});
    }

    public login(email: string, password: string): Observable<Token> {
        const formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);
        return this.http.post<Token>(environment.apiUrl + '/login', formData);
    }

}

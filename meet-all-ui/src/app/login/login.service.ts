import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {SecurityService} from "../security/security.service";

@Injectable()
export class LoginService {

    constructor(private http: HttpClient, private securityService: SecurityService) {
    }

    /**
     * @return The authorization token.
     */
    public createAccount(email: string, password: string): Observable<string> {
        const formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);
        return this.http.post<string>(environment.apiUrl + '/login', formData, {headers: {'Authorization': this.securityService.token}});
    }

    /**
     * @return The authorization token.
     */
    public login(email: string, password: string): Observable<string> {
        const formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);
        return this.http.post<string>(environment.apiUrl + '/login', formData);
    }

}

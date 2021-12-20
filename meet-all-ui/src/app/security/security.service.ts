import {Injectable} from '@angular/core';

@Injectable()
export class SecurityService {

    static readonly tokenKey = 'token';

    private internalToken: string | null = null;

    constructor() {
        this.internalToken = localStorage.getItem(SecurityService.tokenKey);
    }

    public get token(): string | null {
        return this.internalToken;
    }

    public set token(value: string | null) {
        if (value == null) {
            localStorage.removeItem(SecurityService.tokenKey);
        } else {
            localStorage.setItem(SecurityService.tokenKey, value);
        }
        this.internalToken = value;
    }

    isLogged() {
        return this.token !== null;
    }

}

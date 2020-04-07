import {Injectable} from '@angular/core';

@Injectable()
export class SecurityService {

    static readonly TOKEN_KEY = 'token';

    private internalToken: string | null = null;

    public get token(): string | null {
        return this.internalToken;
    }

    public set token(value: string | null) {
        if (value == null) {
            localStorage.removeItem(SecurityService.TOKEN_KEY);
        } else {
            localStorage.setItem(SecurityService.TOKEN_KEY, value);
        }
        this.internalToken = value;
    }

    constructor() {
        this.internalToken = localStorage.getItem(SecurityService.TOKEN_KEY);
    }

    isLogged() {
        return this.token !== null;
    }

}

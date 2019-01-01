import {Injectable} from '@angular/core';

@Injectable()
export class SecurityService {

    static readonly TOKEN_KEY = 'token';

    private _token: string | null = null;

    public get token(): string | null {
        return this._token;
    }

    public set token(value: string | null) {
        if (value == null) {
            localStorage.removeItem(SecurityService.TOKEN_KEY);
        } else {
            localStorage.setItem(SecurityService.TOKEN_KEY, value);
        }
        this._token = value;
    }

    constructor() {
        this._token = localStorage.getItem(SecurityService.TOKEN_KEY);
    }

    isLogged() {
        return this.token !== null;
    }

}

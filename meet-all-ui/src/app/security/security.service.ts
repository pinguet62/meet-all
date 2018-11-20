import {Injectable} from "@angular/core";

@Injectable()
export class SecurityService {

    token: string | null = null;

    isLogged() {
        return this.token !== null;
    }

}

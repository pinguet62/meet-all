import {LoginService} from './login.service';
import {buildLocation} from '../test-utils';

describe('login/login.service', () => {

    it('buildFacebookOAuthLoginUrl', () => {
        const currentLocation: Location = buildLocation('https://example.org/sub/path');
        expect(LoginService.buildFacebookOAuthLoginUrl(currentLocation)).toEqual('https://www.facebook.com/dialog/oauth?client_id=370320447067710&response_type=token&redirect_uri=https://example.org/login/oauth');
    });

    it('extractOAuthAccessTokenFromUrl', () => {
        expect(LoginService.extractOAuthAccessTokenFromUrl('https://localhost:4200/login/oauth')).toBeNull();
        expect(LoginService.extractOAuthAccessTokenFromUrl('https://localhost:4200/login/oauth?#access_token=expected&expires_in=5731&reauthorize_required_in=7776000&data_access_expiration_time=1553444669')).toEqual('expected');
    });

});

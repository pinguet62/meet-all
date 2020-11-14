import {openApp} from '../page-object/app.po';

describe('login', () => {
    it('redirect to login page when not logged', () => {
        const page = openApp();
        expect(page.then(it => it.getToolbarTitle())).toContain('Login');
    });

    it('redirect to app when logged', () => {
        const page = openApp({
            localStorage: {
                token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIn0.WYWTztLu8_n1J17zg3mlQzN16rZUc8h9cSCB8ob1DyI'
            }
        });
        expect(page.then(it => it.getToolbarTitle())).toContain('Conversations');
    });
});

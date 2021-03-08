import * as nock from 'nock';
import {openApp} from '../page-object/app.po';
import {ConversationPage} from '../page-object/conversations.po';

const LOGGED = {
    localStorage: {
        token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIn0.WYWTztLu8_n1J17zg3mlQzN16rZUc8h9cSCB8ob1DyI'
    }
};

describe('conversations', () => {
    afterEach(() => nock.cleanAll());

    it('display list', async () => {
        nock('http://localhost:9999').get(`/conversations`).reply(200, [
            {
                id: '91#convTinder12',
                profile: {
                    id: '91#profTinder12',
                    name: 'Noémie',
                    avatar: 'https%3A%2F%2Fopenjdk.java.net%2Fimages%2Fnanoduke.ico'
                },
                date: '2020-11-13T19:43:54.000Z',
                lastMessage: null
            },
            {
                id: '92#convHappn21',
                profile: {
                    id: '92#profHappn21',
                    name: 'Marion',
                    avatar: 'http%3A%2F%2Fgoogle.fr%2Ffoo%2Fbar%3Fparam%3Dvalue'
                },
                date: '2020-11-13T12:00:32.000Z',
                lastMessage: null
            },
            {
                id: '91#profTinder11',
                profile: {
                    id: '5faff1235d45f050f856a6db#3d2efb27-2d3c-496d-932c-ab4e71697caa',
                    name: 'Louise',
                    avatar: 'https%3A%2F%2Fangular.io%2Fassets%2Fimages%2Flogos%2Fangular%2Fangular.svg'
                },
                date: '2020-11-07T17:22:54.000Z',
                lastMessage: 'Coucou'
            }]);
        const page = openApp<ConversationPage>(LOGGED);
        expect(await page.then(it => it.getConversations()).then(it => it.length)).toEqual(3);
    });
});

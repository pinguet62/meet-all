import {browser} from 'protractor';
import {ConversationPage} from './conversations.po';
import {LoginPage} from './login.po';
import {promise} from 'selenium-webdriver';
import {setItem} from '../localStorage';

export function openApp(params: { localStorage?: { [key: string]: string } } = {}): promise.Promise<LoginPage | ConversationPage> {
    return browser.get('/')
        .then(() => promise.all(
            Object.entries(params.localStorage || {})
                .map(([key, value]) => setItem(key, value)))
        )
        .then(() => browser.get('/')) // refresh app with initialized localStorage
        .then(() => browser.getCurrentUrl())
        .then(currentUrl => currentUrl.endsWith('/login') ? new LoginPage() : new ConversationPage());
}

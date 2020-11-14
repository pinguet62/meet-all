import {browser} from 'protractor';
import {promise} from 'selenium-webdriver';
import {ConversationPage} from './conversations.po';
import {LoginPage} from './login.po';
import {clear, setItem} from '../localStorage';

export function openApp<T extends LoginPage | ConversationPage = (LoginPage | ConversationPage)>(params: { localStorage?: { [key: string]: string } } = {}): promise.Promise<T> {
    return browser.get('/') // create browser before initialisation // TODO create automatically browser before all tests
        .then(() => clear()) // TODO create automatically browser before all tests
        .then(() => promise.all(
            Object.entries(params.localStorage || {})
                .map(([key, value]) => setItem(key, value))))
        .then(() => browser.get('/')) // refresh app with initialized localStorage
        .then(() => browser.getCurrentUrl())
        .then(currentUrl => currentUrl.endsWith('/login') ? new LoginPage() as T : new ConversationPage() as T);
}

import {browser} from 'protractor';

export function removeItem(key: string) {
    return browser.executeScript(`return window.localStorage.removeItem("${key}");`);
}

export function setItem(key: string, value: string) {
    return browser.executeScript(`return window.localStorage.setItem("${key}", "${value}");`);
}

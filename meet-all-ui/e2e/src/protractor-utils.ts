import {browser, element, ExpectedConditions, Locator} from 'protractor';

export function waitPresenceOfAndFindElements(locator: Locator) {
    return browser.wait(ExpectedConditions.presenceOf(element(locator)))
        .then(() => browser.findElements(locator));
}

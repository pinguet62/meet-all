import {browser, By} from 'protractor';

export class ConversationPage {
    getPageTitle() {
        return browser.getTitle();
    }

    getToolbarTitle() {
        return browser.findElement(By.css('ion-toolbar > ion-title')).getText();
    }
}

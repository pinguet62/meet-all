import {Component} from '@angular/core';

@Component({
    selector: 'app-tabs',
    template: `
        <ion-tabs>

            <ion-tab tab="conversations">
                <ion-router-outlet name="conversations"></ion-router-outlet>
            </ion-tab>
            <ion-tab tab="credentials">
                <ion-router-outlet name="credentials"></ion-router-outlet>
            </ion-tab>

            <ion-tab-bar slot="bottom">

                <ion-tab-button tab="conversations" href="/tabs/(conversations:conversations)">
                    <ion-icon name="chatboxes"></ion-icon>
                    <ion-label>Conversations</ion-label>
                </ion-tab-button>

                <ion-tab-button tab="credentials" href="/tabs/(credentials:credentials)">
                    <ion-icon name="key"></ion-icon>
                    <ion-label>Credentials</ion-label>
                </ion-tab-button>

            </ion-tab-bar>

        </ion-tabs>
    `,
})
export class TabsPage {
}

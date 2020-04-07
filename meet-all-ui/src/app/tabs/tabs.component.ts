import {Component} from '@angular/core';

@Component({
    selector: 'app-tabs',
    template: `
        <ion-tabs>

            <ion-tab-bar slot="bottom">

                <ion-tab-button tab="proposals">
                    <ion-icon name="heart"></ion-icon>
                    <ion-label>Proposals</ion-label>
                </ion-tab-button>

                <ion-tab-button tab="conversations">
                    <ion-icon name="chatbubbles"></ion-icon>
                    <ion-label>Conversations</ion-label>
                </ion-tab-button>

                <ion-tab-button tab="credentials">
                    <ion-icon name="key"></ion-icon>
                    <ion-label>Credentials</ion-label>
                </ion-tab-button>

            </ion-tab-bar>

        </ion-tabs>
    `,
})
export class TabsComponent {
}

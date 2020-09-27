import {Component} from '@angular/core';
import {SwUpdate} from '@angular/service-worker';
import {AlertController} from '@ionic/angular';

@Component({
    selector: 'app-version',
    template: ``,
})
export class VersionComponent {
    constructor(updates: SwUpdate, alertController: AlertController) {
        updates.available.subscribe(async event => {
            const alert = await alertController.create({
                header: 'New version!',
                subHeader: 'A new version is now available.',
                message: 'Please reload application to take advantage of new features and fixes.',
                buttons: [
                    {
                        text: 'OK',
                        handler: () => updates.activateUpdate().then(() =>
                            // /!\ after "activateUpdate()"
                            document.location.reload())
                    }]
            });
            await alert.present();
        });
    }
}
